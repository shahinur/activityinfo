package org.activityinfo.ui.vdom.client.render;

import org.activityinfo.ui.vdom.shared.diff.VPatchSet;
import org.activityinfo.ui.vdom.shared.dom.DomNode;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VTree;

public interface PatchOpExecutor {

    DomNode updateProperties(DomNode domNode, PropMap propPatch, PropMap previous);

    DomNode removeNode(VTree virtualNode, DomNode domNode);

    DomNode insertNode(DomNode parentNode, VTree newNode);

    DomNode patchText(DomNode domNode, String newText);

    DomNode replaceNode(VTree previousNode, VTree newNode, DomNode domNode);

    DomNode patchComponent(DomNode domNode, VComponent previous, VComponent replacement, VPatchSet patchSet);



}
