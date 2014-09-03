package org.activityinfo.ui.app.client.form.store;

import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.promise.Promise;
import org.activityinfo.ui.flux.action.Action;
import org.activityinfo.ui.flux.store.Store;

public class UpdateFieldAction implements Action<UpdateFieldHandler> {
    private ResourceId fieldValue;
    private FieldValue value;

    public UpdateFieldAction(ResourceId fieldValue, FieldValue value) {
        this.fieldValue = fieldValue;
        this.value = value;
    }

    @Override
    public Promise<Void> accept(Store store) {
        if(store instanceof UpdateFieldHandler) {
            ((UpdateFieldHandler) store).updateField(this);
        }
        return Promise.done();
    }

    public ResourceId getFieldId() {
        return fieldValue;
    }

    public FieldValue getValue() {
        return value;
    }
}
