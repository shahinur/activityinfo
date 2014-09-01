package org.activityinfo.service.tables;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.formTree.FormTree;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.table.*;
import org.activityinfo.service.store.StoreAccessor;
import org.activityinfo.service.tree.FormTreeBuilder;

import java.util.List;
import java.util.Map;

public class TableBuilder {

    private final StoreAccessor resourceStore;
    private final FormTreeBuilder formTreeService;

    public TableBuilder(StoreAccessor resourceStore) {
        this.resourceStore = resourceStore;
        this.formTreeService = new FormTreeBuilder(resourceStore);
    }

    public TableData buildTable(TableModel table) throws Exception {

        ResourceId classId = table.getRowSources().get(0).getRootFormClass();
        FormTree tree = formTreeService.queryTree(classId);
        FormClass formClass = tree.getRootFormClasses().get(classId);

        // We want to make at most one pass over every row set we need to scan,
        // so first queue up all necessary work before executing
        TableQueryBatchBuilder batch = new TableQueryBatchBuilder(resourceStore);
        Map<String, Supplier<ColumnView>> columnViews = Maps.newHashMap();

        for(ColumnModel column : table.getColumns()) {
            Preconditions.checkNotNull(column.getType(), "column.getType()");

            if(column.getSource() instanceof FieldSource) {
                FieldSource source = (FieldSource) column.getSource();
                List<FormTree.Node> sourceNodes = source.select(tree);
                if(sourceNodes.isEmpty()) {
                    columnViews.put(column.getId(), batch.addEmptyColumn(column.getType(), formClass));
                } else {
                    columnViews.put(column.getId(), batch.addColumn(column.getType(), sourceNodes));
                }
            } else if(column.getSource() instanceof ResourceIdSource) {
                columnViews.put(column.getId(), batch.getIdColumn(formClass));
            } else {
                throw new UnsupportedOperationException("Field source " + column.getSource());
            }
        }

        // Now execute the batch
        batch.execute();

        int numRows = -1;

        Map<String, ColumnView> dataMap = Maps.newHashMap();
        for(Map.Entry<String, Supplier<ColumnView>> entry : columnViews.entrySet()) {
            ColumnView view = entry.getValue().get();
            if(numRows == -1) {
                numRows = view.numRows();
            } else {
                assert numRows == view.numRows();
            }
            dataMap.put(entry.getKey(), view);
        }

        return new TableData(numRows, dataMap);
    }
}
