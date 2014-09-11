package org.activityinfo.ui.style.tree;

import org.junit.Test;

public class TreeComponentTest {

    @Test
    public void test() {

        MockTree tree = new MockTree();
        tree.setRootNodes("A", "B", "C");
        tree.setChildren("A", "1", "2", "3");

        TreeComponent component = new TreeComponent<String>(tree);
        component.render();

    }

}