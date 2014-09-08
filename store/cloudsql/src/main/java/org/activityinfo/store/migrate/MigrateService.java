package org.activityinfo.store.migrate;

import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.sun.jersey.api.core.InjectParam;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.service.DeploymentConfiguration;
import org.activityinfo.store.cloudsql.ConnectionProvider;
import org.activityinfo.store.hrd.HrdResourceStore;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/service/migrate")
public class MigrateService {


    @GET
    @Path("database/{id}")
    public Response startMigration(@InjectParam AuthenticatedUser user,
                                   @PathParam("id") int databaseId) {

        QueueFactory.getDefaultQueue().add(TaskOptions.Builder.withUrl("/service/migrate/run")
            .param("databaseId", Integer.toString(databaseId))
            .param("userId", Integer.toString(user.getId())));

        return Response.ok("Task started").build();
    }



    @POST
    @Path("run")
    public void runMigration(@InjectParam AuthenticatedUser user,
                             @InjectParam DeploymentConfiguration config,
                             @FormParam("databaseId") int databaseId,
                             @FormParam("userId") int userId) throws Exception {
        new MigrateDatabaseTask(config, new AuthenticatedUser("", userId, "")).migrate(databaseId);
    }

}
