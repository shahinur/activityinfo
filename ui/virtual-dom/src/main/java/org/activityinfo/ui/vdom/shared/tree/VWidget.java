package org.activityinfo.ui.vdom.shared.tree;

import com.google.gwt.dom.client.Element;

public abstract class VWidget extends VTree {

    public static boolean isWidget(VTree a) {
        return a instanceof VWidget;
    }

    public abstract Element init();

    @Override
    public void accept(VTreeVisitor visitor) {
        visitor.visitWidget(this);
    }
}
