package org.activityinfo.ui.vdom.shared.tree;

public interface VTreeVisitor {

    void visitNode(VNode node);

    void visitText(VText text);

    void visitComponent(VComponent vComponent);

    void visitWidget(VWidget widget);
}
