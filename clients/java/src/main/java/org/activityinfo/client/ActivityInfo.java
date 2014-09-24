package org.activityinfo.client;

import com.google.common.collect.Lists;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.system.FolderClass;

import java.util.List;

/**
 * High-level interface to ActivityInfo workspaces, folders, and forms.
 */
public class ActivityInfo {

    private final ActivityInfoClient client;

    public ActivityInfo(ActivityInfoClient client) {
        this.client = client;
    }

    /**
     * Creates a new, empty workspace
     * @param label the label of the workspace
     * @return a Folder representing the new workspace
     */
    public Folder createWorkspace(String label) {
        Resource resource = Resources.createResource();
        resource.setId(Resources.generateId());
        resource.set("classId", FolderClass.CLASS_ID.asString());
        resource.setOwnerId(Resources.ROOT_ID);
        resource.set(FolderClass.LABEL_FIELD_ID.asString(), label);
        client.create(resource);

        return new Folder(client, new ResourceNode(resource));
    }


    public List<Folder> getOwnedOrSharedWorkspaces() {
        List<Folder> folders = Lists.newArrayList();
        for(ResourceNode node : client.getOwnedOrSharedWorkspaces()) {
            folders.add(new Folder(client, node));
        }

        return folders;
    }

}
