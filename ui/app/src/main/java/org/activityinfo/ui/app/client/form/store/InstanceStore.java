package org.activityinfo.ui.app.client.form.store;

import com.google.common.collect.Maps;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.service.store.UpdateResult;
import org.activityinfo.ui.app.client.store.Application;
import org.activityinfo.ui.flux.store.Store;
import org.activityinfo.ui.flux.store.StoreEventBus;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InstanceStore implements Store, PersistHandler, UpdateFieldHandler {

    private static final Logger LOGGER = Logger.getLogger(InstanceStore.class.getName());

    public enum LoadingState {
        LOADING,
        LOADING_FAILED,
        LOADED,
        SAVING,
        SAVING_FAILED
    }

    private final Application app;
    private final FormClass formClass;

    private Map<ResourceId, FieldState> fields = Maps.newHashMap();
    private Resource originalInstance;


    private LoadingState loadingState = LoadingState.LOADING;

    private boolean valid;

    public InstanceStore(Application app, FormClass formClass) {
        this.app = app;
        this.formClass = formClass;

        for(FormField field : formClass.getFields()) {
            fields.put(field.getId(), new FieldState(field));
        }
    }

    public void newInstance(FormInstance newInstance) {
        this.originalInstance = newInstance.asResource();
        for(FieldState field : fields.values()) {
            field.clearValue();
        }
        this.loadingState = LoadingState.LOADED;

        fireChange();
    }

    public ResourceId getInstanceId() {
        return originalInstance.getId();
    }

    private void fireChange() {
        app.getStoreEventBus().fireChange(this);
    }

    public StoreEventBus getStoreEventBus() {
        return app.getStoreEventBus();
    }

    public FormClass getFormClass() {
        return formClass;
    }

    public FieldState getState(ResourceId fieldId) {
        return fields.get(fieldId);
    }

    public LoadingState getLoadingState() {
        return loadingState;
    }

    private void updateState(LoadingState state) {
        if(this.loadingState != state) {
            this.loadingState = state;
            fireChange();
        }
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
        fields.get(action.getFieldId()).updateValue(action.getValue());
        fireChange();
    }


    /**
     *
     * @return true if the form instance is valid
     */
    public boolean isValid() {
        return valid;
    }

    @Override
    public void persistInstance(ResourceId resourceId) {

        if(!validate()) {
            fireChange();

        } else {

            FormInstance instance = new FormInstance(originalInstance.getId(), formClass.getId());
            instance.setOwnerId(originalInstance.getOwnerId());

            for (FieldState field : fields.values()) {
                originalInstance.set(resourceId, field.getValue());
            }

            updateState(LoadingState.SAVING);

            app.getStore().create(instance.asResource()).then(new AsyncCallback<UpdateResult>() {
                @Override
                public void onFailure(Throwable caught) {
                    updateState(LoadingState.SAVING_FAILED);
                    LOGGER.log(Level.SEVERE, "Exception on store: " + caught.getMessage(), caught);
                }

                @Override
                public void onSuccess(UpdateResult result) {
                    updateState(LoadingState.LOADED);
                }
            });
        }
    }


}
