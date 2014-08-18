package org.activityinfo.ui.app.client.page.resource;

import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.ui.app.client.page.Breadcrumb;
import org.activityinfo.ui.app.client.page.PageStore;
import org.activityinfo.ui.vdom.shared.html.Icon;

import java.util.List;

public interface ResourcePage extends PageStore {

    ResourceId getResourceId();

    public String getPageTitle();

    public String getPageDescription();

    public Icon getPageIcon();

    List<Breadcrumb> getBreadcrumbs();


}
