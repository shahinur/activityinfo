package org.activityinfo.model.form;

import org.activityinfo.model.expr.eval.FieldReader;
import org.activityinfo.model.expr.eval.PartialEvaluator;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.record.Records;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.type.ErrorValue;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.expr.CalculatedFieldType;
import org.activityinfo.model.type.number.Quantity;
import org.activityinfo.model.type.number.QuantityType;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

public class PartialEvaluatorTest {

    private FormField a;
    private FormField b;
    private FormField c;
    private FormField d;

    private FormClass formClass;

    private Record record1;

    @Before
    public void setUp() {
        a = new FormField(Resources.generateId());
        a.setCode("A");
        a.setLabel("Field A");
        a.setType(new QuantityType("quarks"));

        b = new FormField(Resources.generateId());
        b.setCode("B");
        b.setLabel("Field B");
        b.setType(new CalculatedFieldType("A*2"));

        c = new FormField(Resources.generateId());
        c.setCode("C");
        c.setLabel("Field C");
        c.setType(new CalculatedFieldType("D/2"));

        d = new FormField(Resources.generateId());
        d.setCode("D");
        d.setLabel("Field D");
        d.setType(new CalculatedFieldType("C+2"));


        formClass = new FormClass(Resources.generateId());
        formClass.addElement(b);
        formClass.addElement(a);
        formClass.addElement(c);
        formClass.addElement(d);

        record1 = Records.builder(formClass.getId()).set(a.getName(), new Quantity(41, "quarks")).build();
    }

    @Test
    public void simple() {


        PartialEvaluator evaluator = new PartialEvaluator(formClass);
        FieldReader aReader = evaluator.partiallyEvaluate(a);
        FieldReader bReader = evaluator.partiallyEvaluate(b);


        assertThat(aReader.readField(record1), equalTo((FieldValue)new Quantity(41, "quarks")));
        assertThat(bReader.readField(record1), equalTo((FieldValue)new Quantity(82)));
    }

    @Test
    public void circularReference() {

        PartialEvaluator evaluator = new PartialEvaluator(formClass);
        FieldReader reader = evaluator.partiallyEvaluate(d);


        assertThat(reader.readField(record1), instanceOf(ErrorValue.class));
    }
}