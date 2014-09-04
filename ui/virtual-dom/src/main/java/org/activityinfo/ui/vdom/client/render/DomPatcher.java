package org.activityinfo.ui.vdom.client.render;


import org.activityinfo.ui.vdom.shared.diff.PatchOp;
import org.activityinfo.ui.vdom.shared.diff.VPatchSet;
import org.activityinfo.ui.vdom.shared.dom.DomElement;
import org.activityinfo.ui.vdom.shared.dom.DomNode;
import org.activityinfo.ui.vdom.shared.dom.DomText;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import java.util.List;
import java.util.Map;

public class DomPatcher implements PatchOpExecutor {

    private DomBuilder domBuilder;
    private RenderContext context;

    public DomPatcher(DomBuilder domBuilder, RenderContext context) {
        this.domBuilder = domBuilder;
        this.context = context;
    }

    public DomNode patch(DomNode rootNode, VPatchSet patches) {
        return patchRecursive(rootNode, patches);
    }

    public DomNode patchRecursive(DomNode rootNode, VPatchSet patches) {
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

    private DomNode applyPatch(DomNode rootNode, DomNode domNode, List<PatchOp> patchList) {
        if(domNode != null) {
            DomNode newNode;
            for (int i = 0; i != patchList.size(); ++i) {
                newNode = patchList.get(i).apply(this, domNode);
                if (domNode == rootNode) {
                    rootNode = newNode;
                }
            }
        }
        return rootNode;
    }

    @Override
    public DomNode updateProperties(DomNode domNode, PropMap propPatch, PropMap previous) {
        Properties.applyProperties((DomElement) domNode, propPatch, previous);
        return domNode;
    }


    @Override
    public DomNode removeNode(DomNode domNode) {
        DomNode parentNode = domNode.getParentNode();
        if (parentNode != null) {
            parentNode.removeChild(domNode);
        }
        return null;
    }

    @Override
    public DomNode insertNode(DomNode parentNode, VTree newNode) {
        DomNode domNode = domBuilder.render(newNode);
        parentNode.appendChild(domNode);
        return parentNode;
    }

    @Override
    public DomNode patchText(DomNode domNode, String newText) {
        assert domNode.getNodeType() == DomNode.TEXT_NODE;

        DomText textNode = (DomText)domNode;
        textNode.setData(newText);

        return domNode;
    }

    @Override
    public DomNode replaceNode(DomNode domNode, VTree vNode) {

        DomNode parentNode = domNode.getParentNode();
        DomNode newNode = domBuilder.render(vNode);
        if (parentNode != null) {
            parentNode.replaceChild(newNode, domNode);
        }
        return newNode;
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

    @Override
    public DomNode patchComponent(DomNode rootDomNode, VComponent previous, VComponent replacement, VPatchSet patch) {

        DomNode newNode = patch(rootDomNode, patch);

        if( previous != replacement) {
            assert previous.isMounted();
            previous.fireWillUnmount();
        }

        if( replacement != previous &&
            replacement.isMounted() &&
            replacement.getDomNode() != newNode) {

            replacement.fireWillUnmount();
        }
        rootDomNode = replaceRoot(rootDomNode, newNode);

        if(!replacement.isMounted()) {
            replacement.fireMounted(context, rootDomNode);
        }
        return rootDomNode;
    }

    public DomNode replaceRoot(DomNode oldRoot, DomNode newRoot) {
        if ( (oldRoot != null) && (newRoot != null) &&
             (oldRoot != newRoot) && (oldRoot.getParentNode() != null)) {

            oldRoot.getParentNode().replaceChild(newRoot, oldRoot);
        }
        return newRoot;
    }
}
