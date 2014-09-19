package org.activityinfo.service.cubes;

import com.google.common.base.Supplier;
import org.activityinfo.model.analysis.cube.AttributeLoading;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.service.tables.RowSetBuilder;

public class LoadedAttributeReader implements AttributeReader {
    private int attrIndex;
    private AttributeLoading loading;
    private Supplier<ColumnView> criteriaSupplier;
    private Supplier<ColumnView> factorSupplier;

    private ColumnView criteria;
    private ColumnView factor;

    public LoadedAttributeReader(int attrIndex, AttributeLoading loading) {
        this.attrIndex = attrIndex;
        this.loading = loading;
    }

    public void scheduleRequests(RowSetBuilder batch) {
        criteriaSupplier = batch.fetch(loading.getCriteriaExpression());
        factorSupplier = batch.fetch(loading.getFactorExpression());
    }

    @Override
    public int getAttributeIndex() {
        return attrIndex;
    }

    public void start() {
        this.criteria = criteriaSupplier.get();
        this.factor = factorSupplier.get();
    }


    public double factor(int rowIndex) {
        if(criteria.getBoolean(rowIndex) == ColumnView.TRUE) {
            return factor.getDouble(rowIndex);
        } else {
            return Double.NaN;
        }
    }

    public String member(int rowIndex) {
        return loading.getMemberName();
    }

}
