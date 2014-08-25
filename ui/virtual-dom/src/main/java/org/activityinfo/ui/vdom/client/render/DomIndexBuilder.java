package org.activityinfo.ui.vdom.client.render;

import org.activityinfo.ui.vdom.shared.Truthyness;
import org.activityinfo.ui.vdom.shared.dom.DomNode;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DomIndexBuilder {

    private Map<Integer, DomNode> map;

    public static Map<Integer, DomNode> domIndex(DomNode rootNode, VTree a, int[] indices) {
        return new DomIndexBuilder().buildIndex(rootNode, a, indices);
    }

    public Map<Integer, DomNode> buildIndex(DomNode rootNode, VTree tree, int[] indices) {
        if (indices.length == 0) {
            return Collections.emptyMap();

        } else {
            map = new HashMap<>();

            Arrays.sort(indices);

            recurse(rootNode, tree, indices, 0);

            return map;
        }
    }

    public void recurse(DomNode rootNode, VTree tree, int[] indices, int rootIndex) {
        if (Truthyness.isTrue(rootNode)) {
            if (indexInRange(indices, rootIndex, rootIndex)) {
                map.put(rootIndex, rootNode);
            }
            VTree[] vChildren = tree.children();
            //NodeList<Node> childNodes = rootNode.getChildNodes();
            for (int i = 0; i < vChildren.length; i++) {
                rootIndex += 1;
                int nextIndex = rootIndex + childCount(vChildren, i);
                // skip recursion down the tree if there are no nodes down here
                if (indexInRange(indices, rootIndex, nextIndex)) {
                    recurse(rootNode.getChildDomNode(i), vChildren[i], indices, rootIndex);
                }
                rootIndex = nextIndex;
            }
        }
    }

    private static int childCount(VTree[] vChildren, int i) {
        if(i < vChildren.length) {
            VTree child = vChildren[i];
            return child.count();
        } else {
            return 0;
        }
    }

    // Binary search for an index in the interval [left, right]
    public static boolean indexInRange(int[] indices, int left, int right) {
        if (indices.length == 0) {
            return false;
        }
        int minIndex = 0;
        int maxIndex = indices.length - 1;
        int currentIndex;
        int currentItem;
        while (minIndex <= maxIndex) {
            currentIndex = ((maxIndex + minIndex) / 2);
            currentItem = indices[currentIndex];
            if (minIndex == maxIndex) {
                return currentItem >= left && currentItem <= right;
            } else if (currentItem < left) {
                minIndex = currentIndex + 1;
            } else if (currentItem > right) {
                maxIndex = currentIndex - 1;
            } else {
                return true;
            }
        }
        return false;
    }
    public static int ascending(int a, int b) {
        return a > b ? 1 : -1;
    }

}
