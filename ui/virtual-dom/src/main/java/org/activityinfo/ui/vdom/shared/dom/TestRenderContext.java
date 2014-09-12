package org.activityinfo.ui.vdom.shared.dom;

import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.ui.vdom.client.render.DomBuilder;
import org.activityinfo.ui.vdom.client.render.DomPatcher;
import org.activityinfo.ui.vdom.client.render.RenderContext;
import org.activityinfo.ui.vdom.shared.VDomLogger;
import org.activityinfo.ui.vdom.shared.diff.Diff;
import org.activityinfo.ui.vdom.shared.diff.VPatchSet;
import org.activityinfo.ui.vdom.shared.html.HtmlRenderer;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestRenderContext implements RenderContext {

    private TestDocument document = new TestDocument();
    private DomBuilder builder;
    private DomPatcher patcher;

    private DomNode domRoot;
    VTree virtualRoot;

    private Map<DomNode, VComponent> eventListeners = new HashMap<>();

    boolean dirty = false;

    public TestRenderContext() {
        builder = new DomBuilder(this);
        patcher = new DomPatcher(builder, this);
    }

    public DomBuilder getBuilder() {
        return builder;
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
    public void registerEventListener(VComponent component, DomNode node) {
        eventListeners.put(node, component);
    }

    @Override
    public void componentUnmounted(VComponent component, DomNode domNode) {
        eventListeners.remove(domNode);
    }

    public void clickElementWithText(String text) {

        updateDirty();

        DomNode target = findNodeWithText(text);

        TestDomEvent event = TestDomEvent.onClick(target);

        DomNode node = target;

        while(node != null) {
            VComponent listener = eventListeners.get(node);
            if(listener != null) {
                listener.onBrowserEvent(event);
            }
            node = node.getParentNode();
        }

        updateDirty();
    }

    private DomNode findNodeWithText(String text) {
        List<DomNode> matching = findNodesWithText(text);

        if(matching.isEmpty()) {
            throw new AssertionError("No node with text: " + text);
        } else if(matching.size() > 1) {
            throw new AssertionError("Found multiple matches for " + text );
        }

        return matching.get(0);
    }

    public List<DomNode> findNodesWithText(String text) {

        updateDirty();


        List<DomNode> matching = new ArrayList<>();
        find(matching, domRoot, text);
        return matching;
    }

    private void find(List<DomNode> matches, DomNode node, String text) {
        if(node instanceof DomText) {
            if(((DomText) node).getData().contains(text)) {
                matches.add(node);
            }
        } else {
            DomElement element = (DomElement) node;
            for(int i=0;i!=element.getChildCount();++i) {
                find(matches, element.getChildDomNode(i), text);
            }
        }
    }

    public void render(VTree tree) {
        if(virtualRoot == null) {
            VDomLogger.start("initial render");
            virtualRoot = tree;
            domRoot = (DomElement) builder.render(tree);

        } else {
            VDomLogger.start("render:diff");
            VPatchSet diff = Diff.diff(virtualRoot, tree);
            VDomLogger.dump(diff);

            VDomLogger.start("render:patch");
            domRoot =  patcher.patch(getDomRoot(), diff);
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

    public DomNode getDomRoot() {
        return domRoot;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void dump() throws IOException {
        if( "true".equals(System.getProperty("dumpHtml"))) {
            HtmlRenderer renderer = new HtmlRenderer();
            virtualRoot.accept(renderer);

            Path htmlFile = Files.createTempFile("page", ".html");

            Files.write(htmlFile, renderer.getHtml().getBytes());

            Desktop.getDesktop().open(htmlFile.toFile());
        }
    }

    public void dumpDom() {
        StringBuilder sb = new StringBuilder();
        ((TestNode)domRoot).writeTo(sb, "");
        System.out.println(sb.toString());

    }

    public void assertTextIsPresent(String text) {


        if(findNodesWithText(text).isEmpty()) {
            System.out.println("------------------------------------------");
            System.out.println("Couldn't find text = " + text);
            System.out.println("------------------------------------------");
            dumpDom();
            System.out.println("------------------------------------------");


        }
    }
}
