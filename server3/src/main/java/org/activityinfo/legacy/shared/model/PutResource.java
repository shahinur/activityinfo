package org.activityinfo.legacy.shared.model;

import org.activityinfo.legacy.shared.command.Command;
import org.activityinfo.legacy.shared.command.result.VoidResult;
import org.activityinfo.model.resource.IsResource;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.Resources;

public class PutResource implements Command<VoidResult> {

    private String resourceId;
    private String json;

    public PutResource() {
    }

    public PutResource(String resourceId, String json) {
        this.resourceId = resourceId;
        this.json = json;
    }

    public PutResource(Resource resource) {
        this.resourceId = resource.getId().asString();
        this.json = Resources.toJson(resource);
    }

    public PutResource(IsResource resource) {
        this.resourceId = resource.getId().asString();
        this.json = Resources.toJson(resource.asResource());
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
