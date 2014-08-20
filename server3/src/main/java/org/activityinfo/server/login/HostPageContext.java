package org.activityinfo.server.login;

import org.activityinfo.ui.app.client.chrome.PageContext;
import org.activityinfo.ui.style.BaseStyleResources;

public class HostPageContext implements PageContext {

    private BaseStyleResources style;

    public HostPageContext(BaseStyleResources style) {
        this.style = style;
    }

    @Override
    public String getBootstrapScriptUrl() {
        return "/AI/AI.nocache.js";
    }

    @Override
    public String getStylesheetUrl() {
        return "/assets/" + style.getStylesheetStrongName();
    }

    @Override
    public String getApplicationTitle() {
        return "AI 3.0 Beta";
    }
}
