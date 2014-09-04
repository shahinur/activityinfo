package org.activityinfo.ui.vdom.shared.tree;

public class VText extends VTree {

    private final String text;

    public VText(String text) {
        this.text = text;
    }

    @Override
    public String text() {
        return getText();
    }

    @Override
    public void accept(VTreeVisitor visitor) {
        visitor.visitText(this);
    }

    public String getText() {
        return text;
    }
}
