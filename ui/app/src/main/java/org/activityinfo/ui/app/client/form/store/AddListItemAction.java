package org.activityinfo.ui.app.client.form.store;

import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.ui.flux.action.Action;
import org.activityinfo.ui.flux.store.Store;

public class AddListItemAction implements Action {

    private ResourceId instanceId;
    private ResourceId fieldId;
    private FieldValue fieldValue;

    public AddListItemAction(ResourceId instanceId, ResourceId fieldId, FieldValue fieldValue) {
        this.instanceId = instanceId;
        this.fieldId = fieldId;
        this.fieldValue = fieldValue;
    }

    public ResourceId getInstanceId() {
        return instanceId;
    }

    public ResourceId getFieldId() {
        return fieldId;
    }

    public FieldValue getFieldValue() {
        return fieldValue;
    }

    @Override
    public void accept(Store store) {
        if(store instanceof InstanceChangeHandler) {
            ((InstanceChangeHandler) store).appendListItem(this);
        }
    }
}
