package org.activityinfo.ui.vdom.shared.diff;

import org.activityinfo.ui.vdom.shared.Truthyness;
import org.activityinfo.ui.vdom.shared.tree.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.activityinfo.ui.vdom.shared.diff.VPatch.Type.*;
import static org.activityinfo.ui.vdom.shared.tree.PropMap.isObject;

/**
 * Calculates the difference between trees {@code a} and {@code b} and returns a set of
 * patches required to update {@code a} to {@code b}
 *
 * <p>The implementation below was ported quite literally from the javascript vtree library,
 * was in turn based on the approach taken by Facebook's React library.
 *
 *
 * @author Matt-Esch
 * @author Alex Bertram
 *
 * @see <a href="https://github.com/Matt-Esch/virtual-dom">The virtual-dom project</a>
 * @see <a href="https://github.com/Matt-Esch/vtree/blob/5815772fe5b4af971a34bb18610fd0c55fe8d107/diff.js">Original source</a>
 * @see <a href="http://facebook.github.io/react/docs/reconciliation.html">Facebook's description of the algorithm</a>
 */
public class Diff {

    private VDiff patchSet;

    public static VDiff diff(VTree a, VTree b) {
        Diff diff = new Diff();
        diff.patchSet = new VDiff(a);
        diff.walk(a, b, 0);
        return diff.patchSet;
    }

    private Diff() {}

    public void walk(VTree a, VTree b, int index) {
        if (a == b) {
            if (a instanceof VThunk || b instanceof VThunk) {
                thunks(a, b, index);
            } else {
                hooks(b, index);
            }
            return;
        }

        if (b == null) {
            patchSet.add(index, VPatch.remove(a));
            destroyWidgets(a, index);

        } else if (a instanceof VThunk || b instanceof VThunk) {
            thunks(a, b, index);

        } else if (b instanceof VNode) {
            if (a instanceof VNode) {
                diffVNodes((VNode)a, (VNode)b, index);

            } else {
                patchSet.add(index, VPatch.replace(VNODE, a, b));
                destroyWidgets(a, index);
            }

        } else if (b instanceof VText) {
            if (!(a instanceof VText)) {

                patchSet.add(index, VPatch.replace(VTEXT, a, b));
                destroyWidgets(a, index);

            } else if (!a.text().equals(b.text())) {
                patchSet.add(index, VPatch.replace(VTEXT, a, b));
            }

        } else if (b instanceof VWidget) {

            // right now we consider any two widgets to be equal--
            // that's probably not what we want...

            if (!Objects.equals(a, b)) {

                patchSet.add(index, VPatch.replace(WIDGET, a, b));
                destroyWidgets(a, index);
            }
        }
    }

    private void diffVNodes(VNode a, VNode b, int index) {
        if (a.tag == b.tag &&
            Objects.equals(a.key, b.key)) {

            PropMap propsPatch = diffProps(a.properties, b.properties, b.hooks());
            if (propsPatch != null) {
                patchSet.add(index, VPatch.patchProps(a, propsPatch));
            }
        } else {
            patchSet.add(index, VPatch.replace(VNODE, a, b));
            destroyWidgets(a, index);
        }

        diffChildren(a, b, index);
    }

    public static PropMap diffProps(PropMap a, PropMap b, PropMap hooks) {
        PropMap diff = null;

        for (String aKey : a.keys()) {
            if (!(b.contains(aKey))) {
                if(diff == null) diff = new PropMap();
                diff.trustedSet(aKey, null);
            }

            Object aValue = a.get(aKey);
            Object bValue = b.get(aKey);

            if (Truthyness.isTrue(hooks) && hooks.contains(aKey)) {
                if(diff == null) diff = new PropMap();
                diff.trustedSet(aKey, bValue);

            } else {
                if (isObject(aValue) && isObject(bValue)) {

                    if (getPrototype(bValue) != getPrototype(aValue)) {
                        if(diff == null) diff = new PropMap();
                        diff.trustedSet(aKey, bValue);

                    } else {
                        PropMap objectDiff = diffProps((PropMap) aValue, (PropMap) bValue, null);
                        if (Truthyness.isTrue(objectDiff)) {
                            if(diff == null) diff = new PropMap();
                            diff.trustedSet(aKey, objectDiff);
                        }
                    }
                } else if (!Objects.equals(aValue, bValue)) {
                    if(diff == null) diff = new PropMap();
                    diff.trustedSet(aKey, bValue);
                }
            }
        }

        for (String bKey : b.keys()) {
            if (!(a.contains(bKey))) {
                if(diff == null) diff = new PropMap();
                diff.trustedSet(bKey, b.get(bKey));
            }
        }

        return diff;
    }

    public static Object getPrototype(Object value) {
        //        if (Object.getPrototypeOf) {
        //            return Object.getPrototypeOf(value);
        //        } else if (value.__proto__) {
        //            return value.__proto__;
        //        } else if (value.constructor) {
        //            return value.constructor.prototype;
        //        }
        return value;
    }

    public void diffChildren(VTree a, VTree b, int parentIndex) {
        VTree[] aChildren = a.children();
        VTree[] bChildren = reorder(aChildren, b.children());

        int aLen = aChildren.length;
        int bLen = bChildren.length;
        int len = aLen > bLen ? aLen : bLen;
        int childIndex = parentIndex;

        for (int i = 0; i < len; i++) {
            VTree leftNode = i < aLen ? aChildren[i] : null;
            VTree rightNode = i < bLen ? bChildren[i] : null;
            childIndex += 1;

            if (isAbsent(leftNode)) {
                if (isPresent(rightNode)) {
                    // Excess nodes in b need to be added
                    patchSet.add(parentIndex, VPatch.insert(rightNode));
                }
            } else if (isAbsent(rightNode)) {
                if (isPresent(leftNode)) {
                    // Excess nodes in a need to be removed
                    patchSet.add(childIndex, VPatch.remove(leftNode));
                    destroyWidgets(leftNode, childIndex);
                }
            } else {
                walk(leftNode, rightNode, childIndex);
            }

            if(leftNode instanceof VNode) {
                childIndex += leftNode.count();
            }
        }

        // TODO: reordering
        //        if (bChildren.moves) {
        //            // Reorder nodes last
        //            apply = appendPatch(apply, new VPatch(VPatch.Type.ORDER, a, bChildren.moves));
        //        }
    }

    private boolean isPresent(VTree node) {
        return node != null;
    }

    private boolean isAbsent(VTree node) {
        return node == null;
    }

    // Patch records for all destroyed widgets must be added because we need
    // a DOM node reference for the destroy public static void
    public void destroyWidgets(VTree vNode, int index) {
        if (VWidget.isWidget(vNode)) {
            patchSet.add(index, VPatch.remove(vNode));
        } else if (vNode.hasWidgets()) {
            VTree[] children = vNode.children();
            int len = children.length;
            for (int i = 0; i < len; i++) {
                VTree child = children[i];
                index += 1;

                destroyWidgets(child, index);

                index += child.count();
            }
        }
    }

    // Create a sub-patch for thunks
    public  void thunks(VTree a, VTree b, int index) {

        VTree renderedB = b.force(a);
        VTree renderedA = a.force();

        VDiff thunkPatch = diff(renderedA, renderedB);
        if (!thunkPatch.isEmpty()) {
            patchSet.add(index, VPatch.thunkPatch(thunkPatch));
        }
    }

    // Execute hooks when two nodes are identical
    public static void hooks(VTree vNode, int index) {
        if (VNode.isVNode(vNode)) {
            if (Truthyness.isTrue(vNode.hooks())) {
                throw new UnsupportedOperationException("todo");
                //patch.add(index, new VPatch(VPatch.Type.PROPS, vNode.hooks(), vNode.hooks()));
            }

//            if (vNode.descendantHooks()) {
//                VTree[] children = vNode.children();
//                int len = children.length;
//                for (int i = 0; i < len; i++) {
//                    VTree child = children[i];
//                    index += 1;
//
//                    hooks(child, index);
//
//                    index += child.count();
//                }
//            }
        }
    }

    // List diff, naive left to right reordering
    public static VTree[] reorder(VTree[] aChildren, VTree[] bChildren) {

        return bChildren;
        // TODO:
        //
        //        Map<String, Integer> bKeys = keyIndex(bChildren);
        //
        //        if (!isTrue(bKeys)) {
        //            return bChildren;
        //        }
        //
        //        Map<String, Integer> aKeys = keyIndex(aChildren);
        //
        //        if (!isTrue(aKeys)) {
        //            return bChildren;
        //        }
        //
        //        Map<Integer, Integer> bMatch = new HashMap<>();
        //        Map<Integer, Integer> aMatch = new HashMap<>();
        //
        //        for (String key : bKeys.keySet()) {
        //            bMatch.put(bKeys.get(key), aKeys.get(key));
        //        }
        //
        //        for (String key : aKeys.keySet()) {
        //            aMatch.put(aKeys.get(key), bKeys.get(key));
        //        }
        //
        //        int aLen = aChildren.length;
        //        int bLen = bChildren.length;
        //        int len = aLen > bLen ? aLen : bLen;
        //        VTree shuffle[] = new VTree[0];
        //        int freeIndex = 0;
        //        int i = 0;
        //        int moveIndex = 0;
        //        BigDecimal moves = {};
        //        BigDecimal removes = moves.removes = {};
        //        BigDecimal reverse = moves.reverse = {};
        //        boolean hasMoves = false;
        //
        //        while (freeIndex < len) {
        //            Integer move = aMatch.get(i);
        //            if (move != null) {
        //                shuffle[i] = bChildren[move];
        //                if (move != moveIndex) {
        //                    moves[move] = moveIndex;
        //                    reverse[moveIndex] = move;
        //                    hasMoves = true;
        //                }
        //                moveIndex++;
        //            } else if (aMatch.containsKey(i)) {
        //                shuffle[i] = null;
        //                removes[i] = moveIndex++;
        //                hasMoves = true;
        //            } else {
        //                while (bMatch.containsKey(freeIndex)) {
        //                    freeIndex++;
        //                }
        //
        //                if (freeIndex < len) {
        //                    VTree freeChild = bChildren[freeIndex];
        //                    if (isTrue(freeChild)) {
        //                        shuffle[i] = freeChild;
        //                        if (freeIndex != moveIndex) {
        //                            hasMoves = true
        //                            moves[freeIndex] = moveIndex;
        //                            reverse[moveIndex] = freeIndex;
        //                        }
        //                        moveIndex++;
        //                    }
        //                    freeIndex++;
        //                }
        //            }
        //            i++;
        //        }
        //
        //        if (hasMoves) {
        //            shuffle.moves = moves;
        //        }
        //
        //        return shuffle;
    }


    public static Map<String, Integer> keyIndex(VTree[] children) {
        Map<String, Integer> keys = null;

        for (int i = 0; i < children.length; i++) {
            VTree child = children[i];

            if (child.key() != null) {
                if(keys == null) keys = new HashMap<>();
                keys.put(child.key(), i);
            }
        }

        return keys;
    }

}
