package org.activityinfo.model.analysis.cube;

import com.google.common.base.Strings;
import org.activityinfo.model.analysis.MeasurementType;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;

public class MeasureModel {

    private ResourceId id;
    private String label;
    private MeasurementType measurementType;

    private String expression;

    public MeasureModel() {
    }

    /**
     * Creates a new stock measure with the given label and a newly generated cuid.
     *
     * @param label the measure's label.
     * @return a new {@code MeasureModel}
     */
    public static MeasureModel newStockMeasure(String label) {
        MeasureModel measure = new MeasureModel();
        measure.setId(Resources.generateId());
        measure.setMeasurementType(MeasurementType.STOCK);
        measure.setLabel(label);
        return measure;
    }


    /**
     * Creates a new flow measure with the given label and a newly generated cuid.
     *
     * @param label the measure's label.
     * @return a new {@code MeasureModel}
     */
    public static MeasureModel newFlowMeasure(String label) {
        MeasureModel measure = new MeasureModel();
        measure.setId(Resources.generateId());
        measure.setMeasurementType(MeasurementType.FLOW);
        measure.setLabel(label);
        return measure;
    }

    public ResourceId getId() {
        return id;
    }

    public void setId(ResourceId id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = Strings.emptyToNull(expression);
    }

    public boolean isCalculated() {
        return expression != null;
    }

    public MeasurementType getMeasurementType() {
        return measurementType;
    }

    public void setMeasurementType(MeasurementType measurementType) {
        this.measurementType = measurementType;
    }
}
