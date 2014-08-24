package org.activityinfo.store.cloudsql;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Sets;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.resource.*;
import org.activityinfo.model.system.FolderClass;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.model.type.primitive.TextType;
import org.activityinfo.service.store.CommitStatus;
import org.activityinfo.service.store.ResourceTreeRequest;
import org.activityinfo.service.store.UpdateResult;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class MySqlResourceStoreTest {

    @Rule
    public TestingEnvironment environment = new TestingEnvironment();

    @Test
    public void simple() throws IOException, SQLException {

        // Create a root folder

        FormInstance divA = createDivAFolder();

        UpdateResult divACreationResult = environment.getStore()
                .put(environment.getUser(), divA.getId(), divA.asResource());

        assertThat(divACreationResult, hasProperty("status", equalTo(CommitStatus.COMMITTED)));

        Resource resource = environment.getStore().get(environment.getUser(), divA.getId());
        assertThat(resource.getString(FolderClass.LABEL_FIELD_ID.asString()), equalTo("Division A"));
        assertThat(resource.getVersion(), equalTo(divACreationResult.getNewVersion()));

        // Ensure that this stuff gets cached
        Stopwatch stopwatch = Stopwatch.createStarted();
        int requestCount = 10000;
        for(int i=0;i!= requestCount;++i) {
            ResourceTree tree = environment.getStore().queryTree(
                    environment.getUser(),
                    new ResourceTreeRequest(divA.getId()));
            assertThat(tree.getRootNode().getId(), equalTo(divA.getId()));
            assertThat(tree.getRootNode().getLabel(), equalTo("Division A"));
            assertThat(tree.getRootNode().getVersion(), equalTo(divACreationResult.getNewVersion()));
            assertThat(tree.getRootNode().getSubTreeVersion(), equalTo(divACreationResult.getNewVersion()));
        }
        System.out.println( (requestCount / (double)stopwatch.elapsed(TimeUnit.SECONDS)) + " requests per second");

        // Create form class

        FormClass formClass = createWidgetsFormClass(divA.getId());
        UpdateResult formCreationResult = environment.getStore().put(
                environment.getUser(), formClass.asResource());

        assertThat(formCreationResult.getStatus(), Matchers.equalTo(CommitStatus.COMMITTED));

        // Verify that the subtree version gets updated

        ResourceTree tree = environment.getStore().queryTree(
                environment.getUser(),
                new ResourceTreeRequest(divA.getId()));
        assertThat(tree.getRootNode().getVersion(), equalTo(divACreationResult.getNewVersion()));
        assertThat(tree.getRootNode().getSubTreeVersion(), equalTo(formCreationResult.getNewVersion()));


        // try to fetch two resources at a time
        Set<Resource> resultSet = environment.getStore().get(
                environment.getUser(),
                Sets.newHashSet(divA.getId(), formClass.getId()));
        assertThat(resultSet, containsInAnyOrder(
                hasProperty("id", equalTo(divA.getId())),
                hasProperty("id", equalTo(formClass.getId()))));


        environment.assertThatAllConnectionsHaveBeenClosed();
    }

    @Test
    public void getRootResources() {

        FormInstance folder1 = createFolder("Folder 1");
        FormInstance folder2 = createFolder("Folder 2");

        assertCommitted(environment.getStore().put(environment.getUser(),  folder1.asResource()));
        assertCommitted(environment.getStore().put(environment.getUser(), folder2.asResource()));

        List<ResourceNode> roots = environment.getStore().getUserRootResources(environment.getUser());

        assertThat(roots, hasSize(2));
        assertThat(roots, containsInAnyOrder(
                hasProperty("label", equalTo("Folder 1")),
                hasProperty("label", equalTo("Folder 2"))));

    }

    private FormClass createWidgetsFormClass(ResourceId ownerId) {
        FormClass formClass = new FormClass(Resources.generateId());
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
        return createFolder("Division A");
    }

    private FormInstance createFolder(String label) {
        FormInstance divA = new FormInstance(Resources.generateId(), FolderClass.CLASS_ID);
        divA.setOwnerId(Resources.ROOT_ID);
        divA.set(FolderClass.LABEL_FIELD_ID, label);
        return divA;
    }

    private void assertCommitted(UpdateResult resource) {
        assertThat(resource.getStatus(), Matchers.equalTo(CommitStatus.COMMITTED));
    }

}