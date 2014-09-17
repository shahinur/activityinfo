package org.activityinfo.ui.app.client.page;

public interface PageViewFactory<P extends Place> {

    public boolean accepts(Place place);

    public PageView create(P place);
}
