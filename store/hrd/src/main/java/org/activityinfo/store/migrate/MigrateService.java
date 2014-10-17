package org.activityinfo.store.migrate;

import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.sun.jersey.api.core.InjectParam;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.service.DeploymentConfiguration;
import org.activityinfo.store.hrd.HrdResourceStore;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/service/migrate")
public class MigrateService {

    public static final String TASK_NAME_HEADER = "X-AppEngine-TaskName";

    private final HrdResourceStore store;
    private DeploymentConfiguration config;

    @Inject
    public MigrateService(HrdResourceStore store, DeploymentConfiguration config) {
        this.store = store;
        this.config = config;
    }

    @GET
    @Path("database/{id}")
    public Response startMigration(@InjectParam AuthenticatedUser user,
                                   @PathParam("id") int databaseId) {

        QueueFactory.getQueue("migrate").add(TaskOptions.Builder.withUrl("/service/migrate/tasks/migrateDatabase")
            .param("databaseId", Integer.toString(databaseId))
            .param("userId", Integer.toString(user.getId())));

        return Response.ok("Task started").build();
    }

    @GET
    @Path("resources")
    public Response startResourcesMigration(@InjectParam AuthenticatedUser user,
                                   @QueryParam("databaseId") int databaseId) {

        QueueFactory.getQueue("migrate").add(TaskOptions.Builder.withUrl("/service/migrate/tasks/migrateResources")
            .param("databaseId", Integer.toString(databaseId)));

        return Response.ok("Task started").build();
    }

    @POST
    @Path("tasks/migrateDatabase")
    public void runMigration(@HeaderParam(TASK_NAME_HEADER) String taskName,
                             @FormParam("databaseId") int databaseId,
                             @FormParam("userId") int userId) throws Exception {

        assertValidTaskInvocation(taskName);

        new MigrateDatabaseTask(store, config, new AuthenticatedUser(userId)).migrate(databaseId);
    }


    /**
     * Verify that this task handler is being invoked by the AppEngine task system and not
     * by someone randomly.
     */
    private void assertValidTaskInvocation(String taskNameHeader) {
        // Verify that random people are not invoking tasks
        if(Strings.isNullOrEmpty(taskNameHeader)) {
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }
    }

}
