package org.activityinfo.ui.vdom.shared.diff;

import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import javax.annotation.Nonnull;

public class VPatch {

    public enum Type {
        NONE,
        VTEXT,
        VNODE,
        WIDGET,
        PROPS,
        ORDER,
        INSERT,
        REMOVE,
        THUNK
    }

    public final Type type;
    public final VTree vNode;
    public final Object patch;

    private VPatch(Type type, VTree vNode, Object patch) {
        this.type = type;
        this.vNode = vNode;
        this.patch = patch;
    }


    public static VPatch remove(VTree a) {
        return new VPatch(Type.REMOVE, a, null);
    }

    public static VPatch thunkPatch(VDiff thunkPatch) {
        return new VPatch(Type.THUNK, null, thunkPatch);
    }

    /**
     * Creates a new VPatch that updates the properties of the node {@code a}
     * @param a
     * @param propsPatch
     * @return
     */
    public static VPatch patchProps(VNode a, PropMap propsPatch) {
        return new VPatch(Type.PROPS, a, propsPatch);
    }

    /**
     * Creates a new VPatch that replaces {@code a} with {@code b}
     * @param type the type of the new node
     * @param a the node to replace
     * @param b the replacement node
     *
     */
    public static VPatch replace(Type type, @Nonnull VTree a, @Nonnull VTree b) {

        assert type == Type.VTEXT || type == Type.VNODE || type == Type.WIDGET;

        return new VPatch(type, a, b);
    }

    public static VPatch insert(VTree b) {

        assert b != null;

        return new VPatch(Type.INSERT, null, b);
    }
}
