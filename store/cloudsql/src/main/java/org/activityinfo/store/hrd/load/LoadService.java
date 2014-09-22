package org.activityinfo.store.hrd.load;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.sun.jersey.api.core.InjectParam;
import org.activityinfo.io.load.excel.ExcelFormImportReader;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.store.hrd.Authorization;
import org.activityinfo.store.hrd.entity.ReadTransaction;
import org.activityinfo.store.hrd.entity.Workspace;
import org.activityinfo.store.hrd.entity.WorkspaceTransaction;
import org.activityinfo.store.hrd.index.WorkspaceLookup;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URL;

@Path("/service/load")
public class LoadService {


    private final WorkspaceLookup workspaceLookup = new WorkspaceLookup();
    private final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response startImport(@InjectParam AuthenticatedUser user,
                                @FormParam("ownerId") ResourceId ownerId,
                                @FormParam("url") URL dataFileUrl) {

        if(ownerId == null) {
            throw new WebApplicationException(Response
                .status(Response.Status.BAD_REQUEST)
                .entity("ownerId parameter is required").build());
        }
        if(dataFileUrl == null) {
            throw new WebApplicationException(Response
                .status(Response.Status.BAD_REQUEST)
                .entity("url parameter is required").build());
        }

        Workspace workspace = workspaceLookup.lookup(ownerId);

        try(WorkspaceTransaction tx = new ReadTransaction(workspace, datastoreService, user)) {
            Authorization authorization = new Authorization(user, ownerId, tx);
            authorization.assertCanEdit();

            TaskHandle taskHandle = QueueFactory.getDefaultQueue().add(TaskOptions.Builder
                .withUrl("/service/load/run")
                .param("url", dataFileUrl.toExternalForm())
                .param("userId", Long.toString(user.getId()))
                .param("workspaceId", workspace.getWorkspaceId().asString())
                .param("ownerId", ownerId.asString()));

            return Response.ok(Response.Status.CREATED).build();
        }
    }

    @POST
    @Path("run")
    public Response runImport(@FormParam("url") String dataFileUrl,
                              @FormParam("userId") long userId,
                              @FormParam("workspaceId") ResourceId workspaceId,
                              @FormParam("ownerId") ResourceId ownerId) throws IOException {

        BulkLoader loader = new BulkLoader();
        loader.setUser(new AuthenticatedUser("", (int) userId, ""));
        loader.setWorkspaceId(workspaceId);
        loader.setSource(new UrlByteSource(dataFileUrl));
        loader.setOwnerId(ownerId);
        loader.setReader(new ExcelFormImportReader());
        loader.run();

        return Response.ok().build();
    }
}
