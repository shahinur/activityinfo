package org.activityinfo.ui.vdom.shared.tree;


public class Thunks {

    public static ThunkResult handleThunk(VTree a) {
        return handleThunk(a, null);
    }

    public static ThunkResult handleThunk(VTree a, VTree b) {
        VTree renderedA = a;
        VTree renderedB = b;
        if (b instanceof VThunk) {
            renderedB = renderThunk((VThunk) b, a);
        }
        if (a instanceof VThunk) {
            renderedA = renderThunk((VThunk)a, null);
        }
        return new ThunkResult(renderedA, renderedB);
    }


    public static VTree renderThunk(VThunk thunk, VTree previous) {
        VTree renderedThunk = thunk.vNode;
        if (renderedThunk == null) {
            renderedThunk = thunk.vNode = thunk.render(previous);
        }
        if (!(renderedThunk instanceof VNode ||
              renderedThunk instanceof VText ||
              renderedThunk instanceof VWidget)) {
            throw new Error("thunk did not return a valid node");
        }
        return renderedThunk;
    }
}
