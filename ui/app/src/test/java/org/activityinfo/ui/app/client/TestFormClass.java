package org.activityinfo.ui.app.client;

import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.type.primitive.TextType;

public class TestFormClass {
    private TestScenario scenario;
    private FormClass formClass;

    public TestFormClass(TestScenario scenario, FormClass formClass) {
        this.scenario = scenario;
        this.formClass = formClass;
    }

    public TestFormClass addTextField(String label) {
        FormField field = new FormField(Resources.generateId());
        field.setLabel(label);
        field.setType(TextType.INSTANCE);
        formClass.addElement(field);
        return this;
    }

    public TestFormClass create() {
        scenario.create(formClass);
        return this;
    }

}
