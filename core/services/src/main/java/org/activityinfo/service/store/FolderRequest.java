package org.activityinfo.service.store;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.system.FolderClass;

import java.util.Set;

/**
 * Defines a request for an outline of resources visible
 * to the user, starting at a given node.
 */
public class FolderRequest {

    private ResourceId rootId;

    private Set<ResourceId> formClassIds = Sets.newHashSet(FormClass.CLASS_ID, FolderClass.CLASS_ID);

    /**
     * Creates a request for a {@code ResourceTree}, starting at the {@code Resource}
     * identified by {@code rootResourceId}
     */
    @JsonCreator
    public FolderRequest(@JsonProperty("rootId") ResourceId rootResourceId) {
        this.rootId = rootResourceId;
    }

    public ResourceId getRootId() {
        return rootId;
    }

    /**
     *
     * @return the FormClass to include in the outline
     */
    public Set<ResourceId> getFormClassIds() {
        return formClassIds;
    }
}
