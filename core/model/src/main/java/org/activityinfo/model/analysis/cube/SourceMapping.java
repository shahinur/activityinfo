package org.activityinfo.model.analysis.cube;

import com.google.common.collect.Lists;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.ResourceId;

import java.util.Arrays;
import java.util.List;

/**
 * Defines a mapping from a {@code Form} to the <em>dimensions</em> and <em>measures</em> of a cube.
 */
public class SourceMapping {
    private ResourceId sourceId;
    private List<MeasureMapping> measureMappings = Lists.newArrayList();
    private List<AttributeLoading> attributeLoadings = Lists.newArrayList();
    private List<AttributeMapping> attributeMappings = Lists.newArrayList();

    private String timeExpression;


    public SourceMapping(FormClass costs) {
        this.sourceId = costs.getId();
    }

    public MeasureMapping addMeasure(MeasureModel measure) {
        MeasureMapping mapping = new MeasureMapping();
        mapping.setMeasureId(measure.getId());
        measureMappings.add(mapping);

        return mapping;
    }

    public void addLoadings(AttributeLoading... loadings) {
       attributeLoadings.addAll(Arrays.asList(loadings));
    }

    public void addAttributeMappings(AttributeMapping... attributeMapping) {
        attributeMappings.addAll(Arrays.asList(attributeMapping));
    }

    public List<MeasureMapping> getMeasureMappings() {
        return measureMappings;
    }

    public List<AttributeLoading> getAttributeLoadings() {
        return attributeLoadings;
    }

    public ResourceId getSourceId() {
        return sourceId;
    }

    public String getTimeExpression() {
        return timeExpression;
    }

    public void setTimeExpression(String timeExpression) {
        this.timeExpression = timeExpression;
    }

    public List<AttributeMapping> getAttributeMappings() {
        return attributeMappings;
    }
}
