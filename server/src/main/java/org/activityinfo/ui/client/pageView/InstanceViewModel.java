package org.activityinfo.ui.client.pageView;

import org.activityinfo.model.form.FormInstance;
import org.activityinfo.ui.client.page.PageId;

public class InstanceViewModel {

    private FormInstance instance;
    private PageId pageId;

    public InstanceViewModel(FormInstance instance, PageId pageId) {
        this.instance = instance;
        this.pageId = pageId;
    }

    public FormInstance getInstance() {
        return instance;
    }

    public PageId getPageId() {
        return pageId;
    }
}
