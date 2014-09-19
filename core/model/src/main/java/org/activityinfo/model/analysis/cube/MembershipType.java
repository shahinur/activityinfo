package org.activityinfo.model.analysis.cube;

/**
 * Defines how an attribute's members are determined.
 */
public enum MembershipType {
    /**
     * Attribute members are derived from the field values of the fields
     * bound to the attribute.
     */
    OPEN,

    /**
     * Attribute members are defined completely by the model. Field values that
     * do not belong to the set will be considered invalid.
     */
    CLOSED
}
