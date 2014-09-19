package org.activityinfo.model.analysis.cube;

import org.activityinfo.model.resource.ResourceId;

/**
 * Defines a loading of a source value on an attribute member.
 */
public class AttributeLoading {

    private ResourceId attributeId;
    private String memberName;
    private String criteriaExpression = "true";
    private String factorExpression = "1";

    public String getMemberName() {
        return memberName;
    }

    public String getCriteriaExpression() {
        return criteriaExpression;
    }

    public String getFactorExpression() {
        return factorExpression;
    }

    public ResourceId getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(ResourceId attributeId) {
        this.attributeId = attributeId;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public AttributeLoading where(String criteriaExpression) {
        this.criteriaExpression = criteriaExpression;
        return this;
    }

    public AttributeLoading withFactor(String factorExpression) {
        this.factorExpression = factorExpression;
        return this;
    }

    public AttributeLoading withFactor(double factor) {
        this.factorExpression = Double.toString(factor);
        return this;
    }
}
