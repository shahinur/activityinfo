package org.activityinfo.store.tasks.load;

import org.activityinfo.model.form.FormClass;
import org.activityinfo.service.store.ImportWriter;
import org.activityinfo.service.store.InstanceWriter;
import org.activityinfo.service.store.StoreLoader;

public class BulkWriter implements ImportWriter {

    private final StoreLoader loader;

    public BulkWriter(StoreLoader loader) {
        this.loader = loader;
    }

    @Override
    public InstanceWriter createFormClass(FormClass formClass) {
        loader.create(formClass.asResource(), true);
        return new BulkInstanceWriter(loader, formClass);
    }

}
