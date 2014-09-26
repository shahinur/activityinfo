package org.activityinfo.model.analysis;

import com.google.common.collect.Lists;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.record.IsRecord;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.resource.IsResource;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.system.ApplicationProperties;
import org.activityinfo.model.type.ListFieldType;
import org.activityinfo.model.type.RecordFieldType;
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
            .setType(new ListFieldType(new RecordFieldType(MeasureModel.CLASS_ID))));

        formClass.addElement(new FormField(ResourceId.valueOf("dimensions"))
            .setLabel("Dimensions")
            .setType(new ListFieldType(new RecordFieldType(DimensionModel.CLASS_ID))));

        return formClass;
    }

    @Override
    public Record asRecord() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Resource asResource() {
        throw new UnsupportedOperationException();
    }

    public static PivotTableModel fromRecord(Record record) {
        throw new UnsupportedOperationException();
    }

    public static PivotTableModel fromResource(Resource resource) {
        throw new UnsupportedOperationException();
    }

}
