package org.activityinfo.ui.vdom.shared.diff;

import org.activityinfo.ui.vdom.shared.tree.Tag;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VTree;
import org.hamcrest.Matchers;
import org.junit.Test;

import static org.activityinfo.ui.vdom.shared.html.H.*;
import static org.activityinfo.ui.vdom.shared.html.HtmlTag.UL;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class DiffTest {

    @Test
    public void inserts() {

        VTree a, b;

        a = div(className("red"),         // 0
                p(                        // 1
                    "Hello world."),      // 2
                p(                        // 3
                    "I have a list"),     // 4
                ul(),                     // 5
                p(                        // 6
                    "That was my list")); // 7


        b = div(className("red"),
                p("Hello world."),
                p("I have a list"),
                ul(
                    li("Item 1"),
                    li("Item 2"),
                    li("Item 3"),
                    li("Item 4")
                ),
                p("That was my list"));


        // Find the differences between the two trees
        VDiff diff = Diff.diff(a, b);
        System.out.println(diff);

        // The only node to be patched should be the UL at index #5,
        // the parent where we need to add the new children
        assertThat(diff.getPatchedIndexes(), Matchers.contains(5));

        // Verify that we have three INSERTS
        assertThat(diff.get(5), hasSize(4));

        // With the right nodes...
        VNode updatedList = (VNode) b.childAt(2);
        assertThat(updatedList.tag, is((Tag) UL));
        assertThat(diff.get(5), contains(
                VPatch.insert(updatedList.childAt(0)),
                VPatch.insert(updatedList.childAt(1)),
                VPatch.insert(updatedList.childAt(2)),
                VPatch.insert(updatedList.childAt(3))));
    }
}