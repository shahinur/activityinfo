package org.activityinfo.ui.widget.loading;

public class LoadingStylesheet  {

    public static final LoadingStylesheet INSTANCE = new LoadingStylesheet();

    public String loadingContainer() { return "loading-container"; }
    public String indicator() { return "indicator"; }
    public String loading() { return "loading"; }
    public String failed() { return "failed"; }
    public String loaded() { return "loaded"; }

}
