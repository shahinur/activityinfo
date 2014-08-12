package org.activityinfo.ui.client.component.form.field;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.Cardinality;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.ReferenceValue;
import org.activityinfo.model.type.enumerated.EnumFieldValue;
import org.activityinfo.model.type.enumerated.EnumType;
import org.activityinfo.model.type.enumerated.EnumValue;
import org.activityinfo.promise.Promise;
import org.activityinfo.ui.client.widget.RadioButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EnumFieldWidget implements FormFieldWidget<EnumFieldValue> {


    public interface Templates extends SafeHtmlTemplates {

        @Template("{0} <span class='enum-edit-controls'>[ <span class='enum-edit'>Edit</span> | <span class='enum-remove'>Delete</span> ]</span>")
        SafeHtml label(String label);

        @Template("<span class='enum-add'>+ {0}</span>")
        SafeHtml addChoice(String label);
    }


    private static int nextId = 1;


    private static final Templates TEMPLATES = GWT.create(Templates.class);

    private EnumType enumType;

    private String groupName;

    private final FlowPanel panel;
    private final FlowPanel boxPanel;
    private final List<CheckBox> controls;


    public EnumFieldWidget(EnumType enumType, final ValueUpdater<ReferenceValue> valueUpdater) {
        this.enumType = enumType;
        this.groupName = "group" + (nextId++);
        boxPanel = new FlowPanel();
        controls = new ArrayList<>();

        ValueChangeHandler<Boolean> changeHandler = new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                valueUpdater.update(updatedValue());
            }
        };

        for (final EnumValue instance : enumType.getValues()) {
            CheckBox checkBox = createControl(instance);
            checkBox.addValueChangeHandler(changeHandler);
            boxPanel.add(checkBox);
            controls.add(checkBox);
        }

        panel = new FlowPanel();
        panel.add(boxPanel);
        panel.add(new HTML(TEMPLATES.addChoice("Add option")));

        panel.sinkEvents(Event.MOUSEEVENTS);
        panel.addDomHandler(new MouseUpHandler() {
            @Override
            public void onMouseUp(MouseUpEvent event) {
                if(event.getNativeButton() == NativeEvent.BUTTON_LEFT) {
                    onClick(event);
                }
            }
        }, MouseUpEvent.getType());
    }

    private void onClick(MouseUpEvent event) {
        Element target = event.getNativeEvent().getEventTarget().cast();
        if(target.getClassName().contains("enum-remove")) {
            remove(findId(target));
        } else if(target.getClassName().contains("enum-edit")) {
            ResourceId id = findId(target);
            editLabel(id);
        } else if(target.getClassName().contains("enum-add")) {
            addOption();
        }
    }

    private void addOption() {
        String newLabel = Window.prompt("Enter a new label for this option", "");
        if(!Strings.isNullOrEmpty(newLabel)) {
            EnumValue newValue = new EnumValue(ResourceId.generateId(), newLabel);
            enumType.getValues().add(newValue);
            boxPanel.add(createControl(newValue));
        }
    }

    private void editLabel(ResourceId id) {
        EnumValue enumValue = enumValueForId(id);
        String newLabel = Window.prompt("Enter a new label for this option", enumValue.getLabel());
        if(!Strings.isNullOrEmpty(newLabel)) {
            enumValue.setLabel(newLabel);
            controlForId(id).setHTML(TEMPLATES.label(newLabel));
        }
    }

    private CheckBox controlForId(ResourceId id) {
        for(CheckBox checkBox : controls) {
            if(checkBox.getFormValue().equals(id.asString())) {
                return checkBox;
            }
        }
        throw new IllegalArgumentException(id.asString());
    }

    private EnumValue enumValueForId(ResourceId id) {
        for(EnumValue value : enumType.getValues()) {
            if(value.getId().equals(id)) {
                return value;
            }
        }
        throw new IllegalArgumentException(id.asString());
    }

    private void remove(ResourceId id) {
        controlForId(id).removeFromParent();
        enumType.getValues().remove(enumValueForId(id));
    }

    private ResourceId findId(Element target) {
        Element container = target;
        while(Strings.isNullOrEmpty(container.getAttribute("data-id"))) {
            container = container.getParentElement();
        }
        return ResourceId.create(container.getAttribute("data-id"));

    }

    private CheckBox createControl(EnumValue instance) {
        CheckBox checkBox;
        if(enumType.getCardinality() == Cardinality.SINGLE) {
            checkBox = new RadioButton(groupName, TEMPLATES.label(instance.getLabel()));
        } else {
            checkBox = new org.activityinfo.ui.client.widget.CheckBox(TEMPLATES.label(instance.getLabel()));
        }
        checkBox.setFormValue(instance.getId().asString());
        checkBox.getElement().setAttribute("data-id", instance.getId().asString());
        return checkBox;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        for (CheckBox control : controls) {
            control.setEnabled(!readOnly);
        }
    }

    private ReferenceValue updatedValue() {
        final Set<ResourceId> value = Sets.newHashSet();
        for (CheckBox control : controls) {
            if(control.getValue()) {
                value.add(ResourceId.create(control.getFormValue()));
            }
        }
        return new ReferenceValue(value);
    }

    @Override
    public Promise<Void> setValue(EnumFieldValue value) {
        for (CheckBox entry : controls) {
            ResourceId resourceId = ResourceId.create(entry.getFormValue().toUpperCase()); // upper case, enum name equals is case sensitive
            entry.setValue(value.getValueIds().contains(resourceId));
        }
        return Promise.done();
    }

    @Override
    public void clearValue() {
        setValue(EnumFieldValue.EMPTY);
    }

    @Override
    public void setType(FieldType type) {

    }

    @Override
    public Widget asWidget() {
        return panel;
    }
}
