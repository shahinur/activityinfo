package org.activityinfo.store.load;

import org.activityinfo.model.record.RecordBeanClass;
import org.activityinfo.service.tasks.LoadTaskModel;
import org.activityinfo.service.tasks.LoadTaskModelClass;
import org.activityinfo.service.tasks.TaskContext;
import org.activityinfo.service.tasks.TaskExecutor;

public class LoadTaskExecutor implements TaskExecutor<LoadTaskModel> {

//    private DatastoreService datastore;
//    private AuthenticatedUser user;
//    private Workspace workspace;
//    private ResourceId ownerId;
//    private ByteSource source;
//    private FormLoader reader;
//
//
//    public LoadTaskExecutor() {
//        datastore = DatastoreServiceFactory.getDatastoreService();
//    }
//
//    public void setUser(AuthenticatedUser user) {
//        this.user = user;
//    }
//
//    public AuthenticatedUser getUser() {
//        return user;
//    }
//
//    public void setOwnerId(ResourceId ownerId) {
//        this.ownerId = ownerId;
//    }
//
//    public void setWorkspaceId(ResourceId workspaceId) {
//        workspace = new Workspace(workspaceId);
//    }
//
//    public void setSource(ByteSource source) {
//        this.source = source;
//    }
//
//    public void setReader(FormLoader reader) {
//        this.reader = reader;
//    }
//
//
//    public void run() throws IOException {
//
//
//    }

    @Override
    public RecordBeanClass<LoadTaskModel> getModelClass() {
        return LoadTaskModelClass.INSTANCE;
    }

    @Override
    public String describe(LoadTaskModel task) throws Exception {
        return null;
    }

    @Override
    public void execute(TaskContext context, LoadTaskModel task) throws Exception {

//        // First get temporary version number
//        long temporaryVersionNumber;
//        try(UpdateTransaction tx = new UpdateTransaction(workspace, datastore, user)) {
//            temporaryVersionNumber = -tx.getWorkspaceVersion();
//            tx.commit();
//        }
//
//        FormImportOptions options = new FormImportOptions();
//        options.setOwnerId(ownerId);
//        options.setUser(user);
//
//        // Write the instances outside of a transaction with a temporary number
//        HrdStoreLoader bulkTransaction = new HrdStoreLoader(user, workspace, temporaryVersionNumber);
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
        throw new UnsupportedOperationException();
    }
}
