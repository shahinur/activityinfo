package org.activityinfo.store.tasks.load;

import com.google.common.io.ByteSource;
import org.activityinfo.io.load.excel.ExcelFormImportReader;
import org.activityinfo.model.record.RecordBeanClass;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.store.FormImportReader;
import org.activityinfo.service.store.StoreLoader;
import org.activityinfo.service.tasks.LoadTaskModel;
import org.activityinfo.service.tasks.LoadTaskModelClass;
import org.activityinfo.service.tasks.TaskContext;
import org.activityinfo.service.tasks.TaskExecutor;

import java.io.InputStream;

public class LoadTaskExecutor implements TaskExecutor<LoadTaskModel> {


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

        StoreLoader storeLoader = context.beginLoad(task.getFolderId());
        BulkWriter writer = new BulkWriter(storeLoader);
        ByteSource blob = context.getBlob(BlobId.valueOf(task.getBlobId()));


        try(InputStream in = blob.openBufferedStream()) {

            FormImportReader reader = getReader(task);
            reader.load(in, writer);
        }

        storeLoader.commit();
    }

    private FormImportReader getReader(LoadTaskModel task) {
        return new ExcelFormImportReader();
    }
}
