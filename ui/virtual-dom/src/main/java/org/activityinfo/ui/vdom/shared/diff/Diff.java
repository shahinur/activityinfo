package org.activityinfo.ui.vdom.shared.diff;

import org.activityinfo.ui.vdom.shared.Truthyness;
import org.activityinfo.ui.vdom.shared.tree.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

    private VPatchSet patchSet;

    public static VPatchSet diff(VTree a, VTree b) {
        Diff diff = new Diff();
        diff.patchSet = new VPatchSet(a);
        diff.walk(a, b, 0);
        return diff.patchSet;
    }


    private Diff() {}

    public void walk(VTree a, VTree b, int index) {

        assert a != null;

        if (a == b) {
            // We assume that VNode and VText are immutable,
            // so if a == b, we only need to address components
            // which have marked themselves as dirty with refresh()

            if (a instanceof VComponent) {
                diffComponent((VComponent) a, index);

            } else if (a.hasComponents()) {
                diffChildren(a, b, index);

            }
        } else if (b == null) {
            patchSet.add(index, RemoveOp.INSTANCE);
            unmountComponents(a);

        } else if (b instanceof VComponent) {
            if (a instanceof VComponent) {
                diffComponents((VComponent)a, (VComponent)b, index);
            } else {
                patchSet.add(index, new ReplaceOp(b));
            }

        } else if (b instanceof VNode) {
            if (a instanceof VNode) {
                diffVNodes((VNode)a, (VNode)b, index);

            } else {
                patchSet.add(index, new ReplaceOp(b));
                unmountComponents(a);
            }

        } else if (b instanceof VText) {
            if (a instanceof VText) {
                if(!a.text().equals(b.text())) {
                    patchSet.add(index, new PatchTextOp(b.text()));
                }
            } else {
                patchSet.add(index, new ReplaceOp(b));
                unmountComponents(a);
            }
        }
    }

    private void diffVNodes(VNode a, VNode b, int index) {
        if (a.tag == b.tag &&
            Objects.equals(a.key, b.key)) {

            PropMap propsPatch = diffProps(a.properties, b.properties, b.hooks());
            if (propsPatch != null) {
                patchSet.add(index, new PatchPropsOp(propsPatch, a.properties));
            }

            diffChildren(a, b, index);

        } else {
            patchSet.add(index, new ReplaceOp(b));
            unmountComponents(a);
        }
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

            if (isAbsent(leftNode) && isPresent(rightNode)) {

                // Excess nodes in b need to be added
                patchSet.add(parentIndex, new InsertOp(rightNode));

            } else if (isAbsent(rightNode) && isPresent(leftNode)) {

                // Excess nodes in a need to be removed
                patchSet.add(childIndex, RemoveOp.INSTANCE);
                unmountComponents(leftNode);

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


    public void unmountComponents(VTree vNode) {
        if (vNode instanceof VComponent) {
            VComponent component = (VComponent) vNode;
            component.fireWillUnmount();
            unmountComponents(component.vNode);

        } else if (vNode.hasComponents()) {
            VTree[] children = vNode.children();
            int len = children.length;
            for (int i = 0; i < len; i++) {
                VTree child = children[i];

                unmountComponents(child);
            }
        }
    }

    /**
     * Updates a single component in place
     *
     */
    private void diffComponent(VComponent a, int index) {
        VTree previous = a.vNode;
        if(previous == null) {
            throw new IllegalStateException();
        }

        if(a.isDirty()) {
            VTree updated = a.forceRender();
            addComponentDiff(a, previous, updated, index);

        } else {

            // if there have been no changes to the component, we
            // still need to look for dirty components within the
            // component's tree
            if(previous.hasComponents()) {
                addComponentDiff(a, previous, previous, index);
            }
        }
    }


    private void addComponentDiff(VComponent component, VTree previous, VTree updated, int index) {
        VPatchSet patch = diff(previous, updated);
        if (!patch.isEmpty()) {
            patchSet.add(index, new PatchComponentOp(component, patch));
        }
    }

    public void diffComponents(VComponent a, VComponent b, int index) {

        assert a != null;
        assert b != null;
        assert a.isRendered();

        VTree renderedA = a.vNode;
        VTree renderedB = b.ensureRendered();

        assert renderedA != null;
        assert renderedB != null;

        patchSet.add(index, new PatchComponentOp(a, b, diff(renderedA, renderedB)));
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
