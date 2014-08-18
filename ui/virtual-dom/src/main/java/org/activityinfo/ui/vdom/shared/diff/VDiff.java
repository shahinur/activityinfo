package org.activityinfo.ui.vdom.shared.diff;

import com.google.common.base.Joiner;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import java.util.*;

public class VDiff {

    public final VTree original;

    /**
     * Map from the dom node index to its patch
     */
    public final Map<Integer, List<VPatch>> map = new HashMap<>();

    VDiff(VTree original) {
        this.original = original;
    }

    /**
     * Adds a patch to this set for the DOM node with the given {@code index}
     *
     * DOM nodes are indexed recursively. See {@link org.activityinfo.ui.vdom.client.patch.DomIndex}
     *
     * @param index the index of the DOM node
     */
    public void add(int index, VPatch patch) {
        List<VPatch> list = map.get(index);
        if(list == null) {
            list = new ArrayList<>();
            map.put(index, list);
        }
        list.add(patch);
    }

    /**
     * Gets the list of patches to be applied to the DOM node at the
     * given index.
     *
     * @param index
     * @return the list of patches, or an empty list if there are no
     * patches for the given index
     */
    public List<VPatch> get(int index) {
        List<VPatch> list = map.get(index);
        if(list == null) {
            return Collections.emptyList();
        } else {
            return list;
        }
    }

    /**
     *
     * @return the indexes of the DOM nodes to be patched.
     */
    public Collection<Integer> getPatchedIndexes() {
        return map.keySet();
    }

    /**
     * @return the indexes of the DOM nodes to be patched
     * as an array of integers.
     */
    public int[] patchedIndexArray() {
        int[] array = new int[map.size()];
        int i = 0;
        for(Integer index : map.keySet()) {
            array[i++] = index;
        }
        return array;
    }

    /**
     *
     * @return true if this VDiff has no patches
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public String toString() {
        return Joiner.on("\n").withKeyValueSeparator(" = ").join(map);
    }
}
