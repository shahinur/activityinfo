package org.activityinfo.legacy.shared.command.result;


import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.Resources;

public class ResourceResult implements CommandResult {

    private String json;

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public Resource parseResource() {
        return Resources.fromJson(json);
    }
}
