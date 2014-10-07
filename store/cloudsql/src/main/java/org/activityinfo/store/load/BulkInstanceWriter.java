package org.activityinfo.store.load;

import com.google.common.base.Preconditions;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.service.store.InstanceWriter;

public class BulkInstanceWriter implements InstanceWriter {


    private long rowCount = 0;

    public BulkInstanceWriter(BulkLoadTransaction tx, FormClass formClass) {
        Preconditions.checkNotNull(tx, "tx must not be null");
        Preconditions.checkNotNull(formClass, "formClass must not be null");

    }

    @Override
    public void write(FormInstance instance) {
//        workspace.createResource(tx, instance.asResource(), Optional.of(rowCount++));
    }
//
//    public void flushFormClass(UpdateTransaction tx) {
//        workspace.createResource(tx, formClass.asResource());
//        workspace.getFormMetadata(formClass.getId()).put(tx.getCurrentVersion(), rowCount);
//    }
}
