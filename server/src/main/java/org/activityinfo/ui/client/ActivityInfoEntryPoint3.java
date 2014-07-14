package org.activityinfo.ui.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.RootPanel;
import org.activityinfo.ui.client.chrome.LeftPanel;
import org.activityinfo.ui.client.chrome.MainPanel;
import org.activityinfo.ui.client.style.BaseStylesheet3;

/**
 * Entry Point for AI Version 3
 */
public class ActivityInfoEntryPoint3 implements EntryPoint {

    @Override
    public void onModuleLoad() {

       // hidePreloader();

        Document.get().getBody().addClassName("bs");

        BaseStylesheet3.INSTANCE.ensureInjected();

        RootPanel root = RootPanel.get("root");
        root.add(new LeftPanel());
        root.add(new MainPanel());

    }

    private void hidePreloader() {
        Document.get().getElementById("preloader").getStyle().setDisplay(Style.Display.NONE);
    }
}
