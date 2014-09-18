package org.activityinfo.ui.app.client.store;

import com.google.common.collect.Maps;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.system.FolderClass;
import org.activityinfo.ui.app.client.action.CreateDraft;
import org.activityinfo.ui.app.client.action.DraftHandler;
import org.activityinfo.ui.app.client.draft.Draft;
import org.activityinfo.ui.flux.dispatcher.Dispatcher;
import org.activityinfo.ui.flux.store.AbstractStore;
import org.activityinfo.ui.flux.store.Status;

import java.util.Map;

/**
 * A store which manages the state of resources during editing by users.
 */
public class DraftStore extends AbstractStore implements DraftHandler {

    private final Dispatcher dispatcher;

    /**
     * Resources which have been created here and have never been seen by the server.
     */
    private final Map<ResourceId, Draft> newDrafts = Maps.newHashMap();

    private final Map<ResourceId, InstanceState> openDrafts = Maps.newHashMap();

    private InstanceState newWorkspaceDraft;
    private FormState newFormDraft;

    public DraftStore(Dispatcher dispatcher) {
        super(dispatcher);
        this.dispatcher = dispatcher;
    }

    public Status<Draft> get(ResourceId id) {
        if(newDrafts.containsKey(id)) {
            return Status.cache(newDrafts.get(id));
        }
        return Status.unavailable();
    }

    public boolean hasDraft(ResourceId resourceId) {
        return newDrafts.containsKey(resourceId);
    }


    public InstanceState getWorkspaceDraft() {
        if(newWorkspaceDraft == null) {
            FormInstance instance = new FormInstance(Resources.generateId(), FolderClass.CLASS_ID);
            instance.setOwnerId(Resources.ROOT_ID);

            newWorkspaceDraft = new InstanceState(dispatcher, FolderClass.get(), instance);
        }
        return newWorkspaceDraft;
    }

    public FormState getFormDraft(ResourceId ownerId) {
        if (newFormDraft == null) {
            FormClass newFormClass = new FormClass(Resources.generateId());
            newFormClass.setLabel("New form");
            newFormClass.setOwnerId(ownerId);

            newFormDraft = new FormState(dispatcher, newFormClass);
        }
        return newFormDraft;
    }

    @Override
    public void newDraft(CreateDraft draft) {
        newDrafts.put(draft.getResource().getId(), Draft.create(draft.getResource()));
    }

    @Override
    public void publishDraft(ResourceId resourceId) {

    }
}
