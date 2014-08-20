package org.activityinfo.ui.app.client.chrome;

/**
 * Provides the basic information required to construct the HTML page
 * hosting our application
 */
public interface PageContext {

    /**
     *
     * @return the URL of the bootstrap script which will choose a permutation and
     * load the application.
     */
    public String getBootstrapScriptUrl();

    /**
     *
     * @return the URL of the base stylesheet
     */
    public String getStylesheetUrl();

    /**
     * @return the application title for display
     */
    public String getApplicationTitle();
}
