package org.activityinfo.store.tasks.load;

import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.service.store.InstanceWriter;
import org.activityinfo.service.store.StoreLoader;

public class BulkInstanceWriter implements InstanceWriter {


    private StoreLoader loader;

    public BulkInstanceWriter(StoreLoader loader, FormClass formClass) {
        this.loader = loader;
    }

    @Override
    public void write(FormInstance instance) {
        loader.create(instance.asResource(), false);
    }
}
