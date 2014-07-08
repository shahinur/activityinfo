package org.activityinfo.ui.client.component.form.model;

import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.model.formTree.FormTree;

import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates all the information need to build the layout for a form
 */
public class FormViewModel {

    FormTree formTree;
    FormInstance instance;
    Map<ResourceId, FieldViewModel> fields = new HashMap<>();

    public FormViewModel() {
    }

    public FormTree getFormTree() {
        return formTree;
    }

    public FormInstance getInstance() {
        return instance;
    }

    public FieldViewModel getFieldViewModel(ResourceId fieldId) {
        return fields.get(fieldId);
    }

    public FormClass getFormClass() {
        return formTree.getRootFormClasses().values().iterator().next();
    }
}
