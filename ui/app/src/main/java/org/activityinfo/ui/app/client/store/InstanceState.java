package org.activityinfo.ui.app.client.store;

import com.google.common.collect.Maps;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.service.store.UpdateResult;
import org.activityinfo.ui.app.client.action.RemoteUpdateHandler;
import org.activityinfo.ui.app.client.form.store.AddListItemAction;
import org.activityinfo.ui.app.client.form.store.FieldState;
import org.activityinfo.ui.app.client.form.store.InstanceChangeHandler;
import org.activityinfo.ui.app.client.form.store.UpdateFieldAction;
import org.activityinfo.ui.app.client.request.Request;
import org.activityinfo.ui.app.client.request.SaveRequest;
import org.activityinfo.ui.flux.dispatcher.Dispatcher;
import org.activityinfo.ui.flux.store.AbstractStore;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.logging.Logger;

public class InstanceState extends AbstractStore implements InstanceChangeHandler, RemoteUpdateHandler {

    private static final Logger LOGGER = Logger.getLogger(InstanceState.class.getName());

    private final FormClass formClass;

    private FormInstance instance;

    private Map<ResourceId, FieldState> fields = Maps.newHashMap();

    private boolean valid;
    private boolean dirty;

    private long currentVersion;

    public InstanceState(@Nonnull Dispatcher dispatcher, @Nonnull FormClass formClass,
                         @Nonnull FormInstance instance) {
        super(dispatcher);
        this.formClass = formClass;
        this.instance = instance;
        this.currentVersion = instance.getVersion();

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
            dirty = true;
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
            dirty = true;

            FieldState fieldState = fields.get(action.getFieldId());
            fieldState.appendValue(action.getFieldValue());

            validate();
            fireChange();
        }
    }

    public Resource getUpdatedResource() {
        FormInstance updatedInstance = instance.copy();
        updatedInstance.setVersion(currentVersion);
        for(FieldState state : fields.values()) {
            updatedInstance.set(state.getFieldId(), state.getValue());
        }
        return updatedInstance.asResource();
    }

    /**
     *
     * @return true if the form instance is valid
     */
    public boolean isValid() {
        return validate();
    }


    @Override
    public void requestStarted(Request request) {

    }

    @Override
    public void requestFailed(Request request, Throwable e) {

    }

    @Override
    public <R> void processUpdate(Request<R> request, R response) {
        if(request instanceof SaveRequest) {
            SaveRequest saveRequest = (SaveRequest) request;
            UpdateResult updateResult = (UpdateResult) response;
            if(saveRequest.getResourceId().equals(instance.getId())) {
                dirty = false;
                this.currentVersion = updateResult.getNewVersion();
                fireChange();
            }
        }
    }
}
