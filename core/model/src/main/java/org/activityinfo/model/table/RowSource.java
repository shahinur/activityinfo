package org.activityinfo.model.table;

import org.activityinfo.model.resource.ResourceId;

public class RowSource {

    private ResourceId rootFormClass;
    private String criteriaExpression;

    public ResourceId getRootFormClass() {
        return rootFormClass;
    }

    public RowSource setRootFormClass(ResourceId rootFormClass) {
        this.rootFormClass = rootFormClass;
        return this;
    }


}
