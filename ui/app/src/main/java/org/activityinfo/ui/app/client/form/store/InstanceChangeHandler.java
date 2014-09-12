package org.activityinfo.ui.app.client.form.store;

public interface InstanceChangeHandler {

    void updateField(UpdateFieldAction action);

    void appendListItem(AddListItemAction action);
}
