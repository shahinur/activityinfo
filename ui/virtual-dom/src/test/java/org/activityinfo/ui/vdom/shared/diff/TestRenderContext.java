package org.activityinfo.ui.vdom.shared.diff;

import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.ui.vdom.client.render.DomBuilder;
import org.activityinfo.ui.vdom.client.render.DomPatcher;
import org.activityinfo.ui.vdom.client.render.RenderContext;
import org.activityinfo.ui.vdom.shared.VDomLogger;
import org.activityinfo.ui.vdom.shared.dom.DomDocument;
import org.activityinfo.ui.vdom.shared.dom.DomElement;
import org.activityinfo.ui.vdom.shared.dom.DomNode;
import org.activityinfo.ui.vdom.shared.dom.TestDocument;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VTree;

public class TestRenderContext implements RenderContext {

    private TestDocument document = new TestDocument();
    private DomBuilder builder;
    private DomPatcher patcher;

    private DomElement domRoot;
    VTree virtualRoot;

    boolean dirty = false;

    public TestRenderContext() {
        builder = new DomBuilder(this);
        patcher = new DomPatcher(builder, this);
    }

    @Override
    public DomDocument getDocument() {
        return document;
    }

    @Override
    public void attachWidget(Widget widget, DomElement container) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void detachWidget(Widget widget) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void fireUpdate(VComponent thunk) {
        dirty = true;
    }

    @Override
    public void registerEventListener(DomNode node, VComponent thunk) {

    }

    public void render(VTree tree) {
        if(virtualRoot == null) {
            VDomLogger.start("initial render");
            virtualRoot = tree;
            domRoot = (DomElement) builder.render(tree);

        } else {
            VDomLogger.start("render:diff");
            VPatchSet diff = Diff.diff(virtualRoot, tree);

            VDomLogger.start("render:patch");
            patcher.patch(getDomRoot(), diff);
            virtualRoot = tree;
            dirty = false;
        }
    }

    public void updateDirty() {
        VDomLogger.start("updateDirty:diff");

        VPatchSet diff = Diff.diff(virtualRoot, virtualRoot);
        VDomLogger.dump(diff);

        VDomLogger.start("updateDirty:patch");
        patcher.patch(getDomRoot(), diff);
        dirty = false;
    }

    public DomElement getDomRoot() {
        return domRoot;
    }
}
