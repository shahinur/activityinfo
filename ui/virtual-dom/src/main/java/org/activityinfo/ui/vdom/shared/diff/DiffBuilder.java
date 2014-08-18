package org.activityinfo.ui.vdom.shared.diff;

import org.activityinfo.ui.vdom.shared.Truthyness;
import org.activityinfo.ui.vdom.shared.tree.*;

import java.util.*;

import static org.activityinfo.ui.vdom.shared.tree.PropMap.isObject;

/**
 * Constructs a V
 *
 * Adapted from vtree/diff.js
 */
public class DiffBuilder {

    private VDiff patchSet;

    public static VDiff diff(VTree a, VTree b) {
        DiffBuilder diff = new DiffBuilder();
        diff.patchSet = new VDiff(a);
        diff.walk(a, b, 0);
        return diff.patchSet;
    }

    private DiffBuilder() {}

    public void walk(VTree a, VTree b, int index) {
        if (a == b) {
            if (VThunk.isThunk(a) || VThunk.isThunk(b)) {
                thunks(a, b, index);
            } else {
                hooks(b, index);
            }
            return;
        }

        if (b == null) {
            patchSet.add(index, VPatch.remove(a));
            destroyWidgets(a, index);

        } else if (VThunk.isThunk(a) || VThunk.isThunk(b)) {
            thunks(a, b, index);

        } else if (VNode.isVNode(b)) {
            if (VNode.isVNode(a)) {
                diffVNodes((VNode)a, (VNode)b, index);

            } else {
                patchSet.add(index, VPatch.replace(VPatch.Type.VNODE, a, b));
                destroyWidgets(a, index);
            }

        } else if (VText.isVText(b)) {
            if (!VText.isVText(a)) {
                patchSet.add(index, VPatch.replace(VPatch.Type.VTEXT, a, b));
                destroyWidgets(a, index);

            } else if (!a.text().equals(b.text())) {
                patchSet.add(index, VPatch.replace(VPatch.Type.VTEXT, a, b));
            }

        } else if (VWidget.isWidget(b)) {
            patchSet.add(index, VPatch.replace(VPatch.Type.WIDGET, a, b));

            if (!VWidget.isWidget(a)) {
                destroyWidgets(a, index);
            }
        }
    }

    private void diffVNodes(VNode a, VNode b, int index) {
        if (a.tag == b.tag &&
            Objects.equals(a.key, b.key)) {

            PropMap propsPatch = diffProps(a.properties(), b.properties(), b.hooks());
            if (Truthyness.isTrue(propsPatch)) {
                patchSet.add(index, VPatch.patchProps(a, propsPatch));
            }
        } else {
            patchSet.add(index, VPatch.replace(VPatch.Type.VNODE, a, b));
            destroyWidgets(a, index);
        }

        diffChildren(a, b, index);
    }

    public static PropMap diffProps(PropMap a, PropMap b, PropMap hooks) {
        PropMap diff = null;

        for (String aKey : a.keys()) {
            if (!(b.contains(aKey))) {
                if(diff == null) diff = new PropMap();
                diff.set(aKey, null);
            }

            Object aValue = a.get(aKey);
            Object bValue = b.get(aKey);

            if (Truthyness.isTrue(hooks) && hooks.contains(aKey)) {
                if(diff == null) diff = new PropMap();
                diff.set(aKey, bValue);

            } else {
                if (isObject(aValue) && isObject(bValue)) {

                    if (getPrototype(bValue) != getPrototype(aValue)) {
                        if(diff == null) diff = new PropMap();
                        diff.set(aKey, bValue);

                    } else {
                        PropMap objectDiff = diffProps((PropMap) aValue, (PropMap) bValue, null);
                        if (Truthyness.isTrue(objectDiff)) {
                            if(diff == null) diff = new PropMap();
                            diff.set(aKey, objectDiff);
                        }
                    }
                } else if (!Objects.equals(aValue, bValue)) {
                    if(diff == null) diff = new PropMap();
                    diff.set(aKey, bValue);
                }
            }
        }

        for (String bKey : b.keys()) {
            if (!(a.contains(bKey))) {
                if(diff == null) diff = new PropMap();
                diff.set(bKey, b.get(bKey));
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

    public void diffChildren(VTree a, VTree b, int index) {
        VTree[] aChildren = a.children();
        VTree[] bChildren = reorder(aChildren, b.children());

        int aLen = aChildren.length;
        int bLen = bChildren.length;
        int len = aLen > bLen ? aLen : bLen;

        for (int i = 0; i < len; i++) {
            VTree leftNode = aChildren[i];
            VTree rightNode = bChildren[i];
            index += 1;

            if (isAbsent(leftNode)) {
                if (isPresent(rightNode)) {
                    // Excess nodes in b need to be added
                    patchSet.add(index, VPatch.insert(rightNode));
                }
            } else if (isAbsent(rightNode)) {
                if (isPresent(leftNode)) {
                    // Excess nodes in a need to be removed
                    patchSet.add(index, VPatch.remove(leftNode));
                    destroyWidgets(leftNode, index);
                }
            } else {
                walk(leftNode, rightNode, index);
            }

            index += leftNode.count();
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
            if (vNode instanceof Destructible) {
                patchSet.add(index, VPatch.remove(vNode));
            }
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
        ThunkResult nodes = Thunks.handleThunk(a, b);
        VDiff thunkPatch = diff(nodes.a, nodes.b);
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

            if (vNode.descendantHooks()) {
                VTree[] children = vNode.children();
                int len = children.length;
                for (int i = 0; i < len; i++) {
                    VTree child = children[i];
                    index += 1;

                    hooks(child, index);

                    index += child.count();
                }
            }
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

    public static Object appendPatch(Object apply, VPatch patch) {
        if (Truthyness.isTrue(apply)) {
            if (apply instanceof List) {
                ((List) apply).add(patch);
            } else {
                apply = Arrays.asList(apply, patch);
            }
            return apply;
        } else {

            return patch;
        }
    }
}
