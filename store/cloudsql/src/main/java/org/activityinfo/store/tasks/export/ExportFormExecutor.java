package org.activityinfo.store.tasks.export;

import com.google.common.base.Strings;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.record.RecordBeanClass;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.store.ResourceCursor;
import org.activityinfo.service.tasks.ExportFormTaskModel;
import org.activityinfo.service.tasks.ExportFormTaskModelClass;
import org.activityinfo.service.tasks.TaskContext;
import org.activityinfo.service.tasks.TaskExecutor;

import java.io.OutputStream;
import java.util.List;

public class ExportFormExecutor implements TaskExecutor<ExportFormTaskModel> {


    @Override
    public RecordBeanClass<ExportFormTaskModel> getModelClass() {
        return ExportFormTaskModelClass.INSTANCE;
    }

    @Override
    public String describe(ExportFormTaskModel task) throws Exception {
        if(Strings.isNullOrEmpty(task.getBlobId())) {
            throw new IllegalArgumentException("BlobId is required.");
        }
        if(Strings.isNullOrEmpty(task.getFilename())) {
            throw new IllegalArgumentException("Filename is required");
        }


        return I18N.MESSAGES.exportJobDescription(task.getFilename());
    }

    @Override
    public void execute(TaskContext context, ExportFormTaskModel task) throws Exception {

        FormClass formClass = FormClass.fromResource(context.getResource(task.getFormClassId()).getResource());

        List<FieldColumnSet> fields = new ColumnListBuilder().build(formClass);

        OutputStream out = context.createBlob(BlobId.valueOf(task.getBlobId()), task.getFilename(), "text/plain");

        CsvWriter writer = new CsvWriter(fields, out);

        // Write data rows
        ResourceCursor cursor = context.openCursor(task.getFormClassId());
        while(cursor.hasNext()) {
            Record record = cursor.next().getValue();
            writer.writeRow(record);
        }

        writer.close();
    }
}
