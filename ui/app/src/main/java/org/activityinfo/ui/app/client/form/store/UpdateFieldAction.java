package org.activityinfo.ui.app.client.form.store;

import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.ui.flux.action.Action;
import org.activityinfo.ui.flux.store.Store;

public class UpdateFieldAction implements Action<InstanceChangeHandler> {
    private ResourceId fieldValue;
    private FieldValue value;

    public UpdateFieldAction(ResourceId fieldValue, FieldValue value) {
        this.fieldValue = fieldValue;
        this.value = value;
    }

    @Override
    public void accept(Store store) {
        if(store instanceof InstanceChangeHandler) {
            ((InstanceChangeHandler) store).updateField(this);
        }
    }

    public ResourceId getFieldId() {
        return fieldValue;
    }

    public FieldValue getValue() {
        return value;
    }
}
