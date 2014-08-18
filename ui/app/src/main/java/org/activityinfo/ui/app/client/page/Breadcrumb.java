package org.activityinfo.ui.app.client.page;

public class Breadcrumb {

    private String label;
    private String href;

    public Breadcrumb(String label, String href) {
        this.label = label;
        this.href = href;
    }

    public String getLabel() {
        return label;
    }

    public String getHref() {
        return href;
    }
}
