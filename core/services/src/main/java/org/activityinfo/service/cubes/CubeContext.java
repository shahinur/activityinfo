package org.activityinfo.service.cubes;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.activityinfo.model.analysis.cube.AttributeModel;
import org.activityinfo.model.analysis.cube.CubeModel;
import org.activityinfo.model.analysis.cube.DimensionModel;
import org.activityinfo.model.analysis.cube.MeasureModel;
import org.activityinfo.model.resource.ResourceId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CubeContext {

    private CubeModel model;
    private final Map<ResourceId, MeasureModel> measureLookup;
    private final Map<ResourceId, DimensionModel> dimensionLookup;
    private final Map<ResourceId, AttributeModel> attributeLookup;

    private final List<AttributeModel> attributes = Lists.newArrayList();

    public CubeContext(CubeModel model) {
        this.model = model;

        measureLookup = new HashMap<>();
        for(MeasureModel measure : model.getMeasures()) {
            measureLookup.put(measure.getId(), measure);
        }

        dimensionLookup = new HashMap<>();
        attributeLookup = new HashMap<>();
        for(DimensionModel dim : model.getDimensions()) {
            dimensionLookup.put(dim.getId(), dim);
            for(AttributeModel attribute : dim.getAttributes()) {
                attributeLookup.put(attribute.getId(), attribute);
                attributes.add(attribute);
            }
        }
    }

    public MeasureModel getMeasure(ResourceId measureId) {
        return Preconditions.checkNotNull(measureLookup.get(measureId), "Invalid measure id: " + measureId);
    }

    public DimensionModel getDimension(ResourceId dimensionId) {
        return Preconditions.checkNotNull(dimensionLookup.get(dimensionId), "Invalid dimension id: " + dimensionId);
    }

    public boolean isAttributeIncluded(ResourceId attributeId) {
        return attributeLookup.containsKey(attributeId);
    }

    public int getAttributeIndex(ResourceId attributeId) {
        int index = 0;
        for(AttributeModel attributeModel : attributes) {
            if(attributeId.equals(attributeModel.getId())) {
                return index;
            }
            index++;
        }
        throw new IllegalArgumentException("Invalid attribute Id: " + attributeId);
    }

    public int getAttributeCount() {
        return attributeLookup.size();
    }
}
