package org.activityinfo.model.analysis;

import com.google.common.base.Optional;
import org.activityinfo.model.resource.ResourceId;

import java.util.ArrayList;
import java.util.List;

public class DimensionModel {

    private String id;
    private String label;
    private String description;
    private final List<DimensionSource> sources = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<DimensionSource> getSources() {
        return sources;
    }


    public Optional<DimensionSource> getSource(ResourceId sourceId) {
        for(DimensionSource source : sources) {
            if(source.getSourceId().equals(sourceId)) {
                return Optional.of(source);
            }
        }
        return Optional.absent();
    }
}
