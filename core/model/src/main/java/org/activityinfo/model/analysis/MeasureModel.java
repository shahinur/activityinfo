package org.activityinfo.model.analysis;

import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.ReferenceType;
import org.activityinfo.model.type.expr.ExprFieldType;
import org.activityinfo.model.type.expr.ExprValue;

public class MeasureModel extends AbstractModel<MeasureModel> {


    public static final ResourceId CLASS_ID = ResourceId.valueOf("_measure");

    private AggregationFunction aggregationFunction;
    private MeasurementType type;


    public String getId() {
        return getString("id");
    }

    public MeasureModel setId(String id) {
        return set("id", id);
    }

    public AggregationFunction getAggregationFunction() {
        return aggregationFunction;
    }

    public void setAggregationFunction(AggregationFunction aggregationFunction) {
        this.aggregationFunction = aggregationFunction;
    }

    public MeasurementType getMeasurementType() {
        return type;
    }

    public void setMeasurementType(MeasurementType type) {
        this.type = type;
    }

    /**
     *
     * @return the id of the resource that is the source of this
     * measure
     */
    public ResourceId getSourceId() {
        return getReference("source");
    }

    public MeasureModel setSource(ResourceId source) {
        return set("source", source);
    }

    public MeasureModel setLabel(String label) {
        return set("label", label);
    }

    public String getLabel() {
        return getString("label");
    }

    public MeasureModel setValueExpression(String expr) {
        return set("value", new ExprValue(expr));
    }

    public String getValueExpression() {
        return getExprValue("value");
    }

    public String getCriteriaExpression() {
        return getExprValue("criteria");
    }

    public MeasureModel setCriteriaExpression(String value) {
        return set("criteria", new ExprValue(value));
    }

    public static FormClass getFormClass() {
        FormClass formClass = new FormClass(ResourceId.valueOf("_measure"));

        formClass.addElement(
            new FormField(ResourceId.valueOf("label"))
                .setLabel("Label")
                .setType(ReferenceType.single(FormClass.CLASS_ID))
                .setRequired(true));

        formClass.addElement(
            new FormField(ResourceId.valueOf("source"))
                .setLabel("Source")
                .setType(ReferenceType.single(FormClass.CLASS_ID))
                .setRequired(true));

        formClass.addElement(
            new FormField(ResourceId.valueOf("value"))
                .setLabel("Value")
                .setType(ExprFieldType.INSTANCE)
                .setRequired(true));

        formClass.addElement(
            new FormField(ResourceId.valueOf("criteria"))
                .setLabel("Criteria")
                .setType(ExprFieldType.INSTANCE)
                .setRequired(false));

        return formClass;
    }
}
