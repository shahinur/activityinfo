package org.activityinfo.ui.client.pageView;

import org.activityinfo.model.form.FormInstance;

public class InstanceViewModel {
    private FormInstance instance;
    private String path;

    public InstanceViewModel(FormInstance instance, String path) {
        this.instance = instance;
        this.path = path;
    }

    public FormInstance getInstance() {
        return instance;
    }

    public String getPath() {
        return path;
    }
}
