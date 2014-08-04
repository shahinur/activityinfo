package org.activityinfo.ui.client.component.form.field;

import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.model.type.ReferenceValue;

import java.util.List;

public interface ReferenceFieldWidget extends FormFieldWidget<ReferenceValue> {
    List<FormInstance> getRange();
}
