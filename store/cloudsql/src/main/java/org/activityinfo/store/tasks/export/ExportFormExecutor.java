package org.activityinfo.store.tasks.export;

import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.record.RecordBeanClass;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.store.ResourceCursor;
import org.activityinfo.service.tasks.ExportFormTask;
import org.activityinfo.service.tasks.ExportFormTaskClass;
import org.activityinfo.service.tasks.TaskContext;
import org.activityinfo.service.tasks.TaskExecutor;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import java.io.OutputStream;
import java.util.List;

public class ExportFormExecutor implements TaskExecutor<ExportFormTask> {


    @Override
    public RecordBeanClass<ExportFormTask> getModelClass() {
        return ExportFormTaskClass.INSTANCE;
    }

    @Override
    public String describe(TaskContext context, ExportFormTask task) throws Exception {
        FormClass formClass = FormClass.fromResource(context.getResource(task.getFormClassId()).getResource());

        return I18N.MESSAGES.exportJobDescription(formClass.getLabel());
    }

    @Override
    public void execute(TaskContext context, ExportFormTask task) throws Exception {

        FormClass formClass = FormClass.fromResource(context.getResource(task.getFormClassId()).getResource());

        List<FieldColumnSet> fields = new ColumnListBuilder().build(formClass);

        BlobId blobId = BlobId.generate();
        String fileName =  String.format("%s Export %s.csv", formClass.getLabel(),
            ISODateTimeFormat.basicDateTimeNoMillis().print(new DateTime()));

        OutputStream out = context.createBlob(blobId, fileName, "text/plain");

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
