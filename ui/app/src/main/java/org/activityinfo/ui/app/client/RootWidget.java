package org.activityinfo.ui.app.client;

import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.impl.HyperlinkImpl;
import org.activityinfo.ui.app.client.store.AppStores;
import org.activityinfo.ui.app.client.view.chrome.Chrome;
import org.activityinfo.ui.vdom.client.ElementBuilder;
import org.activityinfo.ui.vdom.client.flux.store.Store;
import org.activityinfo.ui.vdom.client.flux.store.StoreChangeListener;
import org.activityinfo.ui.vdom.client.patch.Patch;
import org.activityinfo.ui.vdom.shared.diff.Diff;
import org.activityinfo.ui.vdom.shared.diff.VDiff;
import org.activityinfo.ui.vdom.shared.tree.VTree;

public class RootWidget extends Widget implements StoreChangeListener {

    private static HyperlinkImpl historyImpl = GWT.create(HyperlinkImpl.class);

    private ElementBuilder builder;
    private AppStores appState;
    private VTree tree;


    public RootWidget(AppStores appState) {
        this.appState = appState;
        builder = new ElementBuilder();

        // Grab the existing body element as our node
        setElement(Document.get().getBody());

        // Replicate the vTree used to construct the empty page
        // on the server and set that as our initial tree
        tree = Chrome.theBody(appState);

        // Listen for any changes to the workspace
        appState.getWorkspaceStore().addChangeListener(this);
        appState.getRouter().addChangeListener(this);

        // Sink DOM Events at the root level
        // we'll redirect them to the correct view
        sinkEvents(Event.ONMOUSEDOWN);

        scheduleUpdate();
    }

    private void scheduleUpdate() {
        Scheduler.get().scheduleFinally(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                updateDom();
            }
        });
    }

    private void updateDom() {
        VTree newTree =  Chrome.theBody(appState);
        VDiff diff = Diff.diff(tree, newTree);

        Patch patch = new Patch();
        patch.patch(getElement(), diff);
        tree = newTree;
    }

    @Override
    public void onStoreChanged(Store store) {
        updateDom();
        // TODO: the page component needs to be the one listenin!
        appState.getRouter().getActivePage().addChangeListener(this);
//        if(pageLoading &&
//           appState.getWorkspaceStore().getLoadingStatus() == LoadingStatus.LOADED) {
//
//            PagePreLoader.hidePreloader();
//        }
    }


    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        if (DOM.eventGetType(event) == Event.ONCLICK && historyImpl.handleAsClick(event)) {
            Element element = event.getEventTarget().cast();
            if(element.getTagName().equals("a")) {
                AnchorElement a = element.cast();
                String href = Strings.nullToEmpty(a.getHref());
                if(href.startsWith("#")) {
                    navigate(href);
                    event.preventDefault();
                }
            }
            event.preventDefault();
        }
    }

    private void navigate(String href) {
        History.newItem(href.substring(1));
        updateDom();
    }


}
