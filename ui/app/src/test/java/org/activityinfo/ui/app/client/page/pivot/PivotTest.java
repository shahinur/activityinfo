package org.activityinfo.ui.app.client.page.pivot;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.activityinfo.model.analysis.DimensionModel;
import org.activityinfo.model.analysis.MeasureModel;
import org.activityinfo.model.analysis.PivotTableModel;
import org.activityinfo.model.analysis.SourceModel;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.*;
import org.activityinfo.model.table.*;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.expr.CalculatedFieldType;
import org.activityinfo.service.store.FolderRequest;
import org.activityinfo.store.test.TestResourceStore;
import org.activityinfo.ui.app.client.PreviewRule;
import org.activityinfo.ui.app.client.action.RemoteUpdate;
import org.activityinfo.ui.app.client.request.FetchWorkspaces;
import org.activityinfo.ui.flux.dispatcher.Dispatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PivotTest {

    @Rule
    public PreviewRule preview = new PreviewRule();
    private TestResourceStore store;

    private List<FormClass> forms = Lists.newArrayList();
    private AuthenticatedUser user;


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
    public void selectionPalette() throws IOException {

        user = new AuthenticatedUser("", 1, "");

        Dispatcher dispatcher = preview.getApplication().getDispatcher();
        dispatcher.dispatch(new RemoteUpdate(new FetchWorkspaces(), store.getOwnedOrSharedWorkspaces(user)));

        preview.render(new PivotPage(preview.getApplication()));

    }

    @Test
    public void testCalculations() {
        dumpIndicators();

        FormClass costs = findForm("Documented Cost Information");
        FormClass system = findForm("System Identification per design (aggregated)");


        PivotTableModel model = new PivotTableModel();
        model.getSources().add(costs.getId());
        model.getDimensions().add(dim("Year",
            source(costs, "Year of expediture"),
            source(system, "Year")));
        model.getDimensions().add(dim("Typology",
            source(costs, "Cost typology"),
            source(system, "Budgeted / Build")));

        MeasureModel iniCost = new MeasureModel("IniCost", "Value Initial Cost",
            source(costs, "Value Initial Cost - Cap Hard", "{System Identifier}"));
        model.getMeasures().add(iniCost);

        model.getMeasures().add(new MeasureModel("NumberOfTaps", "Number of individual collection points",
            source(system, "Number individual collection points")));

        aggregate(model);

//
//        DimensionModel dimensionModel = new
//
////
////        PivotTableModel model = new PivotTableModel(Resources.generateId(), null);
////
    }

    private void aggregate(PivotTableModel model) {

        // calculate all of our measures
        for(MeasureModel measure : model.getMeasures()) {
            SourceModel source = measure.getSource();
            TableModel table = new TableModel(source.getSourceId());
            table.addColumn("measure").select(ColumnType.STRING).fieldPath(ResourceId.valueOf(source.getExpression()));

            ColumnModel criteriaColumn = new ColumnModel();
            criteriaColumn.setId("criteria");
            criteriaColumn.setType(ColumnType.NUMBER);
            criteriaColumn.setSource(new CalcFieldSource(source.getSourceId(), source.getCriteria()));
           // table.addColumn(criteriaColumn);

            for (DimensionModel dim : model.getDimensions()) {
                for(SourceModel dimSource : dim.getSources()) {
                    ResourceId sourceId = Preconditions.checkNotNull(dimSource.getSourceId());
                    if(sourceId.equals(measure.getSource().getSourceId())) {
                        table.addColumn(dim.getId()).select(ColumnType.STRING).fieldPath(ResourceId.valueOf(dimSource.getExpression()));
                    }
                }
            }

            TableData tableData = store.queryTable(user, table);
            dumpTable(tableData);
        }

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

    private SourceModel source(FormClass form, String fieldName) {
        return new SourceModel(form.getId(), findField(form, fieldName).getId());
    }

    private SourceModel source(FormClass form, String fieldName, String criteria) {
        ResourceId fieldId = findField(form, fieldName).getId();
        SourceModel sourceModel = new SourceModel(form.getId(), fieldId);
        sourceModel.setCriteria(criteria);
        return sourceModel;
    }


    private DimensionModel dim(String label, SourceModel... models) {
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
        } else {
            return type.getTypeClass().getId();
        }
    }
}
