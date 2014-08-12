package org.activityinfo.service.tables;

import org.activityinfo.core.shared.expr.eval.FormEvalContext;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.table.ColumnType;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.model.table.TableService;
import org.activityinfo.model.type.CalculatedFieldType;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.number.Quantity;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.ui.client.service.TestResourceStore;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;

public class TableServiceCalcTest {

    private FormClass formClass;

    private TestResourceStore store;
    private TableService tableService;

    @Before
    public void setUp() throws Exception {
        store = new TestResourceStore().load("/dbunit/sites-simple1.json");
        tableService = new TableServiceImpl(store);

        formClass = new FormClass(ResourceId.generateId());
        formClass.setLabel("Calculation Test");
        formClass.setOwnerId(CuidAdapter.resourceId(CuidAdapter.USER_DOMAIN, 1));

        FormField expField = new FormField(ResourceId.generateId());
        expField.setType(new QuantityType().setUnits("currency"));
        expField.setLabel("Expenditure");
        expField.setCode("EXP");
        formClass.addElement(expField);

        FormField waterAllocField = new FormField(ResourceId.generateId());
        waterAllocField.setType(new QuantityType().setUnits("%"));
        waterAllocField.setLabel("Allocation watter programme");
        waterAllocField.setCode("WATER_ALLOC");
        formClass.addElement(waterAllocField);

        FormField pctInitialField = new FormField(ResourceId.generateId());
        pctInitialField.setType(new QuantityType().setUnits("%"));
        pctInitialField.setLabel("Initial Cost - Not specified");
        pctInitialField.setCode("PCT_INITIAL");
        formClass.addElement(pctInitialField);

        FormField pctInitialHardField = new FormField(ResourceId.generateId());
        pctInitialHardField.setType(new QuantityType().setUnits("%"));
        pctInitialHardField.setLabel("Initial Cost - Cap Hard");
        pctInitialHardField.setCode("PCT_INITIAL_HARD");
        formClass.addElement(pctInitialHardField);

        FormField pctInitialSoftField = new FormField(ResourceId.generateId());
        pctInitialSoftField.setType(new QuantityType().setUnits("%"));
        pctInitialSoftField.setLabel("Initial Cost - Cap Soft");
        pctInitialSoftField.setCode("PCT_INITIAL_SOFT");
        formClass.addElement(pctInitialSoftField);

        FormField pctExtensionField = new FormField(ResourceId.generateId());
        pctExtensionField.setType(new QuantityType().setUnits("%"));
        pctExtensionField.setLabel("Extension Cost - Not specified");
        pctExtensionField.setCode("PCT_EXTENSION");
        formClass.addElement(pctExtensionField);

        FormField pctExtensionHardField = new FormField(ResourceId.generateId());
        pctExtensionHardField.setType(new QuantityType().setUnits("%"));
        pctExtensionHardField.setLabel("Extension Cost - Hard");
        pctExtensionHardField.setCode("PCT_EXTENSION_HARD");
        formClass.addElement(pctExtensionHardField);
        
        FormField pctExtensionSoftField = new FormField(ResourceId.generateId());
        pctExtensionSoftField.setType(new QuantityType().setUnits("%"));
        pctExtensionSoftField.setLabel("Extension Cost - Soft");
        pctExtensionSoftField.setCode("PCT_EXTENSION_SOFT");
        formClass.addElement(pctExtensionSoftField);
        
        FormField pctOpField = new FormField(ResourceId.generateId());
        pctOpField.setType(new QuantityType().setUnits("%"));
        pctOpField.setLabel("Operational Cost");
        pctOpField.setCode("PCT_OP");
        formClass.addElement(pctOpField);

        FormField pctMaintenanceField = new FormField(ResourceId.generateId());
        pctMaintenanceField.setType(new QuantityType().setUnits("%"));
        pctMaintenanceField.setLabel("Maintenance Cost");
        pctMaintenanceField.setCode("PCT_MAINTENANCE");
        formClass.addElement(pctMaintenanceField);
        
        FormField pctOpMaintField = new FormField(ResourceId.generateId());
        pctOpMaintField.setType(new QuantityType().setUnits("%"));
        pctOpMaintField.setLabel("Operational & Maintenance Cost");
        pctOpMaintField.setCode("PCT_OP_MAINT");
        formClass.addElement(pctOpMaintField);

        FormField waterExpField = new FormField(ResourceId.generateId());
        waterExpField.setLabel("Expenditure on water programme");
        waterExpField.setCode("WATER_EXP");
        waterExpField.setType(new CalculatedFieldType("{EXP}*({WATER_ALLOC}/100)"));
        formClass.addElement(waterExpField);
        
        FormField initialField = new FormField(ResourceId.generateId());
        initialField.setLabel("Value of Initial Cost - Not specified");
        initialField.setCode("INITIAL");
        initialField.setType(new CalculatedFieldType("{WATER_EXP}*({PCT_INITIAL}/100)"));
        formClass.addElement(initialField);

        FormField initialHardField = new FormField(ResourceId.generateId());
        initialHardField.setLabel("Value of Initial Cost - Cap Hard");
        initialHardField.setCode("INITIAL_HARD");
        initialHardField.setType(new CalculatedFieldType("{WATER_EXP}*({PCT_INITIAL_HARD}/100)"));
        formClass.addElement(initialHardField);

        FormField initialSoftField = new FormField(ResourceId.generateId());
        initialSoftField.setLabel("Value of Initial Cost – Cap Soft");
        initialSoftField.setCode("INITIAL_SOFT");
        initialSoftField.setType(new CalculatedFieldType("{WATER_EXP}*({PCT_INITIAL_SOFT}/100)"));
        formClass.addElement(initialSoftField);
        
        FormField initialTotalField = new FormField(ResourceId.generateId());
        initialTotalField.setLabel("Total Value of Initial Cost");
        initialTotalField.setCode("INITIAL_TOTAL");
        initialTotalField.setType(new CalculatedFieldType("{INITIAL}+{INITIAL_HARD}+{INITIAL_SOFT}"));
        formClass.addElement(initialSoftField);

        formClass.addElement(waterAllocField);
        formClass.addElement(pctInitialField);
        formClass.addElement(pctInitialHardField);
        formClass.addElement(pctInitialSoftField);
        formClass.addElement(pctExtensionField);
        formClass.addElement(pctExtensionHardField);
        formClass.addElement(pctExtensionSoftField);
        formClass.addElement(pctOpField);
        formClass.addElement(pctMaintenanceField);
        formClass.addElement(pctOpMaintField);
        formClass.addElement(waterExpField);

        formClass.addElement(initialField);
        formClass.addElement(initialHardField);
        formClass.addElement(initialSoftField);
        formClass.addElement(initialTotalField);

        store.put(formClass.asResource());
    }

    @Test
    public void persistence() {
        FormClass reform = FormClass.fromResource(store.get(formClass.getId()));
        assertHasFieldWithLabel(reform, "Expenditure");
        assertThat(reform.getFields(), Matchers.<FormField>hasItem(hasProperty("code", equalTo("EXP"))));

        assertHasFieldWithLabel(reform, "Allocation watter programme");
        assertHasFieldWithLabel(reform, "Initial Cost - Not specified");
        assertHasFieldWithLabel(reform, "Initial Cost - Cap Hard");
        assertHasFieldWithLabel(reform, "Initial Cost - Cap Soft");
        assertHasFieldWithLabel(reform, "Extension Cost - Not specified");
        assertHasFieldWithLabel(reform, "Extension Cost - Hard");
        assertHasFieldWithLabel(reform, "Extension Cost - Soft");
        assertHasFieldWithLabel(reform, "Operational Cost");
        assertHasFieldWithLabel(reform, "Maintenance Cost");
        assertHasFieldWithLabel(reform, "Operational & Maintenance Cost");
        assertHasFieldWithLabel(reform, "Expenditure on water programme");
        assertHasFieldWithLabel(reform, "Value of Initial Cost - Not specified");
        assertHasFieldWithLabel(reform, "Value of Initial Cost - Cap Hard");
        assertHasFieldWithLabel(reform, "Value of Initial Cost – Cap Soft");
        assertHasFieldWithLabel(reform, "Total Value of Initial Cost");
    }

    @Test
    public void formContextTest() {

        FormInstance instance = new FormInstance(ResourceId.generateId(), formClass.getId());
        instance.set(fieldId("EXP"), new Quantity(3, "currency"));
        instance.set(fieldId("WATER_ALLOC"), 400);
        instance.set(fieldId("PCT_INITIAL"), 50);
        instance.set(fieldId("PCT_INITIAL_HARD"), 20);
        instance.set(fieldId("PCT_INITIAL_SOFT"), 30);

        FormEvalContext context = new FormEvalContext(formClass, instance.asResource());
        assertThat(context.resolveSymbol("WATER_EXP"), equalTo((FieldValue) new Quantity(12.0)));
    }
    
    @Test
    public void calculations() {

        FormInstance instance = new FormInstance(ResourceId.generateId(), formClass.getId());
        instance.set(fieldId("EXP"), new Quantity(3, "currency"));
        instance.set(fieldId("WATER_ALLOC"), 400);
        instance.set(fieldId("PCT_INITIAL"), 50);
        instance.set(fieldId("PCT_INITIAL_HARD"), 20);
        instance.set(fieldId("PCT_INITIAL_SOFT"), 30);

        store.put(instance.asResource());

        TableModel tableModel = new TableModel(formClass.getId());
        tableModel.addColumn("EXP").select(ColumnType.NUMBER).fieldPath(fieldId("EXP"));
        tableModel.addColumn("WATER_EXP").select(ColumnType.NUMBER).fieldPath(fieldId("WATER_EXP"));
        tableModel.addColumn("INITIAL").select(ColumnType.NUMBER).fieldPath(fieldId("INITIAL"));
        tableModel.addColumn("INITIAL_HARD").select(ColumnType.NUMBER).fieldPath(fieldId("INITIAL_HARD"));
        tableModel.addColumn("INITIAL_SOFT").select(ColumnType.NUMBER).fieldPath(fieldId("INITIAL_SOFT"));
        tableModel.addColumn("INITIAL_TOTAL").select(ColumnType.NUMBER).fieldPath(fieldId("INITIAL_TOTAL"));

        TableData data = tableService.buildTable(tableModel);

        assertThat(data.getNumRows(), equalTo(1));

        int row = 0;
        assertThat(data.getColumnView("EXP").getDouble(row), equalTo(3.0));
        assertThat(data.getColumnView("WATER_EXP").getDouble(row), equalTo(12.0));
        assertThat(data.getColumnView("INITIAL").getDouble(row), equalTo(6.0));
        assertThat(data.getColumnView("INITIAL_HARD").getDouble(row), closeTo(2.4, 0.00001));
        assertThat(data.getColumnView("INITIAL_SOFT").getDouble(row), closeTo(3.6, 0.00001));
        assertThat(data.getColumnView("INITIAL_TOTAL").getDouble(row), equalTo(12.0));

        assertThat(data.getColumnView("WATER_EXP").getDouble(row), equalTo(12.0));
    }


    private FormField field(String code) {
        for (FormField field : formClass.getFields()) {
            if (code.equals(field.getCode())) {
                return field;
            }
        }
        throw new RuntimeException("Enable to find field with code: " + code);
    }

    private ResourceId fieldId(String code) {
        return field(code).getId();
    }


    private static FormField assertHasFieldWithLabel(FormClass formClass, String label) {
        for (FormField field : formClass.getFields()) {
            if (label.equals(field.getLabel())) {
                return field;
            }
        }
        throw new RuntimeException("No field with label: " + label);
    }
}
