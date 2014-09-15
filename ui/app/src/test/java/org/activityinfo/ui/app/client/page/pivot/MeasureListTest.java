package org.activityinfo.ui.app.client.page.pivot;

import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.ListFieldValue;
import org.activityinfo.ui.app.client.TestFolder;
import org.activityinfo.ui.app.client.TestFormClass;
import org.activityinfo.ui.app.client.TestScenario;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;

public class MeasureListTest {

    @Test
    public void testAddMeasure() throws IOException {

        TestScenario scenario = new TestScenario();
        TestFolder workspace = scenario.createWorkspace("Workspace A");
        TestFormClass form = workspace.newFormClass("Test Form");
        FormField field = form.addQuantityField("My Measure #1", "widgets");
        form.create();

        PivotPage pivotPage = new PivotPage(scenario.application());

        scenario.page().render(pivotPage);

        pivotPage.getMeasureList().onMeasureSelected(field);

        ResourceId measureFieldId = ResourceId.valueOf("measures");

        FieldValue value = pivotPage.getWorkingDraft().getState(measureFieldId).getValue();
        assertThat(value, instanceOf(ListFieldValue.class));
        assertThat(((ListFieldValue)value).getElements(), hasSize(1));

        scenario.page().dumpDom();

        scenario.page().assertTextIsPresent(field.getLabel());
    }
}