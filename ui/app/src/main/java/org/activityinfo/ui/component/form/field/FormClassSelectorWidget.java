package org.activityinfo.ui.component.form.field;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.ReferenceValue;
import org.activityinfo.promise.Promise;

public class FormClassSelectorWidget implements FormFieldWidget<ReferenceValue> {

    @Override
    public void setReadOnly(boolean readOnly) {

    }

    @Override
    public Promise<Void> setValue(ReferenceValue value) {
        return null;
    }

    @Override
    public void setType(FieldType type) {

    }

    @Override
    public void clearValue() {

    }

    @Override
    public Widget asWidget() {
        return null;
    }

    interface FormClassSelectorWidgetUiBinder extends UiBinder<HTMLPanel, FormClassSelectorWidget> {
    }

    private static FormClassSelectorWidgetUiBinder ourUiBinder = GWT.create(FormClassSelectorWidgetUiBinder.class);

    public FormClassSelectorWidget() {
        HTMLPanel rootElement = ourUiBinder.createAndBindUi(this);

    }
}