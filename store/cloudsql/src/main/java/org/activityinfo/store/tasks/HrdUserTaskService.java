package org.activityinfo.store.tasks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.sun.jersey.api.core.InjectParam;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.json.ObjectMapperFactory;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.service.tasks.TaskContext;
import org.activityinfo.service.tasks.TaskExecutor;
import org.activityinfo.service.tasks.TaskModel;
import org.activityinfo.service.tasks.UserTask;
import org.activityinfo.service.tasks.UserTaskService;
import org.activityinfo.service.tasks.UserTaskStatus;
import org.activityinfo.store.hrd.HrdResourceStore;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/service/tasks")
public class HrdUserTaskService implements UserTaskService {

    public static final String TASK_NAME_HEADER = "X-AppEngine-TaskName";

    private static final Logger LOGGER = Logger.getLogger(HrdUserTaskService.class.getName());

    private static final String START_TIME_PROPERTY = "startTime";
    private static final String DESCRIPTION_PROPERTY = "description";
    private static final String STATUS_PROPERTY = "status";
    private static final String MODEL_PROPERTY = "model";

    private static final String KIND = "UserTask";

    private static final String ANCESTOR_KIND = "User";

    private static final long TWO_DAYS = 2L * 24L * 60L * 60L * 1000L;

    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private ObjectMapper objectMapper = ObjectMapperFactory.get();

    private Map<ResourceId, TaskExecutor<TaskModel>> executors = Maps.newHashMap();

    private final HrdResourceStore store;

    @Inject
    public HrdUserTaskService(HrdResourceStore store) {
        this.store = store;
        //executors.put(ExportFormTaskClass.CLASS_ID, (TaskExecutor)new ExportFormExecutor());
    }

    @Override
    public UserTask startTask(AuthenticatedUser user, String description) {
        return persistTask(user, null, description);
    }

    private UserTask persistTask(AuthenticatedUser user, Record taskModelRecord, String description) {
        Date startTime = new Date();

        Entity entity = new Entity(KIND, parentKey(user));
        entity.setProperty(START_TIME_PROPERTY, startTime);
        entity.setUnindexedProperty(DESCRIPTION_PROPERTY, description);
        entity.setUnindexedProperty(STATUS_PROPERTY, UserTaskStatus.RUNNING.name());
        if(taskModelRecord != null) {
            try {
                entity.setUnindexedProperty(MODEL_PROPERTY, new Text(objectMapper.writeValueAsString(taskModelRecord)));
            } catch (JsonProcessingException e) {
                LOGGER.log(Level.SEVERE, "Exception writing Record model to json", e);
                throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
            }
        }
        Key taskKey = datastore.put(null, entity);

        UserTask task = new UserTask();
        task.setId(Long.toHexString(taskKey.getId()));
        task.setDescription(description);
        task.setStatus(UserTaskStatus.RUNNING);
        task.setTimeStarted(startTime.getTime());

        return task;
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public UserTask start(@InjectParam AuthenticatedUser user, Record taskModelRecord) {

        TaskExecutor<TaskModel> executor = executors.get(taskModelRecord.getClassId());
        if(executor == null) {
            throw new WebApplicationException(Response
                .status(Response.Status.BAD_REQUEST)
                .entity("Invalid task model class " + taskModelRecord.getClassId())
                .build());
        }

        // Deserialize task model
        TaskModel taskModel = executor.getModelClass().toBean(taskModelRecord);

        // Create a context for executing this task
        TaskContext context = new HrdTaskContext(store, user);

        // Create a new task record
        UserTask task = null;
        try {
            task = persistTask(user, taskModelRecord, executor.describe(context, taskModel));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception thrown while describing task", e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        // Kick off task
        QueueFactory.getDefaultQueue().add(TaskOptions.Builder
            .withUrl("/service/tasks/run")
            .param("userId", Integer.toString(user.getId()))
            .param("taskId", task.getId()));

        return task;
    }

    @POST
    @Path("run")
    public Response run(@HeaderParam(TASK_NAME_HEADER) String taskName,
                        @FormParam("userId") int userId,
                        @FormParam("taskId") String taskId) {

        LOGGER.info(String.format("Task id %s for user %d starting.", taskId, userId));

        AuthenticatedUser user = new AuthenticatedUser(userId);

        // Retrieve task from datastore

        Entity entity;
        Key taskKey = taskKey(user, taskId);

        try {
            entity = datastore.get(taskKey);

        } catch (EntityNotFoundException e) {
            throw abortTask(e, "Entity with %s does not exist", taskKey.toString());
        }

        // Abort if the task has already completed
        UserTask task = fromEntity(entity);
        if(task.getStatus() != UserTaskStatus.RUNNING) {
            LOGGER.info(String.format("Task status is %s, exiting.", task.getStatus().name()));
            return Response.ok().build();
        }

        // Retrieve the task model
        Text modelJson;
        Record modelRecord;
        try {
            modelJson = (Text) entity.getProperty(MODEL_PROPERTY);
            modelRecord = objectMapper.readValue(modelJson.getValue(), Record.class);

        } catch(Exception e) {
            throw abortTask(e, "Failed to parse task model, aborting.");
        }

        // Obtain the executor
        TaskExecutor<TaskModel> executor = executors.get(modelRecord.getClassId());
        if(executor == null) {
            throw abortTask(null, "No executor for model class " + modelRecord.getClassId());
        }

        TaskModel taskModel = executor.getModelClass().toBean(modelRecord);

        // Kick off the task
        try {
            LOGGER.info("Starting task");
            TaskContext context = new HrdTaskContext(store, user);
            executor.execute(context, taskModel);

            // mark task as complete
            LOGGER.info("Task completed successfully");
            updateTask(user, taskId, UserTaskStatus.COMPLETE);

        } catch (NoClassDefFoundError | Exception e) {

            LOGGER.log(Level.SEVERE, "Exception thrown during task execution", e);

            updateTask(user, taskId, UserTaskStatus.FAILED);
        }

        return Response.ok().build();
    }

    private RuntimeException abortTask(Throwable cause, String message, Object... args) {

        LOGGER.log(Level.SEVERE, String.format(message, args), cause);
        // We have to return a 2xx code, otherwise AppEngine will continue to retry
        // task indefinitely.
        throw new WebApplicationException(Response.ok().build());
    }

    @Override
    public void updateTask(AuthenticatedUser user, String taskId, UserTaskStatus status) {

        Key taskKey = taskKey(user, taskId);
        Transaction tx = datastore.beginTransaction();

        try {
            Entity entity = datastore.get(tx, taskKey);
            entity.setUnindexedProperty(STATUS_PROPERTY, status.name());
            datastore.put(tx, entity);
            tx.commit();
        } catch(Exception e) {
            throw new IllegalStateException("UserTask " + taskKey + " does not exist.");
        }
    }

    @GET
    @Override
    @Produces(MediaType.APPLICATION_JSON)
    public List<UserTask> getUserTasks(@InjectParam AuthenticatedUser user) {

        Query query = new Query(KIND, parentKey(user))
            .addSort(START_TIME_PROPERTY, Query.SortDirection.DESCENDING);

        long now = System.currentTimeMillis();

        List<UserTask> tasks = Lists.newArrayList();
        for(Entity entity : datastore.prepare(query).asIterable()) {
            Date timeStarted = (Date) entity.getProperty(START_TIME_PROPERTY);
            if( (now - timeStarted.getTime()) > TWO_DAYS ) {
                break;
            }
            tasks.add(fromEntity(entity));
        }
        return tasks;
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public UserTask getUserTask(@InjectParam AuthenticatedUser user, @PathParam("id") String taskId) {
        try {
            return fromEntity(datastore.get(taskKey(user, taskId)));
        } catch (EntityNotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }


    private Key taskKey(AuthenticatedUser user, String taskId) {
        return KeyFactory.createKey(parentKey(user), KIND, Long.parseLong(taskId, 16));
    }

    private Key parentKey(AuthenticatedUser user) {
        return KeyFactory.createKey(ANCESTOR_KIND, user.getId());
    }

    private UserTask fromEntity(Entity entity) {
        UserTask task = new UserTask();
        task.setId(Long.toHexString(entity.getKey().getId()));
        task.setStatus(UserTaskStatus.valueOf((String) entity.getProperty(STATUS_PROPERTY)));
        task.setDescription((String)entity.getProperty(DESCRIPTION_PROPERTY));
        task.setTimeStarted(((Date)entity.getProperty(START_TIME_PROPERTY)).getTime());
        return task;
    }

}
