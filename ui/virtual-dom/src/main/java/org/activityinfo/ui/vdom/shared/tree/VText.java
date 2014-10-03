package org.activityinfo.ui.vdom.shared.tree;

public class VText extends VTree {

    public static final VTree NO_BREAK_SPACE = new VText("\u00A0");

    public static final VTree EMPTY_TEXT = new VText("");


    private final String text;

    public VText(String text) {
        if(text == null) {
            this.text = "";
        } else {
            this.text = text;
        }
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

    @Override
    public String toString() {
        return "VText(" + text() + ")";
    }
}
