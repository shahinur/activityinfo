package org.activityinfo.io.load;

import com.google.common.io.ByteSource;
import org.activityinfo.model.record.RecordBeanClass;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.blob.BlobMetadata;
import org.activityinfo.service.store.StoreLoader;
import org.activityinfo.service.tasks.LoadTaskModel;
import org.activityinfo.service.tasks.LoadTaskModelClass;
import org.activityinfo.service.tasks.TaskContext;
import org.activityinfo.service.tasks.TaskExecutor;

import java.io.IOException;

public class LoadTaskExecutor implements TaskExecutor<LoadTaskModel> {

    private FileHandlers fileHandlers = new FileHandlers();

    public LoadTaskExecutor() {
    }

    @Override
    public RecordBeanClass<LoadTaskModel> getModelClass() {
        return LoadTaskModelClass.INSTANCE;
    }

    @Override
    public String describe(LoadTaskModel task) throws Exception {
        return "";
    }

    @Override
    public void execute(TaskContext taskContext, LoadTaskModel task) throws Exception {

        StoreLoader storeLoader = taskContext.beginLoad(task.getFolderId());
        FileSource fileSource = rootFileSource(taskContext, task);
        LoadContext loadContext = new LoadContext(storeLoader, task.getFolderId());

        FileHandler handler = fileHandlers.find(fileSource);
        handler.load(loadContext, fileSource);

        storeLoader.commit();
    }

    public FileSource rootFileSource(TaskContext context, LoadTaskModel task) throws IOException {
        String blobId = task.getBlobId();
        ByteSource blob = context.getBlob(BlobId.valueOf(blobId));
        BlobMetadata blobMetadata = context.getBlobMetadata(BlobId.valueOf(blobId));
        return new FileSource(blobMetadata.getContentDisposition().getFileName(), blob);
    }
}
