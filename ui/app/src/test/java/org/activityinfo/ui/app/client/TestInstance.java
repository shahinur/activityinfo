package org.activityinfo.ui.app.client;

import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.ui.app.client.request.SaveRequest;

public class TestInstance {
    private final FormInstance instance;
    private TestScenario scenario;
    private FormClass formClass;

    public TestInstance(TestScenario scenario, FormClass formClass) {
        this.scenario = scenario;
        this.formClass = formClass;
        this.instance = new FormInstance(Resources.generateId(), formClass.getId());
    }

    public TestInstance set(String fieldLabel, int quantityValue) {
        this.instance.set(findField(fieldLabel), quantityValue);
        return this;
    }

    private ResourceId findField(String fieldLabel) {
        for(FormField field : formClass.getFields()) {
            if(field.getLabel().equals(fieldLabel)) {
                return field.getId();
            }
        }
        throw new AssertionError("No such field [" + fieldLabel + "]");
    }

    public void save() {
        scenario.application().getRequestDispatcher().execute(new SaveRequest(this.instance));
    }
}
