package org.activityinfo.ui.component.form;

import org.activityinfo.model.form.FormField;
import org.activityinfo.ui.component.form.field.FormFieldWidget;

/**
 * Creates a FieldContainer appropriate for the form's context, which might vary
 * from a dialog box to a full page form to a mobile device.
 */
public interface FieldContainerFactory {

    public FieldContainer createContainer(FormField field, FormFieldWidget widget);
}
