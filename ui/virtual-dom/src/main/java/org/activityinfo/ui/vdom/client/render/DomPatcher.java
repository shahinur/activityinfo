package org.activityinfo.ui.vdom.client.render;


import com.google.gwt.dom.client.Element;
import org.activityinfo.ui.vdom.shared.diff.VDiff;
import org.activityinfo.ui.vdom.shared.diff.VPatch;
import org.activityinfo.ui.vdom.shared.dom.DomElement;
import org.activityinfo.ui.vdom.shared.dom.DomNode;
import org.activityinfo.ui.vdom.shared.dom.DomText;
import org.activityinfo.ui.vdom.shared.tree.*;

import java.util.List;
import java.util.Map;

public class DomPatcher {

    private DomBuilder domBuilder;
    private RenderContext context;

    public DomPatcher(DomBuilder domBuilder, RenderContext context) {
        this.domBuilder = domBuilder;
        this.context = context;
    }

    public DomNode patch(DomNode rootNode, VDiff patches) {
        return patchRecursive(rootNode, patches);
    }

    public DomNode patchRecursive(DomNode rootNode, VDiff patches) {
        int[] indices = patches.patchedIndexArray();
        if(indices.length > 0) {

            Map<Integer, DomNode> index = DomIndexBuilder.domIndex(rootNode, patches.original, indices);

            for (int i = 0; i < indices.length; ++i) {
                int nodeIndex = indices[i];
                rootNode = applyPatch(rootNode, index.get(nodeIndex), patches.get(nodeIndex));
            }
        }
        return rootNode;
    }

    private DomNode applyPatch(DomNode rootNode, DomNode domNode, List<VPatch> patchList) {
        if(domNode != null) {
            DomNode newNode;
            for (int i = 0; i != patchList.size(); ++i) {
                newNode = applyPatch(patchList.get(i), domNode);
                if (domNode == rootNode) {
                    rootNode = newNode;
                }
            }
        }
        return rootNode;
    }


    private DomNode applyPatch(VPatch vPatch, DomNode domNode) {
        VTree vNode = vPatch.vNode;
        Object patch = vPatch.patch;

        switch (vPatch.type) {
            case REMOVE:
                removeNode(domNode, vNode);
                return null;

            case INSERT:
                return insertNode(domNode, (VTree) patch);

            case VTEXT:
                return patchText(domNode, vNode, (VText) patch);

            case WIDGET:
                return patchWidget(domNode, vNode, (VWidget) patch);

            case VNODE:
                return patchNode(domNode, vNode, (VNode) patch);

            case ORDER:
//                reorderChildren(domNode, [])vPatch);
//                return domNode;
                throw new UnsupportedOperationException();

            case PROPS:
                Properties.applyProperties((DomElement)domNode, (PropMap)patch, vNode.properties());
                return domNode;

            case THUNK:
                return replaceRoot(domNode, patch(domNode, (VDiff) patch));
        }
        return domNode;
    }


    private void removeNode(DomNode domNode, VTree vNode) {

        detachIfWidget(domNode, vNode);

        DomNode parentNode = domNode.getParentNode();
        if (parentNode != null) {
            parentNode.removeChild(domNode);
        }
    }

    private DomNode insertNode(DomNode parentNode, VTree vNode) {
        DomNode domNode = domBuilder.render(vNode);
        parentNode.appendChild(domNode);
        return domNode;
    }

    private DomNode patchText(DomNode domNode, VTree leftVNode, VText vText) {
        DomNode newNode;

        if (domNode.getNodeType() == DomNode.TEXT_NODE) {
            DomText textNode = (DomText)domNode;
            textNode.setData(vText.text);
            newNode = textNode;
        } else {

            detachIfWidget(domNode, leftVNode);

            DomNode parentNode = domNode.getParentNode();
            newNode = domBuilder.render(vText);
            if (parentNode != null) {
                parentNode.replaceChild(newNode, domNode);
            }
        }
        return newNode;
    }

    private DomNode patchWidget(DomNode domNode, VTree leftVNode, VWidget widget) {

        detachIfWidget(domNode, leftVNode);

        DomNode newWidget = domBuilder.render(widget);

        DomNode parentNode = domNode.getParentNode();
        if (parentNode != null) {
            parentNode.replaceChild(newWidget, domNode);
        }
        return newWidget;
    }

    private DomNode patchNode(DomNode domNode, VTree leftVNode, VTree vNode) {

        detachIfWidget(domNode, leftVNode);

        DomNode parentNode = domNode.getParentNode();
        DomNode newNode = domBuilder.render(vNode);
        if (parentNode != null) {
            parentNode.replaceChild(newNode, domNode);
        }
        return newNode;
    }

    private void detachIfWidget(DomNode domNode, VTree w) {
        if (w instanceof VWidget) {
            context.detachWidget((Element)domNode);
        }
        if(w instanceof Destructible) {
            ((Destructible) w).destroy(domNode);
        }
    }


    private void reorderChildren(DomNode domNode, int[] bIndex) {
        throw new UnsupportedOperationException();
//        int[] children = new int[];
//        var childNodes = domNode.childNodes
//        var len = childNodes.length
//        var i
//        var reverseIndex = bIndex.reverse
//        for (i = 0; i < len; i++) {
//            children.push(domNode.childNodes[i])
//        }
//        var insertOffset = 0
//        var move
//        var node
//        var insertNode
//        for (i = 0; i < len; i++) {
//            move = bIndex[i]
//            if (move !== undefined && move !== i) {
//                // the element currently at this index will be moved later so increase the insert offset
//                if (reverseIndex[i] > i) {
//                    insertOffset++
//                }
//                node = children[move]
//                insertNode = childNodes[i + insertOffset]
//                if (node !== insertNode) {
//                    domNode.insertBefore(node, insertNode)
//                }
//                // the moved element came from the front of the array so reduce the insert offset
//                if (move < i) {
//                    insertOffset--
//                }
//            }
//            // element at this index is scheduled to be removed so increase insert offset
//            if (i in bIndex.removes) {
//                insertOffset++
//            }
//        }
    }

    public DomNode replaceRoot(DomNode oldRoot, DomNode newRoot) {
        if ( (oldRoot != null) && (newRoot != null) &&
             (oldRoot != newRoot) && (oldRoot.getParentNode() != null)) {

            oldRoot.getParentNode().replaceChild(newRoot, oldRoot);
        }
        return newRoot;
    }
}
