package org.activityinfo.ui.component.form.field.hierarchy;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import org.activityinfo.model.hierarchy.Hierarchy;
import org.activityinfo.model.hierarchy.Level;
import org.activityinfo.model.hierarchy.Node;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.system.ApplicationProperties;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.model.table.RowSource;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.model.type.ReferenceValue;
import org.activityinfo.promise.Promise;
import org.activityinfo.service.store.ResourceLocator;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Models the selection of hierarchy
 */
class Presenter {
    private Map<ResourceId, LevelView> widgetMap = new HashMap<>();
    private Map<ResourceId, Node> selection = new HashMap<>();
    private List<HandlerRegistration> registrations = new ArrayList<>();
    private ResourceLocator locator;
    private Hierarchy tree;
    private ValueUpdater valueUpdater;

    Presenter(ResourceLocator locator, final Hierarchy tree, Map<ResourceId, ? extends LevelView> widgets,
              ValueUpdater valueUpdater) {
        this.locator = locator;
        this.tree = tree;
        this.valueUpdater = valueUpdater;
        this.widgetMap.putAll(widgets);
        for(final Map.Entry<ResourceId, LevelView> entry : widgetMap.entrySet()) {
            entry.getValue().addSelectionHandler(new SelectionHandler<Node>() {
                @Override
                public void onSelection(SelectionEvent<Node> event) {
                    onUserSelection(tree.getLevel(entry.getKey()), event.getSelectedItem());
                }
            });
        }
    }

    public Promise<Void> setInitialSelection(Set<ResourceId> resourceIds) {
        final InitialSelection initialSelection = new InitialSelection(tree);
        return initialSelection.fetch(locator, resourceIds).then(new Function<Void, Void>() {

            @Nullable
            @Override
            public Void apply(@Nullable Void input) {
                selection.putAll(initialSelection.getSelection());
                for(Level level : tree.getLevels()) {
                    LevelView view = widgetMap.get(level.getClassId());
                    if(level.isRoot() || hasSelection(level.getParent())) {
                        view.setEnabled(true);
                        view.setChoices(choices(level));
                    } else {
                        view.setEnabled(false);
                    }
                    if(hasSelection(level)) {
                        view.setSelection(getSelection(level));
                    }
                }
                return null;
            }
        });
    }


    private void onUserSelection(Level level, Node selectedItem) {
        if(selectedItem == null) {
            this.selection.remove(level.getClassId());
        } else {
            this.selection.put(level.getClassId(), selectedItem);
        }
        clearChildren(level);
        valueUpdater.update(getValue());
    }

    private ReferenceValue getValue() {
        // We want to store the values in a normalized fashion -
        // store only the leaf nodes, their parents are redundant
        Set<ResourceId> instanceIds = Sets.newHashSet();
        Set<ResourceId> parentIds = Sets.newHashSet();
        for(Node node : selection.values()) {
            instanceIds.add(node.getId());
            if(!node.isRoot()) {
                parentIds.add(node.getId());
            }
        }
        return new ReferenceValue(instanceIds);
    }

    private void clearChildren(Level parent) {
        Node parentSelection = selection.get(parent.getClassId());
        for(Level child : parent.getChildren()) {
            selection.remove(child.getClassId());
            clearViewSelection(parentSelection, child);
            clearChildren(child);
        }
    }

    private void clearViewSelection(Node parentSelection, Level child) {
        LevelView view = widgetMap.get(child.getClassId());
        view.clearSelection();
        if(parentSelection != null) {
            view.setChoices(choices(child));
            view.setEnabled(true);
        } else {
            view.setEnabled(false);
        }
    }

    public boolean hasSelection(Level level) {
        return selection.containsKey(level.getClassId());
    }

    public String getSelectionLabel(ResourceId classId) {
        assert selection.containsKey(classId) : "No selection";
        return selection.get(classId).getLabel();
    }

    public Node getSelection(Level level) {
        assert selection.containsKey(level.getClassId());
        return selection.get(level.getClassId());
    }

    private Supplier<Promise<List<Node>>> choices(final Level level) {

        final TableModel tableModel = new TableModel();
        tableModel.getRowSources().add(new RowSource(level.getClassId()));
        tableModel.addResourceId("id");
        tableModel.addColumn("label").select().fieldPath(ApplicationProperties.LABEL_PROPERTY);

        final ResourceId selectedParentId;
        if(level.isRoot()) {
            selectedParentId = null;
        } else {
            selectedParentId = getSelection(level.getParent()).getId();
            tableModel.addColumn("parentId").select().fieldPath(level.getParentFieldId());
        }

        return new Supplier<Promise<List<Node>>>() {
            @Override
            public Promise<List<Node>> get() {
                return locator.queryTable(tableModel).then(new Function<TableData, List<Node>>() {
                    @Override
                    public List<Node> apply(TableData table) {
                        List<Node> nodes = Lists.newArrayList();
                        ColumnView id = table.getColumnView("id");
                        ColumnView label = table.getColumnView("label");
                        ColumnView parent = table.getColumnView("parentId");
                        for(int i=0;i!=table.getNumRows();++i) {
                            ResourceId nodeId = ResourceId.valueOf(id.getString(i));
                            String nodeLabel = label.getString(i);

                            if(level.isRoot()) {
                                nodes.add(new Node(nodeId, nodeLabel));
                            } else {
                                if(selectedParentId.asString().equals(parent.getString(i))) {
                                    nodes.add(new Node(nodeId, selectedParentId, nodeLabel));
                                }
                            }
                        }
                        return nodes;
                    }
                });
            }
        };
    }
}
