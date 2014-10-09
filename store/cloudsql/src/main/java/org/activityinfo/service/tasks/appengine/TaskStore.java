package org.activityinfo.service.tasks.appengine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.datastore.*;
import com.google.common.collect.Lists;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.json.ObjectMapperFactory;
import org.activityinfo.model.record.Record;
import org.activityinfo.service.tasks.UserTask;
import org.activityinfo.service.tasks.UserTaskStatus;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskStore {

    private static final Logger LOGGER = Logger.getLogger(TaskStore.class.getName());

    private final DatastoreService datastore;
    private ObjectMapper objectMapper = ObjectMapperFactory.get();

    public static final String KIND = "UserTask";

    private static final String ANCESTOR_KIND = "User";

    public static final String START_TIME_PROPERTY = "startTime";
    private static final String DESCRIPTION_PROPERTY = "description";
    private static final String STATUS_PROPERTY = "status";
    private static final String MODEL_PROPERTY = "model";


    public TaskStore() {
        datastore = DatastoreServiceFactory.getDatastoreService();
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


    public void put(AuthenticatedUser user, UserTask task) {
        Date startTime = new Date();

        Entity entity = new Entity(taskKey(user, task.getId()));
        entity.setProperty(START_TIME_PROPERTY, startTime);
     //   entity.setUnindexedProperty(DESCRIPTION_PROPERTY, task.get);
        entity.setUnindexedProperty(STATUS_PROPERTY, UserTaskStatus.RUNNING.name());
        if(task.getTaskModel() != null) {
            try {
                entity.setUnindexedProperty(MODEL_PROPERTY, new Text(objectMapper.writeValueAsString(task.getTaskModel())));
            } catch (JsonProcessingException e) {
                LOGGER.log(Level.SEVERE, "Exception writing Record model to json", e);
                throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
            }
        }
        datastore.put(null, entity);
    }


    public void updateTask(AuthenticatedUser user, String taskId, UserTaskStatus status) {

        Key taskKey = taskKey(user, taskId);
        Transaction tx = datastore.beginTransaction();

        try {
            Entity entity = datastore.get(tx, taskKey);
            entity.setUnindexedProperty(STATUS_PROPERTY, status.name());
            datastore.put(tx, entity);
            tx.commit();

        } catch(Exception e) {
            tx.rollback();
            throw new IllegalStateException("UserTask " + taskKey + " does not exist.");
        }
    }

    public List<UserTask> queryRecent(AuthenticatedUser user) {

        Query query = new Query(KIND, parentKey(user))
            .addSort(START_TIME_PROPERTY, Query.SortDirection.DESCENDING);

        long now = System.currentTimeMillis();

        List<UserTask> tasks = Lists.newArrayList();
        for(Entity entity : datastore.prepare(query).asIterable()) {
            Date timeStarted = (Date) entity.getProperty(START_TIME_PROPERTY);
            if( (now - timeStarted.getTime()) > TimeUnit.DAYS.toMillis(2)) {
                break;
            }
            tasks.add(fromEntity(entity));
        }
        return tasks;
    }

    public UserTask get(AuthenticatedUser user, String taskId) throws EntityNotFoundException {
        return fromEntity(datastore.get(null, taskKey(user, taskId)));
    }
}
