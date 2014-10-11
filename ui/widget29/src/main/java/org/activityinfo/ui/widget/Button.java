package org.activityinfo.ui.widget;

import com.google.gwt.uibinder.client.UiConstructor;
import org.activityinfo.ui.style.ButtonStyle;

/**
 * Subclass of {@link ButtonWithSize} that allows to create button without size
 * definition (default).
 */
public class Button extends ButtonWithSize {

    @UiConstructor
    public Button(ButtonStyle style) {
        super(style, null);
    }
}
