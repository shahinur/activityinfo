package org.activityinfo.model.analysis;

import com.google.common.collect.Lists;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.IsRecord;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.ListFieldType;
import org.activityinfo.model.type.SubFormType;

import java.util.List;

public class PivotTableModel implements IsRecord {

    public static final ResourceId CLASS_ID = ResourceId.valueOf("_pivot");

    private List<MeasureModel> measures = Lists.newArrayList();

    private List<DimensionModel> dimensions = Lists.newArrayList();

    public PivotTableModel() {

    }

    public List<MeasureModel> getMeasures() {
        return measures;
    }

    public List<DimensionModel> getDimensions() {
        return dimensions;
    }

    public static FormClass getFormClass() {
        FormClass formClass = new FormClass(CLASS_ID);
        formClass.addElement(new FormField(ResourceId.valueOf("measure"))
        .setLabel("Measures")
        .setRequired(true)
        .setType(new ListFieldType(new SubFormType(MeasureModel.CLASS_ID))));

        return formClass;
    }

    @Override
    public Record asRecord() {
        throw new UnsupportedOperationException();
    }
}
