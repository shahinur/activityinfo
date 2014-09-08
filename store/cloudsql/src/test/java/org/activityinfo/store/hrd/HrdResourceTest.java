package org.activityinfo.store.hrd;

import com.google.common.base.Stopwatch;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.resource.*;
import org.activityinfo.model.system.FolderClass;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.model.type.primitive.TextType;
import org.activityinfo.service.store.CommitStatus;
import org.activityinfo.service.store.FolderRequest;
import org.activityinfo.service.store.UpdateResult;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class HrdResourceTest {

    @Rule
    public TestingEnvironment environment = new TestingEnvironment();

    @Test
    public void createWorkspace() {

        AuthenticatedUser me = environment.getUser();

        FormInstance workspace = createWorkspace("Workspace A");
        environment.getStore().create(me, workspace.asResource());

        List<ResourceNode> workspaces = environment.getStore().getOwnedOrSharedWorkspaces(me);
        assertThat(workspaces, hasSize(1));
    }

    @Test
    public void simple() throws IOException, SQLException {

        // Create a root folder

        FormInstance divA = createDivAFolder();

        UpdateResult divACreationResult = environment.getStore()
                .create(environment.getUser(), divA.asResource());

        assertThat(divACreationResult, hasProperty("status", equalTo(CommitStatus.COMMITTED)));

        Resource resource = environment.getStore().get(environment.getUser(), divA.getId());
        assertThat(resource.getString(FolderClass.LABEL_FIELD_ID.asString()), equalTo("Division A"));
        assertThat(resource.getVersion(), equalTo(divACreationResult.getNewVersion()));

        // Ensure that this stuff gets cached
        Stopwatch stopwatch = Stopwatch.createStarted();
        int requestCount = 10;
        for(int i=0;i!= requestCount;++i) {
            FolderProjection tree = environment.getStore().queryTree(
                environment.getUser(),
                new FolderRequest(divA.getId()));
            assertThat(tree.getRootNode().getId(), equalTo(divA.getId()));
            assertThat(tree.getRootNode().getLabel(), equalTo("Division A"));
            assertThat(tree.getRootNode().getVersion(), equalTo(divACreationResult.getNewVersion()));
            //assertThat(tree.getRootNode().getSubTreeVersion(), equalTo(divACreationResult.getNewVersion()));
        }
        System.out.println( (requestCount / (double)stopwatch.elapsed(TimeUnit.SECONDS)) + " requests per second");

        // Create form class

        FormClass formClass = createWidgetsFormClass(divA.getId());
        UpdateResult formCreationResult = environment.getStore()
                .create(environment.getUser(), formClass.asResource());

        assertThat(formCreationResult.getStatus(), Matchers.equalTo(CommitStatus.COMMITTED));

        // Verify that the subtree version gets updated

        FolderProjection tree = environment.getStore().queryTree(
            environment.getUser(),
            new FolderRequest(divA.getId()));
        assertThat(tree.getRootNode().getVersion(), equalTo(divACreationResult.getNewVersion()));
      //  assertThat(tree.getRootNode().getSubTreeVersion(), equalTo(formCreationResult.getNewVersion()));

    }

    @Test
    public void getWorkspace() {

        FormInstance folder1 = createWorkspace("Folder 1");
        FormInstance folder2 = createWorkspace("Folder 2");

        assertCommitted(environment.getStore().create(environment.getUser(),  folder1.asResource()));
        assertCommitted(environment.getStore().create(environment.getUser(), folder2.asResource()));

        List<ResourceNode> roots = environment.getStore().getOwnedOrSharedWorkspaces(environment.getUser());

        assertThat(roots, hasSize(2));
        assertThat(roots, containsInAnyOrder(
                hasProperty("label", equalTo("Folder 1")),
                hasProperty("label", equalTo("Folder 2"))));

    }

    private FormClass createWidgetsFormClass(ResourceId ownerId) {
        FormClass formClass = new FormClass(environment.generateId());
        formClass.setOwnerId(ownerId);
        formClass.setLabel("Widgets");

        FormField nameField = new FormField(Resources.generateId());
        nameField.setLabel("Name");
        nameField.setType(TextType.INSTANCE);
        formClass.addElement(nameField);

        FormField countField = new FormField(Resources.generateId());
        countField.setLabel("Count");
        countField.setType(new QuantityType().setUnits("widgets"));
        formClass.addElement(countField);
        return formClass;
    }

    private FormInstance createDivAFolder() {
        return createWorkspace("Division A");
    }

    private FormInstance createWorkspace(String label) {
        FormInstance divA = new FormInstance(environment.generateWorkspaceId(), FolderClass.CLASS_ID);
        divA.setOwnerId(Resources.ROOT_ID);
        divA.set(FolderClass.LABEL_FIELD_ID, label);
        return divA;
    }

    private void assertCommitted(UpdateResult resource) {
        assertThat(resource.getStatus(), Matchers.equalTo(CommitStatus.COMMITTED));
    }
}