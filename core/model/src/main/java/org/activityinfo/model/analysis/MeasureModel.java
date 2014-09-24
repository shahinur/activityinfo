package org.activityinfo.model.analysis;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.PropertyBag;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.Cardinality;
import org.activityinfo.model.type.ReferenceType;
import org.activityinfo.model.type.enumerated.EnumType;
import org.activityinfo.model.type.enumerated.EnumValue;
import org.activityinfo.model.type.expr.ExprFieldType;
import org.activityinfo.model.type.expr.ExprValue;

import java.util.Map;

public class MeasureModel extends AbstractModel<MeasureModel> {


    public static final ResourceId CLASS_ID = ResourceId.valueOf("_measure");

    private Map<String, String> dimensionTags = Maps.newHashMap();

    public MeasureModel() {
    }

    public MeasureModel(PropertyBag propertyBag) {
        super(propertyBag);
    }

    @Override
    public ResourceId getClassId() {
        return CLASS_ID;
    }

    public String getId() {
        return getString("id");
    }

    public MeasureModel setId(String id) {
        return set("id", id);
    }


    public MeasurementType getMeasurementType() {
        return get("measurementType", MeasurementType.class);
    }

    public void setMeasurementType(MeasurementType type) {
        set("measurementType", type);
    }

    public void setDimensionTag(String dimensionId, String dimensionValue) {
        dimensionTags.put(dimensionId, dimensionValue);
    }

    public Map<String, String> getDimensionTags() {
        return dimensionTags;
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

    public static MeasureModel fromRecord(Record record) {
        return new MeasureModel(record);
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
            new FormField(ResourceId.valueOf("measurementType"))
                .setLabel("Value")
                .setType(new EnumType(Cardinality.SINGLE, Lists.newArrayList(
                    new EnumValue(ResourceId.valueOf("FLOW"), "Flow"),
                    new EnumValue(ResourceId.valueOf("STOCK"), "Stock"))))
                .setRequired(true));

        formClass.addElement(
            new FormField(ResourceId.valueOf("criteria"))
                .setLabel("Criteria")
                .setType(ExprFieldType.INSTANCE)
                .setRequired(false));

        return formClass;
    }
}