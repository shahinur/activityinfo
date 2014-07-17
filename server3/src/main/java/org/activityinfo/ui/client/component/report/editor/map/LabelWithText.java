package org.activityinfo.ui.client.component.report.editor.map;

import com.extjs.gxt.ui.client.widget.Label;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

public class LabelWithText extends Label {

    public void setText(String text) {
        setHtml(SafeHtmlUtils.htmlEscape(text));
    }
}
