package org.activityinfo.ui.app.client;

import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.system.FolderClass;
import org.activityinfo.ui.app.client.request.FetchFolder;

public class TestFolder {


    private TestScenario testScenario;
    private ResourceId id;

    public TestFolder(TestScenario testScenario, ResourceId id) {
        this.testScenario = testScenario;
        this.id = id;
    }

    public TestFolder createFolder(String label) {
        FormInstance instance = new FormInstance(Resources.generateId(), FolderClass.CLASS_ID);
        instance.setOwnerId(id);

        if(id.equals(instance.getId())) {
            throw new AssertionError(instance.getId() + " == " + id);
        }

        instance.set(FolderClass.LABEL_FIELD_ID, label);
        testScenario.create(instance);
        return new TestFolder(testScenario, instance.getId());
    }

    public TestFormClass newFormClass(String label) {
        FormClass formClass = new FormClass(Resources.generateId());
        formClass.setOwnerId(id);
        formClass.setLabel(label);

        return new TestFormClass(testScenario, formClass);
    }

    public ResourceId getId() {
        return id;
    }

    public TestFolder fetch() {
        testScenario.application().getRequestDispatcher().execute(new FetchFolder(id));
        return this;
    }
}
