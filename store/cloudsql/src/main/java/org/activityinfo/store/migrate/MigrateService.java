package org.activityinfo.store.migrate;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.inject.Inject;
import com.sun.jersey.api.core.InjectParam;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.service.DeploymentConfiguration;
import org.activityinfo.store.hrd.entity.Workspace;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Iterator;

@Path("/service/migrate")
public class MigrateService {

    private DeploymentConfiguration config;

    @Inject
    public MigrateService(DeploymentConfiguration config) {
        this.config = config;
    }

    @GET
    @Path("database/{id}")
    public Response startMigration(@InjectParam AuthenticatedUser user,
                                   @PathParam("id") int databaseId) {

        QueueFactory.getQueue("migrate").add(TaskOptions.Builder.withUrl("/service/migrate/run")
            .param("databaseId", Integer.toString(databaseId))
            .param("userId", Integer.toString(user.getId())));

        return Response.ok("Task started").build();
    }

    @POST
    @Path("run")
    public void runMigration(@InjectParam AuthenticatedUser user,
                             @FormParam("databaseId") int databaseId,
                             @FormParam("userId") int userId) throws Exception {

        UserService userService = UserServiceFactory.getUserService();
        if(!userService.isUserAdmin()) {
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }

        new MigrateDatabaseTask(config, new AuthenticatedUser("", userId, "")).migrate(databaseId);
    }

    @POST
    @Path("cleanup")
    public void cleanupFailedMigration(ResourceId workspaceId) throws Exception {

        UserService userService = UserServiceFactory.getUserService();
        if(!userService.isUserAdmin()) {
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Workspace workspace = new Workspace(workspaceId);

        Query query = new Query(workspace.getRootKey()).setKeysOnly();
        Iterator<Entity> iterator = datastore.prepare(null, query).asIterator();
        while(iterator.hasNext()) {
            datastore.delete((Transaction)null, iterator.next().getKey());
        }
    }
}
