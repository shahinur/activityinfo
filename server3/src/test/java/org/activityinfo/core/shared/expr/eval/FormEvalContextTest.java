package org.activityinfo.core.shared.expr.eval;

import org.activityinfo.core.shared.expr.diagnostic.CircularReferenceException;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.CalculatedFieldType;
import org.junit.Test;

public class FormEvalContextTest {

    @Test(expected = CircularReferenceException.class)
    public void circularRefs() {

        FormField a = new FormField(ResourceId.generateId());
        a.setCode("A");
        a.setType(new CalculatedFieldType("B+1"));
        a.setLabel("A");

        FormField b = new FormField(ResourceId.generateId());
        b.setCode("B");
        b.setType(new CalculatedFieldType("A/50"));
        b.setLabel("B");

        FormClass formClass = new FormClass(ResourceId.generateId());
        formClass.addElement(a);
        formClass.addElement(b);

        FormEvalContext context = new FormEvalContext(formClass, null);
        context.setInstance(new FormInstance(ResourceId.generateId(), formClass.getId()));

        context.getFieldValue(a.getId());

    }

}