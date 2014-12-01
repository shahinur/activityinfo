package org.activityinfo.ui.client.pageView.formClass;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.promise.Promise;
import org.activityinfo.ui.client.EventBus;
import org.activityinfo.ui.client.page.NavigationEvent;
import org.activityinfo.ui.client.page.NavigationHandler;
import org.activityinfo.ui.client.page.instance.InstancePage;
import org.activityinfo.ui.client.page.instance.InstancePlace;
import org.activityinfo.ui.client.pageView.InstancePageView;
import org.activityinfo.ui.client.pageView.InstanceViewModel;
import org.activityinfo.ui.client.style.Icons;
import org.activityinfo.ui.client.widget.DisplayWidget;

/**
 * Provides a view for a FormClass instance
 */
public class FormClassPageView implements InstancePageView {

    private final Widget rootElement;

    interface FormViewUiBinder extends UiBinder<HTMLPanel, FormClassPageView> {
    }

    private static FormViewUiBinder ourUiBinder = GWT.create(FormViewUiBinder.class);

    @UiField(provided = true)
    Widget tabWidget;

    DisplayWidget<FormInstance> tabView;

    @UiField
    AnchorElement designTab;
    @UiField
    AnchorElement tableTab;
    @UiField
    LIElement designTabContainer;
    @UiField
    LIElement tableTabContainer;

    private final EventBus eventBus;
    private InstanceViewModel view;

    public FormClassPageView(DisplayWidget<FormInstance> tabView, final EventBus eventBus) {
        this.tabView = tabView;
        this.tabWidget = tabView.asWidget();
        this.eventBus = eventBus;

        rootElement = ourUiBinder.createAndBindUi(this);

        Icons.INSTANCE.ensureInjected();
        rootElement.addAttachHandler(new AttachEvent.Handler() {
            @Override
            public void onAttachOrDetach(AttachEvent event) {
                addClickHandlers();
            }
        });
    }

    public Promise<Void> show(final InstanceViewModel view) {

        setTabSelected(view);

        return tabView.show(view.getInstance());
    }

    private void addClickHandlers() {
        Event.sinkEvents(tableTab, Event.ONCLICK);
        Event.setEventListener(tableTab, new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                if(Event.ONCLICK == event.getTypeInt()) {
                    eventBus.fireEvent(new NavigationEvent(
                            NavigationHandler.NAVIGATION_REQUESTED,
                            new InstancePlace(view.getInstance().getId(), InstancePage.TABLE_PAGE_ID)));
                }

            }
        });

        Event.sinkEvents(designTab, Event.ONCLICK);
        Event.setEventListener(designTab, new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                if(Event.ONCLICK == event.getTypeInt()) {
                    eventBus.fireEvent(new NavigationEvent(
                            NavigationHandler.NAVIGATION_REQUESTED,
                            new InstancePlace(view.getInstance().getId(), InstancePage.DESIGN_PAGE_ID)));
                }

            }
        });
    }

    private void setTabSelected(InstanceViewModel view) {
        this.view = view;

        designTabContainer.removeClassName("active");
        tableTabContainer.removeClassName("active");

        if (view.getPageId() == InstancePage.DESIGN_PAGE_ID) {
            designTabContainer.addClassName("active");
        } else if (view.getPageId() == InstancePage.TABLE_PAGE_ID) {
            tableTabContainer.addClassName("active");
        } else {
            throw new UnsupportedOperationException("Unknown pageId: " + view.getPageId());
        }
    }

    @Override
    public Widget asWidget() {
        return rootElement;
    }

}