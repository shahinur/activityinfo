package org.activityinfo.model.analysis;

import com.google.common.collect.Lists;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.*;
import org.activityinfo.model.type.*;

import java.util.List;

public class PivotTableModel implements IsRecord, IsResource {

    public static final ResourceId CLASS_ID = ResourceId.valueOf("_pivot");

    private ResourceId id;
    private ResourceId ownerId;
    private List<MeasureModel> measures = Lists.newArrayList();
    private List<DimensionModel> dimensions = Lists.newArrayList();

    public PivotTableModel() {

    }

    @Override
    public ResourceId getId() {
        return id;
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

    public static FormClass getFormClass() {
        FormClass formClass = new FormClass(CLASS_ID);
        formClass.addElement(new FormField(ResourceId.valueOf("measures"))
            .setLabel("Measures")
            .setRequired(true)
            .setType(new ListFieldType(new SubFormType(MeasureModel.CLASS_ID))));


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
        resource.setOwnerId(ownerId);
        resource.set("measures", ListFieldValue.ofSubForms(measures).asRecord());
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
        ListFieldValue measureList = Types.read(resource, "measures", ListFieldType.TYPE_CLASS);
        for(FieldValue value : measureList.getElements()) {
            if(value instanceof SubFormValue) {
                model.addMeasure(new MeasureModel(((SubFormValue) value).getFields()));
            }
        }
        return model;
    }
}
