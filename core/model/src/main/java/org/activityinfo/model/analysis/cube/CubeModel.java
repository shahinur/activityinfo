package org.activityinfo.model.analysis.cube;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class CubeModel {

    private List<DimensionModel> dimensions = Lists.newArrayList();
    private List<MeasureModel> measures = Lists.newArrayList();
    private List<SourceMapping> mappings = Lists.newArrayList();

    public void addDimensions(DimensionModel... dimensions) {
        this.dimensions.addAll(Arrays.asList(dimensions));
    }

    public void addMeasures(MeasureModel... measures) {
        this.measures.addAll(Arrays.asList(measures));
    }

    public void addMappings(SourceMapping... mappings) {
        this.mappings.addAll(Arrays.asList(mappings));
    }

    public List<DimensionModel> getDimensions() {
        return dimensions;
    }

    public List<MeasureModel> getMeasures() {
        return measures;
    }

    public List<SourceMapping> getMappings() {
        return mappings;
    }
}
