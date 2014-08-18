package org.activityinfo.ui.vdom.shared.tree;

public abstract class VTree {

    public boolean hasWidgets() {
        return false;
    }

    /**
     *
     * @return the total number of {@code VNode}s among this tree's children and descendants.
     *
     * <p>Supports DOM node indexing</p>
     */
    public int count() {
        return 0;
    }


    public String key() { throw new UnsupportedOperationException(); }

    public PropMap properties() { throw new UnsupportedOperationException(); }

    public PropMap hooks() { throw new UnsupportedOperationException(); }

    public String text() { throw new UnsupportedOperationException(); }

    public boolean descendantHooks() { throw new UnsupportedOperationException(); }

    public VTree[] children() {
        return VNode.NO_CHILDREN;
    }

    public VTree childAt(int index) {
        return children()[index];
    }

    public abstract void accept(VTreeVisitor visitor);

    /**
     * Forces a Thunk to a concrete value if this VTree is a Thunk, or
     * this VTree itself is a concrete value.
     * @param previous the previously render thunk if available.
     */
    public VTree force(VTree previous) {
        return this;
    }

    /**
     * Forces a Thunk to a concrete value if this VTree is a Thunk, or
     * this VTree itself is a concrete value.
     */
    public VTree force() {
        return this;
    }

}
