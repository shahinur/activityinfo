package org.activityinfo.ui.vdom.shared.diff;

import org.activityinfo.ui.vdom.client.render.PatchOpExecutor;
import org.activityinfo.ui.vdom.shared.dom.DomNode;

public interface PatchOp {

    DomNode apply(PatchOpExecutor executor, DomNode domNode);

}
