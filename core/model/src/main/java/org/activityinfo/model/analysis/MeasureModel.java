package org.activityinfo.model.analysis;

import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.IsRecord;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.ReferenceValue;
import org.activityinfo.model.type.expr.ExprValue;

public class MeasureModel implements IsRecord {

    private String id;
    private String label;
    private MeasurementType measurementType;
    private ReferenceValue sourceId;
    private ExprValue valueExpression;
    private ExprValue criteriaExpression;

    public static final ResourceId CLASS_ID = ResourceId.valueOf("_measure");


    public MeasureModel(Record value) {
    }

    public MeasureModel(FormField value) {
        throw new UnsupportedOperationException();
    }

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

    public ReferenceValue getSourceId() {
        return sourceId;
    }

    public void setSourceId(ReferenceValue sourceId) {
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

    public MeasureModel setSource(ResourceId source) {
        setSourceId(new ReferenceValue(source));
        return this;
    }


    public static MeasureModel fromRecord(Record record) {
        throw new UnsupportedOperationException();
    }

    public Record asRecord() {
        throw new UnsupportedOperationException();
    }


    public static FormClass getFormClass() {
        throw new UnsupportedOperationException();
    }


}
