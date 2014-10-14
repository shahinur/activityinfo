package org.activityinfo.migrator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import org.activityinfo.model.json.ObjectMapperFactory;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.store.test.TestWorkspace;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class JsonTestUnitWriter implements ResourceWriter {
    private final File file;
    private List<TestWorkspace> workspaces;
    private Map<ResourceId, TestWorkspace> workspaceLookup;

    private final ObjectMapper objectMapper = ObjectMapperFactory.get();


    public JsonTestUnitWriter(File file) throws IOException {
        this.file = file;
        this.workspaceLookup = new HashMap<>();
        this.workspaces = new ArrayList<>();
    }

    @Override
    public void beginResources() throws Exception {

    }

    @Override
    public void writeResource(int userId, Resource resource, Date dateCreated, Date dateDeleted) throws IOException {

        if(resource.getOwnerId().equals(Resources.ROOT_ID)) {
            Preconditions.checkArgument(userId!=0, "user id for workspace " + resource.getId() + " is not set");
            TestWorkspace workspace = new TestWorkspace();
            workspace.setUserId(userId);
            workspace.setWorkspace(resource);
            workspaces.add(workspace);
            workspaceLookup.put(resource.getId(), workspace);
        } else {
            TestWorkspace workspace = workspaceLookup.get(resource.getOwnerId());
            Preconditions.checkNotNull(workspace, "missing ownerId " + resource.getOwnerId());
            workspace.add(resource);
            workspaceLookup.put(resource.getId(), workspace);
        }
    }

    @Override
    public void endResources() throws Exception {

    }

    @Override
    public void close() throws IOException {
        System.out.println("Writing " + workspaces.size() + " to " + file.getAbsolutePath());
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, workspaces);
    }

    public void finish() throws IOException {

    }
}
