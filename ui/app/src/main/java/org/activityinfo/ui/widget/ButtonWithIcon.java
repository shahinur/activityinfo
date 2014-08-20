package org.activityinfo.ui.widget;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiConstructor;
import org.activityinfo.ui.style.ButtonStyle;

/**
 * Button with an icon
 */
public class ButtonWithIcon extends Button {


    public interface Templates extends SafeHtmlTemplates {
        @Template("<span class=\"{0}\"></span> {1}")
        public SafeHtml withIcon(String styleNames, String text);
    }

    public Templates TEMPLATES = GWT.create(Templates.class);

    @UiConstructor
    public ButtonWithIcon(ButtonStyle style, String iconStyle, String text) {
        super(style);
        setHTML(TEMPLATES.withIcon(iconStyle, text));
    }
}
