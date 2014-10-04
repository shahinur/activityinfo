package org.activityinfo.store.tasks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.sun.jersey.api.core.InjectParam;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.json.ObjectMapperFactory;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.service.tasks.*;
import org.activityinfo.store.tasks.export.ExportFormExecutor;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/service/tasks")
public class HrdUserTaskService implements UserTaskService {

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

    private final TaskContextProvider contextProvider;

    @Inject
    public HrdUserTaskService(TaskContextProvider contextProvider) {
        this.contextProvider = contextProvider;
        executors.put(ExportFormTaskModelClass.CLASS_ID, (TaskExecutor)new ExportFormExecutor());
    }

    @Override
    public UserTask startTask(AuthenticatedUser user, String description) {
        return persistTask(user, Resources.generateId().asString(), null, description);
    }



    @POST
    @Path("{taskId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public UserTask start(@InjectParam AuthenticatedUser user, @PathParam("taskId") String taskId, Record taskModelRecord) {

        UserTask task = createTask(user, taskId, taskModelRecord);


        // Kick off task
        QueueFactory.getDefaultQueue().add(null, TaskOptions.Builder
            .withUrl("/service/tasks/run")
            .param("userId", Integer.toString(user.getId()))
            .param("taskId", task.getId()));

        return task;
    }

    @VisibleForTesting
    UserTask createTask(AuthenticatedUser user, String taskId, Record taskModelRecord) {
        TaskExecutor<TaskModel> executor = executors.get(taskModelRecord.getClassId());
        if(executor == null) {
            throw new WebApplicationException(Response
                .status(Response.Status.BAD_REQUEST)
                .entity("Invalid task model class " + taskModelRecord.getClassId())
                .build());
        }

        // Deserialize task model
        TaskModel taskModel = executor.getModelClass().toBean(taskModelRecord);

        // Create a new task record
        try {
            String describe;
            try {
                describe = executor.describe(taskModel);
            } catch (IllegalArgumentException e) {
                LOGGER.log(Level.SEVERE, "Invalid task model", e);
                throw new WebApplicationException(Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage()).build());
            }
            return persistTask(user, taskId, taskModelRecord, describe);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception thrown while describing task", e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    private UserTask persistTask(AuthenticatedUser user, String taskId, Record taskModelRecord, String description) {
        Date startTime = new Date();

        Entity entity = new Entity(taskKey(user, taskId));
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
        datastore.put(null, entity);

        UserTask task = new UserTask();
        task.setId(taskId);
        task.setStatus(UserTaskStatus.RUNNING);
        task.setTimeStarted(startTime.getTime());
        task.setTaskModel(taskModelRecord);

        return task;
    }


    @Override
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
            TaskContext context = contextProvider.create(user);
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
            return fromEntity(datastore.get(null, taskKey(user, taskId)));
        } catch (EntityNotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }


    private Key taskKey(AuthenticatedUser user, String taskId) {
        return KeyFactory.createKey(parentKey(user), KIND, taskId);
    }

    private Key parentKey(AuthenticatedUser user) {
        return KeyFactory.createKey(ANCESTOR_KIND, user.getId());
    }

    private UserTask fromEntity(Entity entity) {
        UserTask task = new UserTask();
        task.setId(entity.getKey().getName());
        task.setStatus(UserTaskStatus.valueOf((String) entity.getProperty(STATUS_PROPERTY)));
        task.setTimeStarted(((Date) entity.getProperty(START_TIME_PROPERTY)).getTime());

        Text modelJson = (Text) entity.getProperty(MODEL_PROPERTY);
        if(modelJson != null) {
            try {
                task.setTaskModel(objectMapper.readValue(modelJson.getValue(), Record.class));
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Exception deserializing model json: " + modelJson.getValue(), e);
                throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
            }
        }
        return task;
    }

}
