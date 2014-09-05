package org.activityinfo.ui.style;

import org.activityinfo.ui.vdom.shared.html.Icon;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.a;
import static org.activityinfo.ui.vdom.shared.html.H.t;

public class LinkButton extends VComponent {

    private Icon icon;
    private String label;
    private ButtonStyle style;

    public LinkButton(String label) {
        this.label = label;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public ButtonStyle getStyle() {
        return style;
    }

    public void setStyle(ButtonStyle style) {
        this.style = style;
    }

    @Override
    protected VTree render() {
        if(icon == null) {
            return a(PropMap.withClasses(styleNames()), t(label));
        } else {
            return a(PropMap.withClasses(styleNames()), icon.render(), t(label));
        }
    }

    private String styleNames() {
        return style.getClassNames();
    }
}
