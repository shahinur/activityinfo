package org.activityinfo.ui.app.client.form.control;

import com.google.common.base.Strings;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.ui.app.client.form.store.FieldState;
import org.activityinfo.ui.app.client.form.store.InstanceStore;
import org.activityinfo.ui.style.BaseStyles;
import org.activityinfo.ui.vdom.shared.html.Children;
import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VThunk;
import org.activityinfo.ui.vdom.shared.tree.VTree;
import org.activityinfo.ui.widget.validation.ValidationMessage;

import java.util.ArrayList;
import java.util.List;

import static org.activityinfo.ui.vdom.shared.html.H.*;

public class HorizontalFieldGroup extends VThunk<HorizontalFieldGroup> {

    private final InstanceStore store;
    private final ResourceId fieldId;
    private VThunk control;

    public HorizontalFieldGroup(InstanceStore store, ResourceId fieldId, VThunk control) {
        this.store = store;
        this.fieldId = fieldId;
        this.control = control;
    }

    @Override
    protected VTree render() {

        String classNames = BaseStyles.FORM_GROUP.getClassNames();

        FieldState state = store.getState(fieldId);
        FormField field = state.getField();

        List<VTree> children = new ArrayList<>();
        children.add(renderLabel(state.getField()));
        children.add(renderControl());

        if(!Strings.isNullOrEmpty(field.getDescription())) {
            children.add(renderHelpBlock(field));
        }

        if(!state.isValid()) {
            classNames += " " + BaseStyles.HAS_ERROR;
            for(ValidationMessage message : state.getValidationMessages()) {
                children.add(render(message));
            }
        }

        return div(PropMap.withClasses(classNames), Children.toArray(children));
    }

    private VNode renderLabel(FormField field) {
        return new VNode(HtmlTag.LABEL, PropMap.withClasses("col-sm-3 control-label"),
            t(field.getLabel()), requiredMarker(field.isRequired()));
    }

    private VTree requiredMarker(boolean required) {
        if(required) {
            return span(BaseStyles.ASTERISK, "*");
        }  else {
            return t("");
        }
    }

    private VNode renderControl() {
        return new VNode(HtmlTag.DIV, PropMap.withClasses("col-sm-6"), control);
    }

    private VNode renderHelpBlock(FormField field) {
        return new VNode(HtmlTag.SPAN, t(field.getDescription()));
    }

    private VTree render(ValidationMessage message) {
        return label(BaseStyles.ERROR, t(message.getMessage()));
    }

}
