package org.activityinfo.service.store;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.activityinfo.model.formTree.FieldPath;
import org.activityinfo.model.legacy.CriteriaAnalysis;
import org.activityinfo.model.legacy.InstanceQuery;
import org.activityinfo.model.legacy.Projection;
import org.activityinfo.model.legacy.QueryResult;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.table.*;
import org.activityinfo.promise.Promise;

import java.util.List;

/**
 * Temporary adapter to resolve Projection queries using the TableService
 */
public class ProjectionAdapter {

    private final RemoteStoreService tableService;

    public ProjectionAdapter(RemoteStoreService tableService) {
        this.tableService = tableService;
    }

    public Promise<QueryResult> query(final InstanceQuery query) {

        CriteriaAnalysis analysis = CriteriaAnalysis.analyze(query.getCriteria());
        if(!analysis.isRestrictedToSingleClass()) {
            return Promise.rejected(new UnsupportedOperationException("must be restricted to a single class"));
        }

        final ResourceId classId = analysis.getClassRestriction();

        // Temporary work around: wrap the TableService to return Projections
        TableModel tableModel = new TableModel();
        tableModel.getRowSources().add(new RowSource(classId));
        tableModel.selectResourceId().as("_id");
        tableModel.selectClassId().as("_classId");

        for(FieldPath path : query.getFieldPaths()) {
            tableModel.addColumn(new ColumnModel().setId(path.toString()).setExpression(path));
        }

        return tableService.queryTable(tableModel).then(new Function<TableData, QueryResult>() {
            @Override
            public QueryResult apply(TableData table) {
                return tableToProjectionList(classId, table, query);
            }
        });
    }

    private QueryResult tableToProjectionList(ResourceId classId, TableData table, InstanceQuery query) {

        List<Projection> projections = Lists.newArrayList();

        ColumnView id = table.getColumnView("_id");

        List<FieldPath> columnPaths = Lists.newArrayList(query.getFieldPaths());
        List<ColumnView> columnViews = Lists.newArrayList();

        for(FieldPath fieldPath : columnPaths) {
            columnViews.add(table.getColumnView(fieldPath.toString()));
        }

        for(int row=0;row!=table.getNumRows();++row) {
            Projection projection = new Projection(
                    ResourceId.valueOf(id.getString(row)),
                    classId);

            for(int col=0;col!=columnViews.size();++col) {
                projection.setValue(columnPaths.get(col), columnViews.get(col).get(row));
            }
            if(query.getCriteria().apply(projection)) {
                projections.add(projection);
            }
        }
        return new QueryResult(getPage(query, projections), projections.size());
    }

    private List<Projection> getPage(InstanceQuery query, List<Projection> projections) {

        int fromIndex = query.getOffset();
        int toIndex = Math.min(projections.size(), fromIndex+query.getMaxCount());

        return projections.subList(fromIndex, toIndex);
    }
}
