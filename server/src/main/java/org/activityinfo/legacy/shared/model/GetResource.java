package org.activityinfo.legacy.shared.model;

import com.google.common.collect.Lists;
import org.activityinfo.legacy.shared.command.Command;
import org.activityinfo.legacy.shared.command.result.ResourceResult;
import org.activityinfo.model.resource.ResourceId;

import java.util.ArrayList;

/**
 * Fetches a resource by Id;
 */
public class GetResource implements Command<ResourceResult> {

    private ArrayList<String> ids;

    public GetResource() {
    }

    public GetResource(ResourceId id) {
        this.ids = Lists.newArrayList(id.asString());
    }

    public GetResource(String id) {
        this.ids = Lists.newArrayList(id);
    }

    public GetResource(Iterable<ResourceId> resourceIds) {
        ids = Lists.newArrayList();
        for(ResourceId id : resourceIds) {
            ids.add(id.asString());
        }
    }

    public ArrayList<String> getIds() {
        return ids;
    }

    public void setIds(ArrayList<String> ids) {
        this.ids = ids;
    }
}
