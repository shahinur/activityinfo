package org.activityinfo.ui.app.client.action;

import org.activityinfo.model.resource.ResourceId;

public interface DraftHandler {

    void newDraft(CreateDraft draft);

    void publishDraft(ResourceId resourceId);

}
