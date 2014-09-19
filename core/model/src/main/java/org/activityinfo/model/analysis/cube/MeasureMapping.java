package org.activityinfo.model.analysis.cube;

import com.google.common.collect.Lists;
import org.activityinfo.model.resource.ResourceId;

import java.util.Arrays;
import java.util.List;

/**
 * Maps a {@code FormField} to a <em>measure</em>
 */
public class MeasureMapping {

    private ResourceId measureId;

    private String valueExpression;
    private String criteriaExpression;

    private List<AttributeLoading> loadings = Lists.newArrayList();

    public ResourceId getMeasureId() {
        return measureId;
    }

    public void setMeasureId(ResourceId measureId) {
        this.measureId = measureId;
    }

    public String getValueExpression() {
        return valueExpression;
    }

    public MeasureMapping setValueExpression(String valueExpression) {
        this.valueExpression = valueExpression; return this;
    }

    public String getCriteriaExpression() {
        return criteriaExpression;
    }

    public void setCriteriaExpression(String criteriaExpression) {
        this.criteriaExpression = criteriaExpression;
    }

    public void addLoading(AttributeLoading loading) {
        loadings.add(loading);
    }

    public void addLoadings(AttributeLoading... loading) {
        loadings.addAll(Arrays.asList(loading));
    }

    public List<AttributeLoading> getLoadings() {
        return loadings;
    }
}
