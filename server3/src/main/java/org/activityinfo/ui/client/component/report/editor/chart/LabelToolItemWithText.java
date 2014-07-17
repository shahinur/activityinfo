package org.activityinfo.ui.client.component.report.editor.chart;

import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

public class LabelToolItemWithText extends LabelToolItem {

    public void setText(String text) {
        setHtml(SafeHtmlUtils.htmlEscape(text));
    }
}
