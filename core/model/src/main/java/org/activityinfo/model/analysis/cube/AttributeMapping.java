package org.activityinfo.model.analysis.cube;

import org.activityinfo.model.resource.ResourceId;

public class AttributeMapping {

    private ResourceId attributeId;
    private String memberExpression;

    public AttributeMapping(ResourceId attributeId, String memberExpression) {
        this.attributeId = attributeId;
        this.memberExpression = memberExpression;
    }

    public ResourceId getAttributeId() {
        return attributeId;
    }

    public String getMemberExpression() {
        return memberExpression;
    }
}
