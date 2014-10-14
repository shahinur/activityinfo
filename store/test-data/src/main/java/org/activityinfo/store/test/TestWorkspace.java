package org.activityinfo.store.test;

import com.google.common.collect.Lists;
import org.activityinfo.model.resource.Resource;

import java.util.List;

public class TestWorkspace {

    private int userId;
    private Resource workspace;
    private final List<Resource> resources = Lists.newArrayList();

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Resource getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Resource workspace) {
        this.workspace = workspace;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void add(Resource resource) {
        resources.add(resource);
    }
}
