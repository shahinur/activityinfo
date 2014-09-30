package org.activityinfo.store.load;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.sun.jersey.api.core.InjectParam;
import org.activityinfo.io.load.excel.ExcelFormImportReader;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.service.blob.BlobFieldStorageService;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.tasks.UserTask;
import org.activityinfo.service.tasks.UserTaskService;
import org.activityinfo.service.tasks.UserTaskStatus;
import org.activityinfo.store.hrd.Authorization;
import org.activityinfo.store.hrd.entity.ReadTransaction;
import org.activityinfo.store.hrd.entity.Workspace;
import org.activityinfo.store.hrd.entity.WorkspaceTransaction;
import org.activityinfo.store.hrd.index.WorkspaceLookup;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/service/load")
public class LoadService {

    private static final Logger LOGGER = Logger.getLogger(LoadService.class.getName());

    private final WorkspaceLookup workspaceLookup = new WorkspaceLookup();
    private final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

    private final UserTaskService taskService;
    private final BlobFieldStorageService blobService;

    @Inject
    public LoadService(UserTaskService taskService, BlobFieldStorageService blobService) {
        this.taskService = taskService;
        this.blobService = blobService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public UserTask startImport(@InjectParam AuthenticatedUser user,
                                @FormParam("ownerId") ResourceId ownerId,
                                @FormParam("blobId") BlobId blobId) {

        if(ownerId == null) {
            throw new WebApplicationException(Response
                .status(Response.Status.BAD_REQUEST)
                .entity("ownerId parameter is required").build());
        }
        if(blobId == null) {
            throw new WebApplicationException(Response
                .status(Response.Status.BAD_REQUEST)
                .entity("url parameter is required").build());
        }

        Workspace workspace = workspaceLookup.lookup(ownerId);

        try(WorkspaceTransaction tx = new ReadTransaction(workspace, datastoreService, user)) {
            Authorization authorization = new Authorization(user, ownerId, tx);
            authorization.assertCanEdit();

            UserTask userTask = taskService.startTask(user, "Importing ...");

            QueueFactory.getDefaultQueue().add(TaskOptions.Builder
                .withUrl("/service/load/run")
                .param("blobId", blobId.asString())
                .param("userId", Long.toString(user.getId()))
                .param("workspaceId", workspace.getWorkspaceId().asString())
                .param("ownerId", ownerId.asString())
                .param("userTaskId", userTask.getId()));

            return userTask;
        }
    }

    @POST
    @Path("run")
    public Response runImport(@FormParam("blobId") BlobId blobId,
                              @FormParam("userId") long userId,
                              @FormParam("taskId") String taskId,
                              @FormParam("workspaceId") ResourceId workspaceId,
                              @FormParam("ownerId") ResourceId ownerId) throws IOException {

        AuthenticatedUser user = new AuthenticatedUser("", (int) userId, "");

        try {
            BulkLoader loader = new BulkLoader();
            loader.setUser(user);
            loader.setWorkspaceId(workspaceId);
            loader.setSource(blobService.getBlob(user, blobId).getContent());
            loader.setOwnerId(ownerId);
            loader.setReader(new ExcelFormImportReader());
            loader.run();
            taskService.updateTask(user, taskId, UserTaskStatus.COMPLETE);

        } catch(Exception | NoClassDefFoundError e) {
            LOGGER.log(Level.SEVERE, "Import failed: " + e.getMessage(), e);
            taskService.updateTask(user, taskId, UserTaskStatus.FAILED);
        }
        return Response.ok().build();
    }
}
