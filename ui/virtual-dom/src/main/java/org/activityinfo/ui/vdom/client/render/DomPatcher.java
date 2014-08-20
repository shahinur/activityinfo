package org.activityinfo.ui.vdom.client.render;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Text;
import org.activityinfo.ui.vdom.shared.diff.VDiff;
import org.activityinfo.ui.vdom.shared.diff.VPatch;
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

    public Node patch(Node rootNode, VDiff patches) {
        return patchRecursive(rootNode, patches);
    }

    public Node patchRecursive(Node rootNode, VDiff patches) {
        int[] indices = patches.patchedIndexArray();
        if(indices.length > 0) {

            Map<Integer, Node> index = DomIndexBuilder.domIndex(rootNode, patches.original, indices);

            for (int i = 0; i < indices.length; ++i) {
                int nodeIndex = indices[i];
                rootNode = applyPatch(rootNode, index.get(nodeIndex), patches.get(nodeIndex));
            }
        }
        return rootNode;
    }

    private Node applyPatch(Node rootNode, Node domNode, List<VPatch> patchList) {
        if(domNode != null) {
            Node newNode;
            for (int i = 0; i != patchList.size(); ++i) {
                newNode = applyPatch(patchList.get(i), domNode);
                if (domNode == rootNode) {
                    rootNode = newNode;
                }
            }
        }
        return rootNode;
    }


    private Node applyPatch(VPatch vPatch, Node domNode) {
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
                Properties.applyProperties(domNode.<Element>cast(), (PropMap)patch, vNode.properties());
                return domNode;

            case THUNK:
                return replaceRoot(domNode, patch(domNode, (VDiff) patch));
        }
        return domNode;
    }


    private void removeNode(Node domNode, VTree vNode) {

        detachIfWidget(domNode, vNode);

        Node parentNode = domNode.getParentNode();
        if (parentNode != null) {
            parentNode.removeChild(domNode);
        }
    }

    private Node insertNode(Node parentNode, VTree vNode) {
        Node domNode = domBuilder.render(vNode);
        parentNode.appendChild(domNode);
        return domNode;
    }

    private Node patchText(Node domNode, VTree leftVNode, VText vText) {
        Node newNode;

        if (domNode.getNodeType() == Node.TEXT_NODE) {
            Text textNode = domNode.cast();
            textNode.setData(vText.text);
            newNode = textNode;
        } else {

            detachIfWidget(domNode, leftVNode);

            Node parentNode = domNode.getParentNode();
            newNode = domBuilder.render(vText);
            if (parentNode != null) {
                parentNode.replaceChild(newNode, domNode);
            }
        }
        return newNode;
    }

    private Node patchWidget(Node domNode, VTree leftVNode, VWidget widget) {

        detachIfWidget(domNode, leftVNode);

        Node newWidget = domBuilder.render(widget);

        Node parentNode = domNode.getParentNode();
        if (parentNode != null) {
            parentNode.replaceChild(newWidget, domNode);
        }
        return newWidget;
    }

    private Node patchNode(Node domNode, VTree leftVNode, VTree vNode) {

        detachIfWidget(domNode, leftVNode);

        Node parentNode = domNode.getParentNode();
        Node newNode = domBuilder.render(vNode);
        if (parentNode != null) {
            parentNode.replaceChild(newNode, domNode);
        }
        return newNode;
    }

    private void detachIfWidget(Node domNode, VTree w) {
        if (w instanceof VWidget) {
            context.detachWidget(domNode.<Element>cast());
        }
        if(w instanceof Destructible) {
            ((Destructible) w).destroy(domNode);
        }
    }


    private void reorderChildren(Node domNode, int[] bIndex) {
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

    public Node replaceRoot(Node oldRoot, Node newRoot) {
        if ( (oldRoot != null) && (newRoot != null) &&
             (oldRoot != newRoot) && (oldRoot.getParentNode() != null)) {

            oldRoot.getParentNode().replaceChild(newRoot, oldRoot);
        }
        return newRoot;
    }
}
