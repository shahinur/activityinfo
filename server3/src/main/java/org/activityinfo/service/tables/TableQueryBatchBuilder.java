package org.activityinfo.service.tables;


import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.formTree.FormTree;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.service.store.ResourceStore;
import org.activityinfo.model.table.ColumnType;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.service.tables.join.Join;
import org.activityinfo.service.tables.join.JoinLink;
import org.activityinfo.service.tables.views.ForeignKeyColumn;
import org.activityinfo.service.tables.views.PrimaryKeyMap;

import java.util.List;
import java.util.Map;

/**
 * Constructs a batch of queries required to construct a Table
 * from the underlying graph.
 */
public class TableQueryBatchBuilder {

    private final ResourceStore store;

    /**
     * We want to do one pass over each FormClass so
     * keep track of what we need
     */
    private Map<ResourceId, TableScan> tableMap = Maps.newHashMap();


    public TableQueryBatchBuilder(ResourceStore store) {
        this.store = store;
    }

    /**
     * Adds a query to the batch for a column composed of a several possible nodes within
     * the FormTree.
     *
     * @param columnType the type to which the columns should be coerced
     * @return a ColumnView Supplier that can be used to retrieve the result after the batch
     * has finished executing.
     */
    public Supplier<ColumnView> addColumn(ColumnType columnType, List<FormTree.Node> nodes) {
        Preconditions.checkArgument(!nodes.isEmpty(), "nodes cannot be empty");

        if(nodes.size() == 1) {
            return addColumn(columnType, Iterables.getOnlyElement(nodes));
        } else {
            List<Supplier<ColumnView>> sources = Lists.newArrayList();
            for(FormTree.Node node : nodes) {
                sources.add(addColumn(columnType, node));
            }
            return new ColumnCombiner(columnType, sources);
        }
    }

    /**
     * Adds a query to the batch for a column derived from a single node within the FormTree, along
     * with any necessary join structures required to join this column to the base table, if the column
     * is nested.
     *
     * @return a ColumnView Supplier that can be used to retrieve the result after the batch
     * has finished executing.
     */
    private Supplier<ColumnView> addColumn(ColumnType columnType, FormTree.Node node) {

        if(node.isRoot()) {
            // simple root column
            return getDataColumn(columnType, node);

        } else {
            // requires join
            return getNestedColumn(columnType, node);
        }
    }

    /**
     * Adds a query to the batch for an empty column. It may still be required to hit the data store
     * to find the number of rows.
     */
    public Supplier<ColumnView> addEmptyColumn(ColumnType type, ResourceId formClassId) {
        return getTable(formClassId).fetchEmptyColumn(type);
    }

    /**
     * Adds a query to the batch for a nested column, which will be joined based on the structure
     * of the FormTree
     * @return a ColumnView Supplier that can be used to retrieve the result after the batch
     * has finished executing.
     */
    private Supplier<ColumnView> getNestedColumn(ColumnType columnType, FormTree.Node node) {

        // Schedule the links we need to join the node to the base form
        List<FormTree.Node> path = node.getSelfAndAncestors();
        List<JoinLink> links = Lists.newArrayList();
        for(int i=1;i<path.size();++i) {
            FormTree.Node left = path.get(i-1);
            FormTree.Node right = path.get(i);
            links.add(addJoinLink(left, right.getDefiningFormClass()));
        }

        // Schedule the actual column to be joined
        Supplier<ColumnView> column = getDataColumn(columnType, node);

        return new Join(links, column);
    }

    private JoinLink addJoinLink(FormTree.Node leftField, FormClass rightForm) {
        TableScan leftTable = getTable(leftField.getDefiningFormClass().getId());
        TableScan rightTable = getTable(rightForm.getId());

        Supplier<ForeignKeyColumn> foreignKey = leftTable.fetchForeignKey(leftField.getFieldId().asString());
        Supplier<PrimaryKeyMap> primaryKey = rightTable.fetchPrimaryKey();

        return new JoinLink(foreignKey, primaryKey);
    }


    public Supplier<ColumnView> getIdColumn(ResourceId classId) {
        return getTable(classId).fetchPrimaryKeyColumn();
    }

    private Supplier<ColumnView> getDataColumn(ColumnType columnType, FormTree.Node node) {
        return getTable(node.getDefiningFormClass().getId()).fetchColumn(node, columnType);
    }

    private TableScan getTable(ResourceId classId) {
        TableScan tableScan = tableMap.get(classId);
        if(tableScan == null) {
            tableScan = new TableScan(store, classId);
            tableMap.put(classId, tableScan);
        }
        return tableScan;
    }

    /**
     * Executes the batch
     */
    public void execute() {
        for(TableScan tableScan : tableMap.values()) {
            tableScan.execute();
        }
    }

}
