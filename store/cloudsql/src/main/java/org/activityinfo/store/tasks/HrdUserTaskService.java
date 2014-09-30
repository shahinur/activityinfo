package org.activityinfo.store.tasks;

import com.google.appengine.api.datastore.*;
import com.google.common.collect.Lists;
import com.sun.jersey.api.core.InjectParam;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.service.tasks.UserTask;
import org.activityinfo.service.tasks.UserTaskService;
import org.activityinfo.service.tasks.UserTaskStatus;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.List;

@Path("/service/tasks")
public class HrdUserTaskService implements UserTaskService {

    private static final String START_TIME_PROPERTY = "startTime";
    private static final String DESCRIPTION_PROPERTY = "description";
    private static final String STATUS_PROPERTY = "status";

    private static final String KIND = "UserTask";

    private static final String ANCESTOR_KIND = "User";

    private static final long TWO_DAYS = 2L * 24L * 60L * 60L * 1000L;

    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    @Override
    public UserTask startTask(AuthenticatedUser user, String description) {

        Date startTime = new Date();

        Entity entity = new Entity(KIND, parentKey(user));
        entity.setProperty(START_TIME_PROPERTY, startTime);
        entity.setUnindexedProperty(DESCRIPTION_PROPERTY, description);
        entity.setUnindexedProperty(STATUS_PROPERTY, UserTaskStatus.RUNNING.name());
        Key taskKey = datastore.put(null, entity);

        UserTask task = new UserTask();
        task.setId(Long.toHexString(taskKey.getId()));
        task.setDescription(description);
        task.setStatus(UserTaskStatus.RUNNING);
        task.setTimeStarted(startTime.getTime());

        return task;
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

    private Key taskKey(AuthenticatedUser user, String taskId) {
        return KeyFactory.createKey(parentKey(user), KIND, Long.parseLong(taskId, 16));
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
            UserTask task = new UserTask();
            task.setId(Long.toHexString(entity.getKey().getId()));
            task.setStatus(UserTaskStatus.valueOf((String) entity.getProperty(STATUS_PROPERTY)));
            task.setDescription((String)entity.getProperty(DESCRIPTION_PROPERTY));
            task.setTimeStarted(((Date)entity.getProperty(START_TIME_PROPERTY)).getTime());
            tasks.add(task);
        }
        return tasks;
    }

    private Key parentKey(AuthenticatedUser user) {
        return KeyFactory.createKey(ANCESTOR_KIND, user.getId());
    }
}
