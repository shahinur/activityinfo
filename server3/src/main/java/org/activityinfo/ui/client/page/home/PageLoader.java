package org.activityinfo.ui.client.page.home;

import com.google.common.base.Function;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.model.system.FolderClass;
import org.activityinfo.core.shared.criteria.ClassCriteria;
import org.activityinfo.core.shared.criteria.CriteriaIntersection;
import org.activityinfo.core.shared.criteria.ParentCriteria;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.ui.client.page.*;
import org.activityinfo.ui.client.page.instance.InstancePage;
import org.activityinfo.ui.client.page.instance.InstancePlace;
import org.activityinfo.ui.client.style.BaseStylesheet;

import javax.annotation.Nullable;
import java.util.List;

public class PageLoader implements org.activityinfo.ui.client.page.PageLoader {

    private ResourceLocator resourceLocator;

    @Inject
    public PageLoader(NavigationHandler pageManager,
                      PageStateSerializer placeSerializer,
                      ResourceLocator resourceLocator) {

        this.resourceLocator = resourceLocator;

        pageManager.registerPageLoader(InstancePage.PAGE_ID, this);
        placeSerializer.registerParser(InstancePage.PAGE_ID, new InstancePlace.Parser());

    }

    @Override
    public void load(final PageId pageId, final PageState pageState, final AsyncCallback<Page> callback) {

        BaseStylesheet.INSTANCE.ensureInjected();

        GWT.runAsync(new RunAsyncCallback() {
            @Override
            public void onSuccess() {
                if (pageState instanceof InstancePlace) {
                    InstancePage page = new InstancePage(resourceLocator);
                    page.navigate(pageState);
                    callback.onSuccess(page);
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                callback.onFailure(throwable);
            }
        });
    }

}
