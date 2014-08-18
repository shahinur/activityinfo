package org.activityinfo.ui.app.server;

import org.activityinfo.ui.app.client.view.chrome.PageContext;
import org.activityinfo.ui.style.BaseStyleResources;

public class HostPageContext implements PageContext {

    private BaseStyleResources style;

    public HostPageContext(BaseStyleResources style) {
        this.style = style;
    }

    @Override
    public String getBootstrapScriptUrl() {
        return "/AppTest/AppTest.nocache.js";
    }

    @Override
    public String getStylesheetUrl() {
        return "assets/" + style.getStylesheetStrongName();
    }

    @Override
    public String getApplicationTitle() {
        return "ActivityInfo 3.0";
    }
}
