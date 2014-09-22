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

    private static final String KIND = "UserTask";

    private static final String ANCESTOR_KIND = "User";

    private static final long TWO_DAYS = 2L * 24L * 60L * 60L * 1000L;

    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    @Override
    public UserTask startTask(AuthenticatedUser user, String description) {

        Date startTime = new Date();

        Entity entity = new Entity(KIND, parentKey(user));
        entity.setProperty("startTime", startTime);
        entity.setUnindexedProperty("description", description);
        entity.setUnindexedProperty("status", UserTaskStatus.RUNNING.name());
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
            entity.setUnindexedProperty("status", status.name());
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
            .addSort("startTime", Query.SortDirection.DESCENDING);

        long now = System.currentTimeMillis();

        List<UserTask> tasks = Lists.newArrayList();
        for(Entity entity : datastore.prepare(query).asIterable()) {
            Date timeStarted = (Date) entity.getProperty("startTime");
            if( (now - timeStarted.getTime()) > TWO_DAYS ) {
                break;
            }
            UserTask task = new UserTask();
            task.setId(Long.toHexString(entity.getKey().getId()));
            task.setStatus(UserTaskStatus.valueOf((String) entity.getProperty("status")));
            task.setDescription((String)entity.getProperty("description"));
            task.setTimeStarted((Long)entity.getProperty("timeStarted"));
            tasks.add(task);
        }
        return tasks;
    }

    private Key parentKey(AuthenticatedUser user) {
        return KeyFactory.createKey(ANCESTOR_KIND, user.getId());
    }
}
