package org.activityinfo.ui.client.pageView.folder;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiRenderer;

/**
 * Renders an instance with an icon and a label
 */
public interface FormItemRenderer extends UiRenderer {

    void render(SafeHtmlBuilder sb, String label, String description, String link);

}
