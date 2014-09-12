package org.activityinfo.ui.app.client.page.pivot.tree;

import org.activityinfo.ui.app.client.TestFolder;
import org.activityinfo.ui.app.client.TestFormClass;
import org.activityinfo.ui.app.client.TestScenario;
import org.activityinfo.ui.vdom.shared.dom.TestRenderContext;
import org.junit.Test;

import java.io.IOException;

public class FieldChooserTest {

    @Test
    public void test() throws IOException {

        // Setup data
        TestScenario scenario = new TestScenario();
        TestFolder workspace = scenario.createWorkspace("Workspace A");
        TestFormClass form = workspace.newFormClass("Test Form")
            .addTextField("Field 1").create()
            .addTextField("Field 2").create();


        TestRenderContext page = scenario.page();

        // Show the FieldChooser
        FieldChooser chooser = new FieldChooser(scenario.application());
        page.render(chooser);

        // Expected: form choice is displayed first
        chooser.showFormSelection();
        chooser.setVisible(true);

        // Expected: Workspace A is displayed as a root node
        page.assertTextIsPresent("Workspace A");

        // Click on the label to expand list
        page.clickElementWithText("Workspace A");

        // Expected: Test Form appears
        page.assertTextIsPresent("Test Form");

        // Select the form and then click next to choose the field
        page.clickElementWithText("Test Form");
        page.clickElementWithText("Next");

        // Verify that we arrive at the field tree
        page.assertTextIsPresent("Field 1");

        page.dumpDom();

    }
}