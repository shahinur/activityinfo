package org.activityinfo.ui.widget.form;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiConstructor;
import org.activityinfo.ui.style.BaseStyles;

/**
 * Subclass of {@link RadioButton} that applies our application styles
 */
public class RadioButton extends com.google.gwt.user.client.ui.RadioButton {

    @UiConstructor
    public RadioButton(String name) {
        super(name);
        setStyleName(BaseStyles.RADIO.getClassNames());
    }

    public RadioButton(String name, String label) {
        super(name, label);
        setStyleName(BaseStyles.RADIO.getClassNames());
    }

    public RadioButton(String name, SafeHtml label) {
        super(name, label);
        setStyleName(BaseStyles.RADIO.getClassNames());
    }
}
