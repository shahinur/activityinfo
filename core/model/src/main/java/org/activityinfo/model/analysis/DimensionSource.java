package org.activityinfo.model.analysis;

import org.activityinfo.model.annotation.RecordBean;
import org.activityinfo.model.annotation.Reference;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.expr.ExprValue;

@RecordBean(classId = "_dimensionSource")
public class DimensionSource {

    private ResourceId sourceId;
    private ExprValue expression;
    private ExprValue criteria;

    DimensionSource() {

    }

    public DimensionSource(ResourceId sourceId, ResourceId fieldId) {
        this.sourceId = sourceId;
        this.expression = new ExprValue(fieldId.asString());
    }

    public DimensionSource(ResourceId sourceId, String expr) {
        this.sourceId = sourceId;
        this.expression = new ExprValue(expr);
    }


    @Reference(range = FormClass.class)
    public ResourceId getSourceId() {
        return sourceId;
    }

    public void setSourceId(ResourceId sourceId) {
        this.sourceId = sourceId;
    }

    public ExprValue getExpression() {
        return expression;
    }

    public void setExpression(ExprValue expression) {
        this.expression = expression;
    }

    public ExprValue getCriteria() {
        return criteria;
    }

    public void setCriteria(ExprValue criteria) {
        this.criteria = criteria;
    }
}
