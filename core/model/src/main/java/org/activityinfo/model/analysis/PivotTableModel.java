package org.activityinfo.model.analysis;

import com.google.common.collect.Lists;
import org.activityinfo.model.resource.IsResource;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;

import java.util.List;

public class PivotTableModel implements IsResource {

    private ResourceId resourceId;
    private ResourceId ownerId;

    private List<ResourceId> sources = Lists.newArrayList();

    private List<DimensionModel> dimensions = Lists.newArrayList();
    private List<MeasureModel> measures = Lists.newArrayList();

    public PivotTableModel(ResourceId id, ResourceId ownerId) {
        this.resourceId = id;
        this.ownerId = ownerId;
    }

    public PivotTableModel() {
    }

    public List<ResourceId> getSources() {
        return sources;
    }

    public List<DimensionModel> getDimensions() {
        return dimensions;
    }

    public List<MeasureModel> getMeasures() {
        return measures;
    }

    @Override
    public ResourceId getId() {
        return resourceId;
    }

    @Override
    public Resource asResource() {
        throw new UnsupportedOperationException();
    }


}
