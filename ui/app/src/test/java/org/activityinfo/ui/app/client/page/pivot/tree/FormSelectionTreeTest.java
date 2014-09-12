package org.activityinfo.ui.app.client.page.pivot.tree;

import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.ui.app.client.TestFolder;
import org.activityinfo.ui.app.client.TestFormClass;
import org.activityinfo.ui.app.client.TestScenario;
import org.activityinfo.ui.flux.store.Status;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class FormSelectionTreeTest {

    @Test
    public void testRender() {

        TestScenario scenario = new TestScenario();
        TestFolder workspace = scenario.createWorkspace("Workspace A");
        TestFormClass form = workspace.newFormClass("Test Form").addTextField("Field").create();

        FormSelectionTree tree = new FormSelectionTree(scenario.application());
        Status<List<ResourceNode>> rootNodes = tree.getRootNodes();

        assertThat(rootNodes.isAvailable(), equalTo(true));
        assertThat(rootNodes.get().get(0).getLabel(), equalTo("Workspace A"));
        assertThat(rootNodes.get().get(0).getId(), equalTo(workspace.getId()));

        workspace.fetch();
        Status<List<ResourceNode>> workspaceItems = tree.getChildren(rootNodes.get().get(0));
        assertTrue(workspaceItems.isAvailable());
        assertThat(workspaceItems.get().get(0).getLabel(), equalTo("Test Form"));
    }

}