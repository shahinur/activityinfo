package org.activityinfo.ui.style.tree;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.activityinfo.ui.flux.store.Status;
import org.activityinfo.ui.flux.store.StoreChangeListener;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.vdom.shared.html.Icon;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MockTree implements TreeModel<String> {

    private List<String> rootNodes = Lists.newArrayList();
    private Map<String, List<String>> children = Maps.newHashMap();

    @Override
    public boolean isLeaf(String node) {
        return !children.containsKey(node);
    }

    @Override
    public Status<List<String>> getRootNodes() {
        return Status.cache(rootNodes);
    }

    @Override
    public Status<List<String>> getChildren(String parent) {
        return Status.cache(children.get(parent));
    }

    @Override
    public String getLabel(String node) {
        return node;
    }

    @Override
    public Icon getIcon(String node, boolean expanded) {
        return FontAwesome.FOLDER;
    }

    @Override
    public String getKey(String node) {
        return node;
    }

    @Override
    public void requestRootNodes() {

    }

    @Override
    public void requestChildren(String node) {

    }

    @Override
    public void addChangeListener(StoreChangeListener listener) {

    }

    @Override
    public void removeChangeListener(StoreChangeListener listener) {

    }

    public void setRootNodes(String... nodes) {
        this.rootNodes = Arrays.asList(nodes);
    }

    public void setChildren(String parent, String... children) {
        this.children.put(parent, Arrays.asList(children));
    }
}
