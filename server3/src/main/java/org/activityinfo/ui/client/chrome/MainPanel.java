package org.activityinfo.ui.client.chrome;

import com.google.common.base.Function;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Provider;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.promise.Promise;
import org.activityinfo.ui.client.page.instance.InstancePlace;
import org.activityinfo.ui.client.pageView.InstancePageViewFactory;
import org.activityinfo.ui.client.pageView.InstanceViewModel;
import org.activityinfo.ui.client.widget.LoadingPanel;
import org.activityinfo.ui.client.widget.loading.PageLoadingPanel;

import javax.annotation.Nullable;

public class MainPanel implements IsWidget {

    private final FlowPanel panel;


    private HeaderBar headerBar;
    private LoadingPanel<InstanceViewModel> loadingPanel;
    private ResourceLocator locator;

    public MainPanel(ResourceLocator locator) {
        this.locator = locator;

        headerBar = new HeaderBar();
        loadingPanel = new LoadingPanel<>(new PageLoadingPanel());
        panel = new FlowPanel();
        panel.setStyleName("mainpanel");
        panel.add(headerBar);
        panel.add(loadingPanel);
    }

    @Override
    public Widget asWidget() {
        return panel;
    }

    public HeaderBar getHeaderBar() {
        return headerBar;
    }

    public void navigate(final InstancePlace place) {
        loadingPanel.setDisplayWidgetProvider(
                new InstancePageViewFactory(locator));

        loadingPanel.show(new Provider<Promise<InstanceViewModel>>() {
            @Override
            public Promise<InstanceViewModel> get() {
                return locator.getFormInstance(place.getInstanceId())
                              .then(new Function<FormInstance, InstanceViewModel>() {
                                  @Nullable
                                  @Override
                                  public InstanceViewModel apply(@Nullable FormInstance input) {
                                      return new InstanceViewModel(input, place.getView());
                                  }
                              });
            }
        });
    }
}