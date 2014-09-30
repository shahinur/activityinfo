package org.activityinfo.ui.app.client.page.pivot;

import org.activityinfo.model.analysis.PivotTableModelClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.record.Records;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.table.Bucket;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.ListFieldValue;
import org.activityinfo.promise.Promise;
import org.activityinfo.ui.app.client.TestFolder;
import org.activityinfo.ui.app.client.TestFormClass;
import org.activityinfo.ui.app.client.TestScenario;
import org.activityinfo.ui.app.client.request.FetchCube;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
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

        form.newInstance().set("My Measure #1", 5).save();

        Resource pivotTableModel = Resources.createResource();
        pivotTableModel.setId(Resources.generateId());
        pivotTableModel.setOwnerId(Resources.generateId());
        pivotTableModel.setValue(Records.builder(PivotTableModelClass.CLASS_ID).build());

        PivotPage pivotPage = new PivotPage(scenario.application(), PivotTableModelClass.INSTANCE.get(),
           pivotTableModel);

        scenario.page().render(pivotPage);

        pivotPage.getPivotSideBar().onMeasureSelected(form.get(), field);

        ResourceId measureFieldId = ResourceId.valueOf("measures");

        FieldValue value = pivotPage.getWorkingDraft().getState(measureFieldId).getValue();
        assertThat(value, instanceOf(ListFieldValue.class));
        assertThat(((ListFieldValue)value).getElements(), hasSize(1));

        scenario.page().dumpDom();

        scenario.page().assertTextIsPresent(field.getLabel());

        Promise<List<Bucket>> cube = scenario.request(new FetchCube(
            PivotTableModelClass.INSTANCE.toBean(pivotPage.getWorkingDraft().getUpdatedResource().getValue())));

        assertThat(cube.getState(), equalTo(Promise.State.FULFILLED));
        assertThat(cube.get(), hasSize(1));
        assertThat(cube.get().get(0).getValue(), equalTo(5d));

    }
}