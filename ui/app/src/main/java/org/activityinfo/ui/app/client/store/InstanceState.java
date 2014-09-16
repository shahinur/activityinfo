package org.activityinfo.ui.app.client.store;

import com.google.common.collect.Maps;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.ui.app.client.form.store.*;
import org.activityinfo.ui.flux.dispatcher.Dispatcher;
import org.activityinfo.ui.flux.store.AbstractStore;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.logging.Logger;

public class InstanceState extends AbstractStore implements PersistHandler, InstanceChangeHandler {

    private static final Logger LOGGER = Logger.getLogger(InstanceState.class.getName());

    private final FormClass formClass;

    private FormInstance instance;

    private Map<ResourceId, FieldState> fields = Maps.newHashMap();

    private boolean valid;
    private boolean dirty;

    public InstanceState(@Nonnull Dispatcher dispatcher, @Nonnull FormClass formClass,
                         @Nonnull FormInstance instance) {
        super(dispatcher);
        this.formClass = formClass;
        this.instance = instance;

        for(FormField field : formClass.getFields()) {
            fields.put(field.getId(), new FieldState(instance.getId(), field, instance.get(field.getId())));
        }
    }

    public ResourceId getInstanceId() {
        return instance.getId();
    }

    public FormClass getFormClass() {
        return formClass;
    }

    public FieldState getState(ResourceId fieldId) {
        return fields.get(fieldId);
    }

    private boolean validate() {
        boolean valid = true;
        for(FieldState field : this.fields.values()) {
            if(!field.validate()) {
                valid = false;
            }
        }
        if(this.valid != valid) {
            this.valid = valid;
            fireChange();
        }
        return valid;
    }

    @Override
    public void updateField(UpdateFieldAction action) {
        if(action.getInstanceId().equals(instance.getId())) {
            fields.get(action.getFieldId()).updateValue(action.getValue());
            validate();
            fireChange();
        }
    }

    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void appendListItem(AddListItemAction action) {
        if(action.getInstanceId().equals(instance.getId())) {
            FieldState fieldState = fields.get(action.getFieldId());
            fieldState.appendValue(action.getFieldValue());

            validate();
            fireChange();
        }
    }

    public Resource getUpdatedResource() {
        FormInstance updatedInstance = instance.copy();
        for(FieldState state : fields.values()) {
            updatedInstance.set(state.getFieldId(), state.getValue());
        }
        return updatedInstance.asResource();
    }

    @Override
    public void persistInstance(ResourceId resourceId) {
        throw new UnsupportedOperationException();
    }

    /**
     *
     * @return true if the form instance is valid
     */
    public boolean isValid() {
        return validate();
    }


}
