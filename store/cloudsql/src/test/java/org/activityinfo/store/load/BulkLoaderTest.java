package org.activityinfo.store.load;

import com.google.common.collect.Lists;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.record.Records;
import org.activityinfo.model.resource.*;
import org.activityinfo.model.system.Folder;
import org.activityinfo.model.system.FolderClass;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.service.store.CommitStatus;
import org.activityinfo.service.store.ResourceNotFound;
import org.activityinfo.service.store.UpdateResult;
import org.activityinfo.store.hrd.TestingEnvironment;
import org.activityinfo.store.hrd.dao.BulkLoader;
import org.activityinfo.store.hrd.dao.WorkspaceQuery;
import org.activityinfo.store.hrd.entity.workspace.WorkspaceEntityGroup;
import org.activityinfo.store.test.TestFormClass;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;


public class BulkLoaderTest {

    @Rule
    public TestingEnvironment environment = new TestingEnvironment();


    private TestFormClass form;
    private ResourceId workspaceId;
    private WorkspaceEntityGroup workspace;
    private AuthenticatedUser me;

    @Before
    public void setup() {
        workspaceId = Resources.generateId();
        workspace = new WorkspaceEntityGroup(workspaceId);
        form = new TestFormClass(workspaceId);
        me = environment.getUser();
    }

    @Test
    public void test() throws IOException {

        Resource rootFolder = Resources.createResource();
        rootFolder.setId(workspaceId);
        rootFolder.setOwnerId(Resources.ROOT_ID);
        rootFolder.setValue(new Folder().setLabel("Workspace").asRecord());
        environment.getStore().create(me, rootFolder);

        Resource parentFolder = Resources.createResource();
        parentFolder.setId(Resources.generateId());
        parentFolder.setValue(Records.builder(FolderClass.CLASS_ID)
            .set(FolderClass.LABEL_FIELD_NAME, "ParentFolder")
            .build());
        parentFolder.setOwnerId(workspaceId);


        TestFormClass form = new TestFormClass(parentFolder.getId());

        BulkLoader loader = BulkLoader.newBuilder(environment.getContext(), me, rootFolder.getId())
            .setBatchSize(5)
            .begin();

        // Create the form
        loader.create(parentFolder, true);
        loader.create(form.formClass.asResource(), true);


        // During the load process, resources should not be visible
        assertNotVisible(form.formClass.getId());

        List<Resource> instances = Lists.newArrayList(form.instances(20));

        // Load the remainder to force an actual write to the datastore
        for (Resource resource : instances) {
            loader.create(resource, false);
        }

        for(Resource instance : instances) {
            assertNotVisible(instance.getId());
        }

        // Finally commit
        long commitVersion = loader.commit();

        // Cheat a bit to avoid hitting late syncing
        environment.getContext().getWorkspaceCache().cache(parentFolder.getId(), workspaceId);
        environment.getContext().getWorkspaceCache().cache(form.formClass.getId(), workspaceId);
        environment.getContext().getWorkspaceCache().cache(instances.get(10).getId(), workspaceId);

        // Now everything should be visible.


        // Check get()
        UserResource formClassResource = environment.getResource(form.formClass.getId());
        assertThat(formClassResource.getResourceId(), equalTo(form.formClass.getId()));
        assertThat(formClassResource.getResource().getOwnerId(), equalTo(parentFolder.getId()));
        assertThat(formClassResource.getResource().getVersion(), equalTo(commitVersion));

        ResourceId instanceId = instances.get(10).getId();
        UserResource instanceResource = environment.getResource(instanceId);
        assertThat(instanceResource.getResourceId(), equalTo(instanceId));
        assertThat(instanceResource.getResource().getOwnerId(), equalTo(formClassResource.getResourceId()));
        assertThat(instanceResource.getResource().getVersion(), equalTo(commitVersion));

        FolderProjection folder = environment.queryTree(workspaceId);
        assertThat(folder.getRootNode().getChildren(), hasSize(1));

        folder =  environment.queryTree(parentFolder.getId());
        assertThat(folder.getRootNode().getChildren(), hasSize(1));
        ResourceNode formNode = folder.getRootNode().getChildren().get(0);
        assertThat(formNode.getId(), equalTo(form.formClass.getId()));
        assertThat(formNode.getClassId(), equalTo(FormClass.CLASS_ID));
        assertThat(formNode.getLabel(), equalTo(form.formClass.getLabel()));
        assertThat(formNode.getOwnerId(), equalTo(parentFolder.getId()));

        // Verify we can read imported instances
        TableModel tableModel = new TableModel(form.formClass.getId());
        tableModel.selectField("age").as("C1");

        TableData data = environment.queryTable(tableModel);
        assertThat(data.getNumRows(), equalTo(20));

        // Now try to update one of the form instances
        Resource updatedInstance = instanceResource.getResource().copy();
        updatedInstance.setValue(Records.buildCopyOf(updatedInstance.getValue())
            .set(form.name.getName(), "New Name")
            .build());

        UpdateResult updateResult = environment.put(updatedInstance);
        assertThat(updateResult.getStatus(), Matchers.equalTo(CommitStatus.COMMITTED));
        assertThat(updateResult.getNewVersion(), greaterThan(commitVersion));

        // Verify that we can re-read
        UserResource reloaded = environment.getResource(updatedInstance.getId());
        assertThat(reloaded.getResource().getVersion(), Matchers.equalTo(updateResult.getNewVersion()));
        assertThat(reloaded.getResource().getValue().getString(form.name.getName()), Matchers.equalTo("New Name"));

    }

    private void assertNotVisible(ResourceId id) {
        try(WorkspaceQuery query = new WorkspaceQuery(environment.getContext(), workspace, me)) {
            try {
                UserResource node = query.getResource(id).asUserResource();
                Assert.fail("Resource id " + id + " should not be visible");

            } catch(ResourceNotFound e) {
                // correct
                return;
            }
        }
    }


}