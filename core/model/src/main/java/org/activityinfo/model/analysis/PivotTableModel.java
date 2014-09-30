package org.activityinfo.model.analysis;

import com.google.common.collect.Lists;
import org.activityinfo.model.annotation.RecordBean;

import java.util.List;

@RecordBean(classId = "_pivotTable")
public class PivotTableModel {

    private String label;
    private List<MeasureModel> measures = Lists.newArrayList();
    private List<DimensionModel> dimensions = Lists.newArrayList();

    public PivotTableModel() {
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


}
