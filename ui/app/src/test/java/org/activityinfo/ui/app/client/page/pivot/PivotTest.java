package org.activityinfo.ui.app.client.page.pivot;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.activityinfo.model.analysis.*;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.*;
import org.activityinfo.model.table.Bucket;
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
    private FormClass costs;
    private FormClass wp;
    private FormClass camp;

    @Before
    public void loadData() throws IOException {
        store = new TestResourceStore();
        store.load("lcca.json");

        for(Resource resource : store.all()) {
            if(FormClass.CLASS_ID.asString().equals(resource.isString("classId"))) {
                forms.add(FormClass.fromResource(resource));
            }
        }

        costs = findForm("Documented Cost Information");
        wp = findForm("Water Collection Point information over time - Monthly Reports");
        camp = findForm("Camp Information per month  - Monthly Reports");

    }


//    @Test
//    public void hp1() throws Exception {
//
//        // aggregate to zone, household id
//        // aggregate to weekday and water point
//        // weight the results by (sat = 1, other=2)
//        // number of households per water point per week
//        // weight by the sampling fraction
//
//        // 1 km = 30 min
//        // 6 min < ok
//
//
//    }
//



    @Test
    public void serialization() throws Exception {

        PivotTableModel model = new PivotTableModel();

        MeasureModel iniCost = new MeasureModel();
        iniCost.setId("HP_1_IN_HDW");
        iniCost.setLabel("HP1 Capital cost hand-dug wells as designed: Initial");
        iniCost.setSource(costs.getId());
        iniCost.setValueExpression("V_InCostAgg");
        iniCost.setMeasurementType(MeasurementType.FLOW);
        iniCost.setCriteriaExpression("[System Identifier]=='Hand Dug Wells (All)'");
        model.addMeasure(iniCost);

        Record record = model.asRecord();

        execute(PivotTableModel.fromRecord(record));
    }

    @Test
    public void hp1() throws Exception {
        dumpIndicators();


        PivotTableModel model = new PivotTableModel();
        model.getDimensions().add(dim("Year", dimSource(costs, "[Year of expediture]"), dimSource(wp, "Year")));
        model.getDimensions().add(dim("System",
            dimSource(costs, "[System Identifier]"),
            dimSource(wp, "[Site].[Water Collection Point Identifier]")));
        model.getDimensions().add(dim("Typology", dimSource(costs, "[Cost typology]")));


        MeasureModel iniCost = new MeasureModel();
        iniCost.setId("HP_1_IN_HDW");
        iniCost.setLabel("HP1 Capital cost hand-dug wells as designed: Initial");
        iniCost.setSource(costs.getId());
        iniCost.setValueExpression("V_InCostAgg");
        iniCost.setMeasurementType(MeasurementType.FLOW);
        iniCost.setCriteriaExpression("[System Identifier]=='Hand Dug Wells (All)'");
        model.addMeasure(iniCost);

        MeasureModel extCosts = new MeasureModel();
        extCosts.setId("extCosts");
        extCosts.setLabel("HP1 Capital cost hand-dug wells as designed: Extended");
        extCosts.setSource(costs.getId());
        extCosts.setValueExpression("V_ExtCostAgg");
        extCosts.setCriteriaExpression("[System Identifier]=='Hand Dug Wells (All)'");
        extCosts.setMeasurementType(MeasurementType.FLOW);
        model.addMeasure(extCosts);

        MeasureModel wellCount = new MeasureModel();
        wellCount.setId("wellCount");
        wellCount.setLabel("HDW Count");
        wellCount.setSource(wp.getId());
        wellCount.setValueExpression("[Nr actual HDW]");
        wellCount.setMeasurementType(MeasurementType.STOCK);
        wellCount.setCriteriaExpression("[Site].[Water Collection Point Identifier]='Water trucking/pumping'");
        model.addMeasure(wellCount);

        execute(model);
    }

    private void execute(PivotTableModel model) throws Exception {
        CubeBuilder cubeBuilder = new CubeBuilder(store);
        List<Bucket> buckets = cubeBuilder.buildCube(model);

        dump(model, buckets);
    }


    @Test
    public void hp3() throws Exception {
        dumpIndicators();

        PivotTableModel model = new PivotTableModel();

        MeasureModel population = new MeasureModel();
        population.setSource(camp.getId());
        population.setValueExpression("[Camp Population]");
        population.setCriteriaExpression("[Year]=='2012'");
        population.setMeasurementType(MeasurementType.STOCK);
        model.addMeasure(population);

        MeasureModel points = new MeasureModel();
        points.setSource(wp.getId());
        points.setValueExpression("[TPSfnct]");
        points.setCriteriaExpression("[Site].[Water Collection Point Identifier]=='Water trucking' && [Year]=='2012'");
        points.setMeasurementType(MeasurementType.STOCK);
        //points.setAggregationFunction(AggregationFunction.MEAN);
        model.addMeasure(points);

        execute(model);
    }


    @Test
    public void hp3_ops() throws Exception {
        dumpIndicators();

        PivotTableModel model = new PivotTableModel();
        model.getDimensions().add(dim("Year", dimSource(costs, "[Year of expediture]")));
        model.getDimensions().add(dim("Typology", dimSource(costs, "[Cost typology]")));
        model.getDimensions().add(dim("System Identifier", dimSource(costs, "[System Identifier]")));

        for(String name : Arrays.asList("V_OperCost", "V_MainCost", "V_OpMaCost", "V_OpMaAgg")) {

            MeasureModel cost = new MeasureModel();
            cost.setSource(costs.getId());
            cost.setId(name);
            cost.setValueExpression(name);
            cost.setCriteriaExpression("[System Identifier]=='Emergency (Trucking/prov. PS)'");
            cost.setMeasurementType(MeasurementType.FLOW);
            //cost.setAggregationFunction(AggregationFunction.SUM);
            model.addMeasure(cost);
        }

        execute(model);
    }



    @Test
    public void ps34() throws Exception {
        dumpIndicators();

        PivotTableModel model = new PivotTableModel();
        model.getDimensions().add(dim("Year", dimSource(costs, "[Year of expediture]")));
        model.getDimensions().add(dim("Typology", dimSource(costs, "[Cost typology]")));
        model.getDimensions().add(dim("System Identifier", dimSource(costs, "[System Identifier]")));

        MeasureModel numerator = new MeasureModel();
        numerator.setSource(costs.getId());
        numerator.setId("numerator");
        numerator.setMeasurementType(MeasurementType.FLOW);
        numerator.setValueExpression("V_CapMaInSys+V_CapMaSysExt");
        model.addMeasure(numerator);

        execute(model);
    }


    @Test
    public void s10() throws Exception {
        dumpIndicators();

        PivotTableModel model = new PivotTableModel();
        model.getDimensions().add(dim("Year", dimSource(costs, "[Year of expediture]")));
        model.getDimensions().add(dim("Typology", dimSource(costs, "[Cost typology]")));
        model.getDimensions().add(dim("System Identifier", dimSource(costs, "[System Identifier]")));

        MeasureModel numerator = new MeasureModel();
        numerator.setSource(costs.getId());
        numerator.setId("numerator");
        numerator.setLabel("Budgeted - Extension");
        numerator.setMeasurementType(MeasurementType.FLOW);
        numerator.setValueExpression("V_DirSupUnsp");
//        numerator.setCriteriaExpression("[Cost typology]=='Budgeted' && ([System Identifier] != 'Hand Dug Wells (All)') && " +
//            "([System Identifier] != 'Emergency (Trucking/prov. PS)')");
        model.addMeasure(numerator);

        execute(model);
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
                    System.out.println("  " + field.getId().asString() + "   " +
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


    public void dumpModel(PivotTableModel model) {

        for(MeasureModel measure : model.getMeasures()) {

            System.out.println("AGGREGATE " + measure.getLabel() + ": " + measure.getValueExpression());
            System.out.println("\tFROM [" + getFormLabel(measure.getSourceId()) + "]");

            if(!Strings.isNullOrEmpty(measure.getCriteriaExpression())) {
                System.out.println("\tWHERE " + measure.getCriteriaExpression());
            }

            System.out.println("\tBY");
            for(DimensionModel dim : model.getDimensions()) {
                System.out.println("\t\t" + dim.getId() + " = " + dim.getSource(measure.getSourceId()));
            }

//            System.out.println("\tWITH");
//            measure.ge

        }

    }


    public void dump(PivotTableModel model, List<Bucket> buckets) {

        List<String> headers = Lists.newArrayList();
        headers.add("Measure");
        for(DimensionModel dimension : model.getDimensions()) {
            headers.add(dimension.getLabel());
        }
        headers.add("Value");
        System.out.println(Joiner.on("\t").join(headers));

        for(Bucket bucket : buckets) {
            List<String> cells = Lists.newArrayList();
            cells.add(bucket.getDimensionValue("_measure"));
            for (DimensionModel dim : model.getDimensions()) {
                cells.add(Strings.nullToEmpty(bucket.getDimensionValue(dim.getId())));
            }
            cells.add(Double.toString(bucket.getValue()));
            System.out.println(Joiner.on("\t").join(cells));
        }
    }

    private String getFormLabel(ResourceId sourceId) {
        return store.get(sourceId).getString(FormClass.LABEL_FIELD_ID);
    }
}
