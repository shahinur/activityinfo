package org.activityinfo.legacy.shared.command.result;


import com.google.common.collect.Lists;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.Resources;

import java.util.ArrayList;
import java.util.List;

public class ResourceResult implements CommandResult {

    private ArrayList<String> json;

    public ResourceResult() {
    }

    public ResourceResult(ArrayList<String> encodedResults) {
        this.json = encodedResults;
    }

    public ArrayList<String> getJson() {
        return json;
    }

    public void setJson(ArrayList<String> json) {
        this.json = json;
    }

    public Resource parseResource() {
        assert json.size() == 1;
        return Resources.fromJson(json.get(0));
    }

    public List<Resource> parseResources() {
        List<Resource> resources = Lists.newArrayList();
        for(String jsonEncoded : json) {
            resources.add(Resources.fromJson(jsonEncoded));
        }
        return resources;
    }
}
