package org.activityinfo.ui.app.client.store;

import com.google.common.collect.Maps;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.system.FolderClass;
import org.activityinfo.ui.app.client.action.DraftHandler;
import org.activityinfo.ui.app.client.draft.Draft;
import org.activityinfo.ui.flux.dispatcher.Dispatcher;
import org.activityinfo.ui.flux.store.AbstractStore;

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

    public InstanceState getWorkspaceDraft() {
        if(newWorkspaceDraft == null) {
            FormInstance instance = new FormInstance(Resources.generateId(), FolderClass.CLASS_ID);
            instance.setOwnerId(Resources.ROOT_ID);

            newWorkspaceDraft = new InstanceState(dispatcher, FolderClass.get(), instance);
        }
        return newWorkspaceDraft;
    }

    @Override
    public void publishDraft(ResourceId resourceId) {

    }

}
