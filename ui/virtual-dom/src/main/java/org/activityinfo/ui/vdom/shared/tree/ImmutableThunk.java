package org.activityinfo.ui.vdom.shared.tree;

public abstract class ImmutableThunk extends VThunk {


    private VTree tree;


    @Override
    public final VTree render(VTree previous) {
        if(previous == null) {
            return render();
        } else {
            return previous;
        }
    }

    public abstract VTree render();
}
