package org.activityinfo.store.cloudsql;

import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.resource.IsResource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.table.ColumnType;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.model.type.Cardinality;
import org.activityinfo.model.type.barcode.BarcodeType;
import org.activityinfo.model.type.barcode.BarcodeValue;
import org.activityinfo.model.type.enumerated.EnumFieldValue;
import org.activityinfo.model.type.enumerated.EnumType;
import org.activityinfo.model.type.enumerated.EnumValue;
import org.activityinfo.model.type.expr.CalculatedFieldType;
import org.activityinfo.model.type.number.Quantity;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.model.type.primitive.TextType;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.Objects;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class TableQueryTest {

    @Rule
    public TestingEnvironment environment = new TestingEnvironment();
    private ResourceId formClassId;
    private ResourceId name;
    private ResourceId age;
    private ResourceId dogYears;
    private ResourceId barcode;
    private ResourceId gender;
    private EnumFieldValue male;
    private EnumFieldValue female;


    @Before
    public void createFormClass() {
        formClassId = Resources.generateId();
        name = Resources.generateId();
        age = Resources.generateId();
        dogYears = Resources.generateId();
        barcode = Resources.generateId();
        gender = Resources.generateId();
        
        FormClass formClass = new FormClass(formClassId);
        formClass.setParentId(Resources.ROOT_ID);
        formClass.setLabel("Test Form");
        formClass.addElement(new FormField(name).setLabel("Name").setType(TextType.INSTANCE));
        formClass.addElement(new FormField(age).setCode("AGE").setLabel("Age").setType(new QuantityType().setUnits("years")));
        formClass.addElement(new FormField(dogYears).setLabel("Dog years").setType(new CalculatedFieldType("AGE*7")));
        formClass.addElement(new FormField(barcode).setLabel("Bar code").setType(BarcodeType.INSTANCE));

        male = new EnumFieldValue(Resources.generateId());
        female = new EnumFieldValue(Resources.generateId());

        EnumType genderType = new EnumType(Cardinality.SINGLE, Arrays.asList(
                new EnumValue(male.getValueId(), "Male"),
                new EnumValue(female.getValueId(), "Female")));

        formClass.addElement(new FormField(gender).setLabel("Gender").setType(genderType));
        put(formClass);
    }


    @Test
    public void basicTable() {

        put(newInstance().set(name, "Bob").set(age, years(42)).set(gender, male));
        put(newInstance().set(name, "Francine").set(age, years(10)).set(gender, female));
        put(newInstance().set(name, "Doug").set(age, years(18)).set(gender, male));

        TableModel tableModel = new TableModel(formClassId);
        tableModel.addColumn("C1").select(ColumnType.STRING).fieldPath(name);
        tableModel.addColumn("C2").select(ColumnType.NUMBER).fieldPath(age);
        tableModel.addColumn("C3").select(ColumnType.STRING).fieldPath(gender);

        TableData data = environment.getStore().queryTable(environment.getUser(), tableModel);

        assertThat(data.getNumRows(), equalTo(3));
        assertThat(data.getColumnView("C1"), hasValues("Bob", "Francine", "Doug"));
        assertThat(data.getColumnView("C2"), hasValues(42, 10, 18));
        assertThat(data.getColumnView("C3"), hasValues("Male", "Female", "Male"));
    }

    @Test
    public void calculatedColumn() {

        put(newInstance().set(name, "Bob").set(age, years(42)));
        put(newInstance().set(name, "Francine").set(age, years(10)));
        put(newInstance().set(name, "Doug").set(age, years(18)));


        TableModel tableModel = new TableModel(formClassId);
        tableModel.addColumn("C1").select(ColumnType.STRING).fieldPath(name);
        tableModel.addColumn("C2").select(ColumnType.NUMBER).fieldPath(dogYears);


        TableData data = environment.getStore().queryTable(environment.getUser(), tableModel);

        assertThat(data.getNumRows(), equalTo(3));
        assertThat(data.getColumnView("C1"), hasValues("Bob", "Francine", "Doug"));
        assertThat(data.getColumnView("C2"), hasValues(42*7, 10*7, 18*7));
    }


    @Test
    public void missingEnumValues() {

        put(newInstance().set(name, "Bob").set(age, years(42)));
        put(newInstance().set(name, "Francine").set(age, years(10)).set(gender, female));

        TableModel tableModel = new TableModel(formClassId);
        tableModel.addColumn("C1").select(ColumnType.STRING).fieldPath(name);
        tableModel.addColumn("C2").select(ColumnType.STRING).fieldPath(gender);


        TableData data = environment.getStore().queryTable(environment.getUser(), tableModel);

        assertThat(data.getColumnView("C1"), hasValues("Bob", "Francine"));
        assertThat(data.getColumnView("C2"), hasValues(null, "Female"));
    }



    @Test
    public void calculatedWithMissingColumn() {

        put(newInstance().set(name, "Bob").set(age, years(42)));
        put(newInstance().set(name, "Francine"));
        put(newInstance().set(name, "Doug").set(age, years(18)));


        TableModel tableModel = new TableModel(formClassId);
        tableModel.addColumn("C1").select(ColumnType.STRING).fieldPath(name);
        tableModel.addColumn("C2").select(ColumnType.NUMBER).fieldPath(dogYears);

        TableData data = environment.getStore().queryTable(environment.getUser(), tableModel);

        assertThat(data.getNumRows(), equalTo(3));
        assertThat(data.getColumnView("C1"), hasValues("Bob", "Francine", "Doug"));
        assertThat(data.getColumnView("C2"), hasValues(42*7, null, 18*7));
    }

    @Test
    public void barcodeAsString() {
        put(newInstance().set(name, "Bob").set(barcode, BarcodeValue.valueOf("01010101")));
        put(newInstance().set(name, "Francine").set(barcode, BarcodeValue.valueOf("XYZ123")));
        put(newInstance().set(name, "Doug").set(barcode, BarcodeValue.valueOf("XOXOXO")));
        put(newInstance().set(age, years(44)));


        TableModel tableModel = new TableModel(formClassId);
        tableModel.addColumn("C1").select(ColumnType.STRING).fieldPath(name);
        tableModel.addColumn("C2").select(ColumnType.STRING).fieldPath(barcode);

        TableData data = environment.getStore().queryTable(environment.getUser(), tableModel);

        assertThat(data.getColumnView("C1"), hasValues("Bob", "Francine", "Doug", null));
        assertThat(data.getColumnView("C2"), hasValues("01010101", "XYZ123", "XOXOXO", null));

    }


    private Quantity years(int i) {
        return new Quantity(i, "years");
    }

    private FormInstance newInstance() {
        return new FormInstance(Resources.generateId(), formClassId);
    }

    private void put(IsResource resource) {
        environment.getStore().put(environment.getUser(), resource.asResource());
    }

    private TypeSafeMatcher<ColumnView> hasValues(final String... values) {
        return new TypeSafeMatcher<ColumnView>() {
            @Override
            protected boolean matchesSafely(ColumnView item) {
                if(item.numRows() != values.length) {
                    return false;
                }
                for(int i=0;i!=values.length;++i) {
                    if(!Objects.equals(item.getString(i), values[i])) {
                        return false;
                    }
                }
                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("column with values").appendValueList("[", ", ", "]", values);
            }
        };
    }


    private TypeSafeMatcher<ColumnView> hasValues(final Number... values) {
        return new TypeSafeMatcher<ColumnView>() {
            @Override
            protected boolean matchesSafely(ColumnView item) {
                if(item.numRows() != values.length) {
                    return false;
                }
                for(int i=0;i!=values.length;++i) {
                    if(values[i] == null) {
                        if(!Double.isNaN(item.getDouble(i))) {
                            return false;
                        }
                    } else {
                        double diff = Math.abs(item.getDouble(i) - values[i].doubleValue());
                        if (diff > 0.001) {
                            return false;
                        }
                    }
                }
                return true;
            }

            @Override
            protected void describeMismatchSafely(ColumnView item, Description mismatchDescription) {
                mismatchDescription.appendText(" was [");
                for(int i=0;i!=item.numRows();++i) {
                    if(i > 0) {
                        mismatchDescription.appendText(", ");
                    }
                    mismatchDescription.appendValue(item.getDouble(i));
                }
                mismatchDescription.appendText("]");
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("column with values").appendValueList("[", ", ", "]", values);
            }
        };
    }


}
