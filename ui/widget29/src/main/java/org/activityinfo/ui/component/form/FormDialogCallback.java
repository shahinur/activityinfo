package org.activityinfo.ui.component.form;


import org.activityinfo.model.form.FormInstance;

public interface FormDialogCallback {

    void onPersisted(FormInstance instance);
}
