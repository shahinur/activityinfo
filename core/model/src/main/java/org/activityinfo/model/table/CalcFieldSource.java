package org.activityinfo.model.table;

import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.ResourceId;

public class CalcFieldSource extends ColumnSource {

    private ResourceId formClassId;
    private String expression;

    public CalcFieldSource(ResourceId formClassId, String expression) {
        this.formClassId = formClassId;
        this.expression = expression;
    }

    public ResourceId getFormClassId() {
        return formClassId;
    }

    public String getExpression() {
        return expression;
    }

    @Override
    public Record asRecord() {
        throw new UnsupportedOperationException();
    }
}
