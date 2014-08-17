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
        return null;
    }

    public abstract void accept(VTreeVisitor visitor);
}
