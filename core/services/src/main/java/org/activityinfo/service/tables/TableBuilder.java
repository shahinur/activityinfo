package org.activityinfo.service.tables;

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import org.activityinfo.model.expr.diagnostic.ExprException;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.formTree.FormTree;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.table.ColumnModel;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.service.tree.FormTreeBuilder;

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

        RowSetBuilder rowSetBuilder = new RowSetBuilder(formClass.getId(), batch);

        Map<String, Supplier<ColumnView>> columnViews = Maps.newHashMap();

        for(ColumnModel column : table.getColumns()) {
            Supplier<ColumnView> view;
            try {
                view = rowSetBuilder.fetch(column.getExpression().getExpression());
            } catch(ExprException e) {
                throw new QuerySyntaxException("Syntax error in column " + column.getId() +
                        " '" + column.getExpression().getExpression() + "' : " + e.getMessage(), e);
            }
            columnViews.put(column.getId(), view);
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
                if(numRows != view.numRows()) {
                    throw new IllegalStateException("Columns are of unequal length: " + dataMap);
                }
            }
            dataMap.put(entry.getKey(), view);
        }

        return new TableData(numRows, dataMap);
    }

}
