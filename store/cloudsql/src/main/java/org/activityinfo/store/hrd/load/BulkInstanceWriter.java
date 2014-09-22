package org.activityinfo.store.hrd.load;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.service.store.InstanceWriter;
import org.activityinfo.store.hrd.entity.UpdateTransaction;
import org.activityinfo.store.hrd.entity.Workspace;

public class BulkInstanceWriter implements InstanceWriter {

    private final Workspace workspace;
    private final BulkLoadTransaction tx;
    private final FormClass formClass;

    private long rowCount = 0;

    public BulkInstanceWriter(BulkLoadTransaction tx, FormClass formClass) {
        Preconditions.checkNotNull(tx, "tx must not be null");
        Preconditions.checkNotNull(formClass, "formClass must not be null");

        this.tx = tx;
        this.workspace = tx.getWorkspace();
        this.formClass = formClass;
    }

    @Override
    public void write(FormInstance instance) {
        workspace.createResource(tx, instance.asResource(), Optional.of(rowCount++));
    }

    public void flushFormClass(UpdateTransaction tx) {
        workspace.createResource(tx, formClass.asResource());
        workspace.getFormMetadata(formClass.getId()).put(tx.getCurrentVersion(), rowCount);
    }
}
