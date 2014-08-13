package org.activityinfo.service.tables.views;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.gwt.user.client.ui.HasText;
import org.activityinfo.core.shared.expr.eval.FormEvalContext;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.model.table.ColumnType;
import org.activityinfo.model.table.columns.ConstantColumnView;
import org.activityinfo.model.table.columns.EmptyColumnView;
import org.activityinfo.model.table.columns.StringArrayColumnView;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.primitive.HasStringValue;
import org.activityinfo.model.type.primitive.TextValue;
import org.activityinfo.service.tables.stats.StringStatistics;

import java.util.List;

public class StringColumnBuilder implements ColumnViewBuilder {

    private List<String> values = Lists.newArrayList();

    // Keep track of some statistics to
    private StringStatistics stats = new StringStatistics();

    private Optional<ColumnView> result = Optional.absent();

    private String fieldName;

    public StringColumnBuilder(ResourceId fieldId) {
        this.fieldName = fieldId.asString();
    }

    @Override
    public void accept(FormEvalContext instance) {
        FieldValue fieldValue = instance.getFieldValue(fieldName);
        String string = null;
        if(fieldValue instanceof HasStringValue) {
            string = ((HasStringValue) fieldValue).asString();
        }
        stats.update(string);
        values.add(string);
    }

    @Override
    public void finalizeView() {
        if(result.isPresent()) {
            throw new IllegalStateException();
        }
        result = Optional.of(build());
    }

    private ColumnView build() {
        if(stats.isEmpty()) {
            return new EmptyColumnView(ColumnType.STRING, values.size());
        } else if(stats.isConstant()) {
            return new ConstantColumnView(values.get(0), values.size());
        } else {
            return new StringArrayColumnView(values);
        }
    }

    @Override
    public ColumnView get() {
        return result.get();
    }
}
