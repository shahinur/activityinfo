package org.activityinfo.ui.client.component.form;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.ui.client.component.form.field.FormFieldWidget;

/**
 * A widget that wraps a FieldWidget
 */
public interface FieldContainer extends IsWidget {

    FormFieldWidget getFieldWidget();

    void setValid();

    void setInvalid(String message);

}
