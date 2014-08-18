package org.activityinfo.ui.app.client.view.chrome;

import org.activityinfo.ui.style.BaseStyleResources;

public class TestPageContext implements PageContext {

    private final BaseStyleResources resources;

    public TestPageContext(BaseStyleResources resources) {
        this.resources = resources;
    }

    @Override
    public String getBootstrapScriptUrl() {
        return "about:blank";
    }

    @Override
    public String getStylesheetUrl() {
        return resources.getStylesheetStrongName();
    }

    @Override
    public String getApplicationTitle() {
        return "AI Test";
    }
}
