package org.activityinfo.model.analysis;

import com.google.common.collect.Lists;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.*;
import org.activityinfo.model.system.ApplicationProperties;
import org.activityinfo.model.type.*;
import org.activityinfo.model.type.primitive.TextType;

import java.util.List;

public class PivotTableModel implements IsRecord, IsResource {

    public static final ResourceId CLASS_ID = ResourceId.valueOf("_pivot");

    public static final String LABEL_FIELD_ID = "_pivot_label";

    private ResourceId id;
    private ResourceId ownerId;
    private String label;
    private List<MeasureModel> measures = Lists.newArrayList();
    private List<DimensionModel> dimensions = Lists.newArrayList();

    public PivotTableModel() {

    }

    @Override
    public ResourceId getId() {
        return id;
    }

    public void setId(ResourceId id) {
        this.id = id;
    }

    public ResourceId getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(ResourceId ownerId) {
        this.ownerId = ownerId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<MeasureModel> getMeasures() {
        return measures;
    }

    public void addMeasure(MeasureModel measure) {
        measures.add(measure);
    }

    public List<DimensionModel> getDimensions() {
        return dimensions;
    }


    public void addDimension(DimensionModel dimensionModel) {
        dimensions.add(dimensionModel);
    }

    public static FormClass getFormClass() {
        FormClass formClass = new FormClass(CLASS_ID);
        formClass.addElement(new FormField(ResourceId.valueOf(LABEL_FIELD_ID))
            .setLabel("Label")
            .setRequired(true)
            .setSuperProperty(ApplicationProperties.LABEL_PROPERTY)
            .setType(TextType.INSTANCE));

        formClass.addElement(new FormField(ResourceId.valueOf("measures"))
            .setLabel("Measures")
            .setRequired(true)
            .setType(new ListFieldType(new SubFormType(MeasureModel.CLASS_ID))));

        formClass.addElement(new FormField(ResourceId.valueOf("dimensions"))
            .setLabel("Dimensions")
            .setType(new ListFieldType(new SubFormType(DimensionModel.CLASS_ID))));

        return formClass;
    }

    @Override
    public Record asRecord() {
        return new Record().set("measures", ListFieldValue.ofSubForms(measures).asRecord());
    }

    @Override
    public Resource asResource() {
        Resource resource = Resources.createResource();
        resource.setId(id);
        resource.set("classId", CLASS_ID.asString());
        resource.set(LABEL_FIELD_ID, label);
        resource.setOwnerId(ownerId);
        resource.set("measures", ListFieldValue.ofSubForms(measures).asRecord());
        resource.set("dimensions", ListFieldValue.ofSubForms(dimensions).asRecord());
        return resource;
    }

    public static PivotTableModel fromRecord(Record record) {
        return fromProperties(record);
    }

    public static PivotTableModel fromResource(Resource resource) {
        return fromProperties(resource);
    }

    private static PivotTableModel fromProperties(PropertyBag resource) {
        PivotTableModel model = new PivotTableModel();
        model.setLabel(resource.isString(LABEL_FIELD_ID));
        ListFieldValue measureList = Types.read(resource, "measures", ListFieldType.TYPE_CLASS);
        for(FieldValue value : measureList.getElements()) {
            if(value instanceof SubFormValue) {
                model.addMeasure(new MeasureModel(((SubFormValue) value).getFields()));
            }
        }
        ListFieldValue dimensionList = Types.read(resource, "measures", ListFieldType.TYPE_CLASS);
        for(FieldValue value : dimensionList.getElements()) {
            if(value instanceof SubFormValue) {
                model.addDimension(DimensionModel.fromRecord(((SubFormValue) value).getFields()));
            }
        }
        return model;
    }
}
