package org.activityinfo.ui.vdom.shared.diff;

import org.activityinfo.ui.vdom.shared.tree.VTree;

import java.util.*;

public class VDiff {

    public final VTree a;

    /**
     * Map from the dom node index to its patch
     */
    public final Map<Integer, List<VPatch>> map = new HashMap<>();

    public VDiff(VTree a) {
        this.a = a;
    }

    /**
     * Adds a patch to this set for the DOM node with the given {@code index}
     *
     * DOM nodes are indexed recursively. See {@link org.activityinfo.ui.vdom.client.patch.DomIndex}
     *
     * @param index the index of the DOM node
     * @param apply
     */
    public void add(int index, VPatch patch) {
        List<VPatch> list = map.get(index);
        if(list == null) {
            map.put(index, Arrays.asList(patch));
        } else {
            list.add(patch);
        }
    }

    public List<VPatch> get(int index) {
        List<VPatch> list = map.get(index);
        if(list == null) {
            return Collections.emptyList();
        } else {
            return list;
        }
    }

    public int[] indexArray() {
        int[] array = new int[map.size()];
        int i = 0;
        for(Integer index : map.keySet()) {
            array[i++] = index;
        }
        return array;
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }
}
