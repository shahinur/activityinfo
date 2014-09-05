package org.activityinfo.ui.style;

import org.activityinfo.ui.vdom.shared.html.Children;
import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.tree.*;

import java.util.ArrayList;
import java.util.List;

import static org.activityinfo.ui.vdom.shared.html.H.div;

public class FormGroup extends VComponent {

    private VTree labelContent;
    private VTree control;
    private VTree helpBlock;
    private ValidationState validationState;

    public FormGroup(VTree label, VTree control) {
        this.labelContent = label;
        this.control = control;
    }

    public FormGroup(String label, VTree control) {
        this.labelContent = new VText(label);
        this.control = control;
    }

    public VTree getLabelContent() {
        return labelContent;
    }

    public void setLabelContent(VTree labelContent) {
        this.labelContent = labelContent;
    }

    public VTree getControl() {
        return control;
    }

    public void setControl(VTree control) {
        this.control = control;
    }

    public VTree getHelpBlock() {
        return helpBlock;
    }

    public void setHelpBlock(VTree helpBlock) {
        this.helpBlock = helpBlock;
    }

    public void setHelpBlock(String helpBlock) {
        this.helpBlock = new VText(helpBlock);
    }

    public ValidationState getValidationState() {
        return validationState;
    }

    public void setValidationState(ValidationState validationState) {
        this.validationState = validationState;
    }

    public VTree render() {

        List<VTree> children = new ArrayList<>();
        children.add(new VNode(HtmlTag.LABEL, PropMap.withClasses("col-sm-3 control-label"), labelContent));
        children.add(new VNode(HtmlTag.DIV, PropMap.withClasses("col-sm-6"), control));

        if(helpBlock != null) {
            children.add(new VNode(HtmlTag.SPAN, helpBlock));
        }

        String classNames = BaseStyles.FORM_GROUP.getClassNames();
        if(validationState != null) {
            classNames += " " + validationState.className();
        }

        return div(PropMap.withClasses(classNames), Children.toArray(children));
    }
}
