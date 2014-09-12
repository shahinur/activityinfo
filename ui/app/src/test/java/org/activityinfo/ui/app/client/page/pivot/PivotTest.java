package org.activityinfo.ui.app.client.page.pivot;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.activityinfo.model.analysis.*;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.*;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.enumerated.EnumType;
import org.activityinfo.model.type.expr.CalculatedFieldType;
import org.activityinfo.service.cubes.CubeBuilder;
import org.activityinfo.service.store.FolderRequest;
import org.activityinfo.store.test.TestResourceStore;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PivotTest {

    private TestResourceStore store;

    private List<FormClass> forms = Lists.newArrayList();

    @Before
    public void loadData() throws IOException {
        store = new TestResourceStore();
        store.load("lcca.json");

        for(Resource resource : store.all()) {
            if(FormClass.CLASS_ID.asString().equals(resource.isString("classId"))) {
                forms.add(FormClass.fromResource(resource));
            }
        }
    }


    @Test
    public void testCalculations() throws Exception {
        dumpIndicators();

        FormClass costs = findForm("Documented Cost Information");
        FormClass wp = findForm("Water Collection Point information over time - Monthly Reports");

        PivotTableModel model = new PivotTableModel();
        model.getDimensions().add(dim("Year", dimSource(costs, "[Year of expediture]"), dimSource(wp, "Year")));
        model.getDimensions().add(dim("Typology", dimSource(costs, "[Cost typology]")));


        MeasureModel iniCost = new MeasureModel();
        iniCost.setId("iniCosts");
        iniCost.setLabel("Initial Costs");
        iniCost.setSource(costs.getId());
        iniCost.setValueExpression("V_InCostHrd");
        iniCost.setMeasurementType(MeasurementType.FLOW);
        iniCost.setCriteriaExpression("{System Identifier}=={Hand Dug Wells (All)}");
        model.addMeasure(iniCost);

        MeasureModel wellCount = new MeasureModel();
        wellCount.setId("wellCount");
        wellCount.setLabel("HDW Count");
        wellCount.setSource(wp.getId());
        wellCount.setValueExpression("TPSfnct");
        wellCount.setMeasurementType(MeasurementType.STOCK);
        model.addMeasure(wellCount);

        CubeBuilder cubeBuilder = new CubeBuilder(store);
        Cube cube = cubeBuilder.buildCube(model);

        cube.dump();
    }

    private DimensionSource dimSource(FormClass costs, String valueExpr) {
        return new DimensionSource(costs.getId(), valueExpr);
    }

    private void dumpTable(TableData tableData) {

        ArrayList<String> columns = Lists.newArrayList(tableData.getColumns().keySet());
        System.out.println(Joiner.on(",").join(columns));
        for(int i=0;i!=tableData.getNumRows();++i) {
            for(int j=0;j<tableData.getColumns().size();++j) {
                if(j > 0) {
                    System.out.print(",");
                }
                System.out.print("" + tableData.getColumnView(columns.get(j)).get(i));
            }
            System.out.println();
        }
    }

    private DimensionSource source(FormClass form, String fieldName) {
        return new DimensionSource(form.getId(), findField(form, fieldName).getId());
    }

    private DimensionSource source(FormClass form, String fieldName, String criteria) {
        ResourceId fieldId = findField(form, fieldName).getId();
        DimensionSource dimensionSource = new DimensionSource(form.getId(), fieldId);
        dimensionSource.setCriteria(criteria);
        return dimensionSource;
    }


    private DimensionModel dim(String label, DimensionSource... models) {
        DimensionModel dimModel = new DimensionModel();
        dimModel.setId(Resources.generateId().asString());
        dimModel.setLabel(label);
        dimModel.getSources().addAll(Arrays.asList(models));
        return dimModel;
    }

    private FormField findField(FormClass formClass, String label) {
        List<String> labels = Lists.newArrayList();
        for(FormField field : formClass.getFields()) {
            labels.add(field.getLabel());
            if(field.getLabel().equals(label)) {
                return field;
            }
        }
        throw new IllegalArgumentException(label + ". Have: " + Joiner.on(", ").join(labels));
    }

    private FormClass findForm(String label) {
        for(FormClass form : forms) {
            if(form.getLabel().equals(label)) {
                return form;
            }
        }
        throw new IllegalArgumentException(label);
    }

    private void dumpIndicators() {
        AuthenticatedUser user = new AuthenticatedUser("", 1, "");
        ResourceNode lcca = store.getOwnedOrSharedWorkspaces(user).get(0);
        FolderProjection projection = store.queryTree(user, new FolderRequest(lcca.getId()));

        for(ResourceNode form : projection.getRootNode().getChildren()) {
            if(form.getClassId().equals(FormClass.CLASS_ID)) {
                FormClass formClass = FormClass.fromResource(store.get(form.getId()));

                System.out.println();
                System.out.println(formClass.getLabel());
                System.out.println("-------------------------");

                for(FormField field : formClass.getFields()) {
                    System.out.println("  " +
                        Strings.padEnd(Strings.nullToEmpty(field.getCode()), 15, '.') + toString(field.getType()));
                    System.out.println(Strings.repeat(" ", 17) + field.getLabel());
                }
            }
        }
    }

    private String toString(FieldType type) {

        if(type instanceof CalculatedFieldType) {
            return ((CalculatedFieldType) type).getExpression();
        } else if(type instanceof EnumType) {
            return "Enum: " + Joiner.on(", ").join(((EnumType) type).getValues());
        } else {
            return type.getTypeClass().getId();
        }
    }
}
