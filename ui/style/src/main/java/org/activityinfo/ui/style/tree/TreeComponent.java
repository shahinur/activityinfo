package org.activityinfo.ui.style.tree;

import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.ui.flux.store.Status;
import org.activityinfo.ui.flux.store.Store;
import org.activityinfo.ui.flux.store.StoreChangeListener;
import org.activityinfo.ui.style.Spinners;
import org.activityinfo.ui.vdom.shared.Stylesheet;
import org.activityinfo.ui.vdom.shared.html.CssClass;
import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.activityinfo.ui.vdom.shared.html.H.*;

/**
 * A tree component
 */
@Stylesheet("Tree.less")
public class TreeComponent<T> extends VComponent implements StoreChangeListener {

    private final TreeModel<T> model;
    private final SelectionModel selectionModel;

    private final Set<String> expanded = new HashSet<>();

    private Render<T> nodeItemRenderer = new Render<T>() {
        @Override
        public VTree render(T node) {
            return nodeRenderer.renderNode(node, TreeComponent.this);
        }
    };

    private TreeNodeRenderer<T> nodeRenderer = new DefaultTreeNodeRenderer<>();

    public TreeComponent(TreeModel<T> model, SelectionModel selectionModel) {
        this.model = model;
        this.selectionModel = selectionModel;
    }

    @Override
    protected void componentDidMount() {
        model.addChangeListener(this);
        model.requestRootNodes();
        selectionModel.addChangeListener(this);
    }

    @Override
    public void onStoreChanged(Store store) {
        refresh();
    }


    @Override
    public void onSelectionChanged(SelectionModel selectionModel) {
        refresh();
    }

    @Override
    protected void componentWillUnmount() {
        model.removeChangeListener(this);
        selectionModel.removeChangeListener(this);
    }

    public void onLabelClicked(T node) {

        VDomLogger.event(this, "onLabelClicked");

        if(model.isLeaf(node)) {
            selectionModel.select(model.getKey(node));
        } else {
            toggleNode(node);
        }
    }

    private void toggleNode(T node) {
        if(isExpanded(node)) {
            collapse(node);
        } else if(!model.isLeaf(node)) {
            expand(node);
        }
    }

    private void expand(T node) {
        expanded.add(model.getKey(node));
        model.requestChildren(node);

        refresh();
    }

    private void collapse(T node) {
        expanded.remove(model.getKey(node));
        refresh();
    }

    public boolean isExpanded(T node) {
        return expanded.contains(model.getKey(node));
    }

    @Override
    protected VTree render() {
        Status<List<T>> rootNodes = model.getRootNodes();
        if(rootNodes.isAvailable()) {
            return container(ul(CssClass.valueOf("tree"), map(rootNodes.get(), nodeItemRenderer)));
        } else {
            return Spinners.spinner().render();
        }
    }

    private VTree container(VNode tree) {
        PropMap propMap = PropMap.withClasses("tree-container");
        return div(propMap, tree);
    }

    public VTree renderNode(T node) {
        boolean expanded = isExpanded(node);
        boolean selected = selectionModel.isSelected(model.getKey(node));

        TreeNodeIcon<T> icon = new TreeNodeIcon<>(this, node, model.getIcon(node, expanded));
        TreeNode<T> label = new TreeNode<>(this, node, model.getLabel(node));

        PropMap props = new PropMap();
        if(selected) {
            props.setClass(CssClass.valueOf("selected"));
        }

        return li(props,
            background(),
            new VNode(HtmlTag.SPAN, PropMap.withClasses("node-container"),
                icon, label),
            renderChildren(node, expanded));
    }

    /**
     *
     * @return an absolutely positioned div that highlights the node and fills the
     *
     */
    public VNode background() {
        return div(PropMap.withClasses("node-overlay"), t("\u00A0"));
    }

    public VTree renderChildren(T node, boolean expanded) {
        if(!expanded) {
            return new VNode(HtmlTag.SPAN);
        } else {
            Status<List<T>> children = model.getChildren(node);
            if(!children.isAvailable()) {
                return nodeList(li(Spinners.spinner().render(), t(I18N.CONSTANTS.loading())));

            } else {
                return nodeList(map(children.get(), nodeItemRenderer));
            }
        }
    }

    private VNode nodeList(VTree... listItems) {
        return new VNode(HtmlTag.UL, PropMap.withClasses("tree"), listItems);
    }

    public TreeModel<T> getModel() {
        return model;
    }

    public TreeNodeRenderer<T> getNodeRenderer() {
        return nodeRenderer;
    }

    public void setNodeRenderer(TreeNodeRenderer<T> nodeRenderer) {
        this.nodeRenderer = nodeRenderer;
    }

    public boolean isSelected(T node) {
        return selectionModel.isSelected(model.getKey(node));
    }
}
