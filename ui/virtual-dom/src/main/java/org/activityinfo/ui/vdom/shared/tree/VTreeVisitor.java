package org.activityinfo.ui.vdom.shared.tree;

public interface VTreeVisitor {

    void visitNode(VNode node);

    void visitText(VText text);

    void visitThunk(VThunk vThunk);

    void visitWidget(VWidget widget);

}
