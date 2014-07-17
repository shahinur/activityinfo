package org.activityinfo.ui.client.component.formdesigner.palette;

import com.google.gwt.user.client.ui.Label;
import org.activityinfo.ui.client.component.formdesigner.Metrics;

/**
 * Label widget that users can drag from the palette
 * onto the EditableFormPanel
 */
public class FieldLabel extends Label {

    private final FieldTemplate fieldTemplate;

    public FieldLabel(FieldTemplate fieldTemplate) {
        super(fieldTemplate.getLabel());
        this.fieldTemplate = fieldTemplate;
        setStyleName(Metrics.DEFAULT_STYLE_NAME);
        setWidth(Metrics.SOURCE_CONTROL_WIDTH_PX + "px");
    }

    public FieldTemplate getFieldTemplate() {
        return fieldTemplate;
    }
}
