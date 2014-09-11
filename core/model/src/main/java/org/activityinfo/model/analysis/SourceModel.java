package org.activityinfo.model.analysis;

import org.activityinfo.model.resource.ResourceId;

public class SourceModel {

    private ResourceId sourceId;
    private String expression;
    private String criteria;

    public SourceModel(ResourceId sourceId, ResourceId fieldId) {
        this.sourceId = sourceId;
        this.expression = fieldId.asString();
    }

    public ResourceId getSourceId() {
        return sourceId;
    }

    public void setSourceId(ResourceId sourceId) {
        this.sourceId = sourceId;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getCriteria() {
        return criteria;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }
}
