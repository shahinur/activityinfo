package org.activityinfo.store.load;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.common.io.ByteSource;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.service.store.FormImportReader;
import org.activityinfo.store.hrd.entity.workspace.WorkspaceEntityGroup;

import java.io.IOException;

public class BulkLoader {

    private DatastoreService datastore;
    private AuthenticatedUser user;
    private WorkspaceEntityGroup workspace;
    private ResourceId ownerId;
    private ByteSource source;
    private FormImportReader reader;


    public BulkLoader() {
        datastore = DatastoreServiceFactory.getDatastoreService();
    }

    public void setUser(AuthenticatedUser user) {
        this.user = user;
    }

    public AuthenticatedUser getUser() {
        return user;
    }

    public void setOwnerId(ResourceId ownerId) {
        this.ownerId = ownerId;
    }

    public void setWorkspaceId(ResourceId workspaceId) {
        workspace = new WorkspaceEntityGroup(workspaceId);
    }

    public void setSource(ByteSource source) {
        this.source = source;
    }

    public void setReader(FormImportReader reader) {
        this.reader = reader;
    }


    public void run() throws IOException {
//
//        // First get temporary version number
//        long temporaryVersionNumber;
//        try(WorkspaceTransaction tx = new UpdateTransaction(workspace, datastore, user)) {
//            temporaryVersionNumber = -tx.incrementVersion();
//            tx.commit();
//        }
//
//        FormImportOptions options = new FormImportOptions();
//        options.setOwnerId(ownerId);
//        options.setUser(user);
//
//        // Write the instances outside of a transaction with a temporary number
//        BulkLoadTransaction bulkTransaction = new BulkLoadTransaction(user, workspace, temporaryVersionNumber);
//        BulkWriter writer = new BulkWriter(bulkTransaction);
//        try(InputStream in = source.openBufferedStream()) {
//            reader.load(options, in, writer);
//        }
//
//        // Trade in our temporary number for a new one within a real transaction
//        // and persist the FormClasses
//        try(UpdateTransaction tx = new UpdateTransaction(workspace, datastore, user)) {
//            writer.flush(tx);
//            tx.commit();
//        }
    }
}
