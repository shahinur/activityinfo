package org.activityinfo.model.analysis;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.activityinfo.model.resource.IsRecord;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.ResourceId;

import java.util.ArrayList;
import java.util.List;

public class DimensionModel implements IsRecord {

    public static final ResourceId CLASS_ID = ResourceId.valueOf("_dimension");

    private String id;
    private String label;
    private String description;
    private final List<DimensionSource> sources = new ArrayList<>();
    private final List<CategoryMapping> categoryMappings = Lists.newArrayList();

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

    public void addCategoryMapping(CategoryMapping mapping) {
        this.categoryMappings.add(mapping);
    }

    public List<CategoryMapping> getCategoryMappings() {
        return categoryMappings;
    }

    public Optional<DimensionSource> getSource(ResourceId sourceId) {
        for(DimensionSource source : sources) {
            if(source.getSourceId().equals(sourceId)) {
                return Optional.of(source);
            }
        }
        return Optional.absent();
    }

    @Override
    public Record asRecord() {
        throw new UnsupportedOperationException();
//        return new Record().set("id", id)
//            .set("label", label)
//            .set("description", description)
//            .set("sources", SubFormValue.toSubFormList(sources));
    }
}
