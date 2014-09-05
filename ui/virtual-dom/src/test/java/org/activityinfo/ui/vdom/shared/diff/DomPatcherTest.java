package org.activityinfo.ui.vdom.shared.diff;

import com.google.common.base.Strings;
import org.activityinfo.ui.vdom.shared.VDomLogger;
import org.activityinfo.ui.vdom.shared.dom.DomElement;
import org.activityinfo.ui.vdom.shared.dom.DomNode;
import org.activityinfo.ui.vdom.shared.dom.DomText;
import org.activityinfo.ui.vdom.shared.tree.VTree;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.activityinfo.ui.vdom.shared.html.H.*;

public class DomPatcherTest {

    @Before
    public void enableLogging() {
        VDomLogger.STD_OUT = true;
    }

    @Test
    public void test() {
        assertCorrectlyPatched(div("a"), div("b"));


        assertCorrectlyPatched(div("red", p("cow")),
                               div("red", li("a"), li("c")));

    }

    @Test
    public void elementAndText() {
        assertCorrectlyPatched(div("a"), p("b"));
    }

    @Test
    public void childRemoved() {
        assertCorrectlyPatched(
            div("red", li("a"), li("b"), li("c")),
            div("red", li("a"), li("c")));
    }

    @Test
    public void childInserted() {
        assertCorrectlyPatched(
            div("red", li("a"), li("c")),
            div("red", li("a"), li("b"), li("c")));
    }

//    @Test
//    public void componentsReplaced() {
//        dump(new FooComponent(), new BarComponent());
//        assertCorrectlyPatched(new FooComponent(), new BarComponent());
//    }

    public void assertCorrectlyPatched(VTree a, VTree b) {
        TestRenderContext context = new TestRenderContext();
        context.render(a);
        context.render(b);

        DomNode patchedResult = context.getDomRoot();
        DomNode expectedResult = context.getBuilder().render(b);

        checkEquivalent(expectedResult, patchedResult);
    }
    

    public void checkEquivalent(DomNode expectedResult, DomNode actualResult) {


        List<String> expectedTree = buildTree(expectedResult);
        List<String> actualTree = buildTree(actualResult);

        if(!actualTree.equals(expectedTree)) {
            dump(expectedTree, actualTree);
            throw new AssertionError("patch problem");
        }
    }

    private void dump(VTree a, VTree b) {

        TestRenderContext ca = new TestRenderContext();
        ca.render(a);

        TestRenderContext cb = new TestRenderContext();
        cb.render(b);

        List<String> ta = buildTree(ca.getDomRoot());
        List<String> tb = buildTree(cb.getDomRoot());

        dump(ta, tb);

    }

    public List<String> buildTree(DomNode node) {
        List<String> tree = new ArrayList<>();
        buildTree(tree, "", node);
        return tree;
    }

    public void buildTree(List<String> tree, String indent, DomNode node) {
        if(node instanceof DomElement) {
            buildTree(tree, indent, (DomElement) node);
        } else if(node instanceof DomText) {
            tree.add(indent + "\"" + ((DomText) node).getData() + "\"");
        }
    }

    public void buildTree(List<String> tree, String indent, DomElement node) {
        String tag = node.getTagName().toLowerCase();

        if(node.getChildCount() == 0) {
            tree.add(indent + "<" + tag + "/>");
        } else {
            tree.add(indent + "<" + tag + ">");
            for(int i=0;i!=node.getChildCount();++i) {
                buildTree(tree, indent + " ", node.getChildDomNode(i));
            }
            tree.add(indent + "</" + tag + ">");
        }
    }

    private void dump(List<String> expectedTree, List<String> actualTree) {

        System.out.println(Strings.padEnd("Expected", 50, ' ') +
                           Strings.padEnd("Actual", 50, ' '));

        Iterator<String> a = expectedTree.iterator();
        Iterator<String> b = actualTree.iterator();
        while(a.hasNext() || b.hasNext()) {
            String sa = a.hasNext() ? a.next() : "";
            String sb = b.hasNext() ? b.next() : "";
            System.out.println(Strings.padEnd(sa, 50, ' ') +
                               Strings.padEnd(sb, 50, ' '));
        }
    }

}
