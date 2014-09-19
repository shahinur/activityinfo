package org.activityinfo.service.cubes;

import com.google.common.base.Supplier;
import org.activityinfo.model.analysis.cube.AttributeMapping;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.service.tables.RowSetBuilder;

public class MappedAttributeReader implements AttributeReader {

    private AttributeMapping mapping;
    private Supplier<ColumnView> memberViewSupplier;
    private ColumnView memberView;
    private int attributeIndex;

    public MappedAttributeReader(AttributeMapping mapping, int attributeIndex) {
        this.mapping = mapping;
        this.attributeIndex = attributeIndex;
    }

    @Override
    public double factor(int rowIndex) {
        return 1;
    }

    @Override
    public String member(int rowIndex) {
        return memberView.getString(rowIndex);
    }

    @Override
    public int getAttributeIndex() {
        return attributeIndex;
    }

    @Override
    public void start() {
        memberView = memberViewSupplier.get();
    }

    public void scheduleRequests(RowSetBuilder rowSetBuilder) {
        memberViewSupplier = rowSetBuilder.fetch(mapping.getMemberExpression());
    }
}
