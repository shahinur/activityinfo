package org.activityinfo.model.analysis;

import org.activityinfo.model.annotation.RecordBean;
import org.activityinfo.model.annotation.Reference;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.expr.ExprValue;


@RecordBean(classId = "_measure")
public class MeasureModel {

    private String id;
    private String label;
    private MeasurementType measurementType;
    private ResourceId sourceId;
    private ExprValue valueExpression;
    private ExprValue criteriaExpression;


    public MeasureModel() {

    }

    public MeasurementType getMeasurementType() {
        return measurementType;
    }

    public void setMeasurementType(MeasurementType measurementType) {
        this.measurementType = measurementType;
    }

    public void setDimensionTag(String dimensionId, String dimensionValue) {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Reference(range = FormClass.class)
    public ResourceId getSourceId() {
        return sourceId;
    }

    public void setSourceId(ResourceId sourceId) {
        this.sourceId = sourceId;
    }

    public ExprValue getValueExpression() {
        return valueExpression;
    }

    public void setValueExpression(String expression) {
        this.valueExpression = new ExprValue(expression);
    }

    public void setValueExpression(ExprValue valueExpression) {
        this.valueExpression = valueExpression;
    }

    public ExprValue getCriteriaExpression() {
        return criteriaExpression;
    }

    public MeasureModel setCriteriaExpression(String expr) {
        return setCriteriaExpression(ExprValue.valueOf(expr));
    }

    public MeasureModel setCriteriaExpression(ExprValue criteriaExpression) {
        this.criteriaExpression = criteriaExpression;
        return this;
    }


}
