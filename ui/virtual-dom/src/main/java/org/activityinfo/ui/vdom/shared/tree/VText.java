package org.activityinfo.ui.vdom.shared.tree;

public class VText extends VTree {

    public final String text;
    public final String version = null;

    public VText(String text) {
        this.text = text;
    }

    public static boolean isVText(VTree b) {
        return b instanceof VText;
    }

    @Override
    public String text() {
        return text;
    }

    @Override
    public void accept(VTreeVisitor visitor) {
        visitor.visitText(this);
    }
}
