package org.activityinfo.model.table;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.activityinfo.model.annotation.RecordBean;
import org.activityinfo.model.annotation.Reference;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.ResourceId;

@RecordBean(classId = "_rowSource")
public class RowSource {

    private ResourceId rootFormClass;

    public RowSource() {
    }

    @JsonCreator
    public RowSource(@JsonProperty("rootFormClass") ResourceId rootFormClass) {
        this.rootFormClass = rootFormClass;
    }

    @Reference(range = FormClass.class)
    public ResourceId getRootFormClass() {
        return rootFormClass;
    }

    public RowSource setRootFormClass(ResourceId rootFormClass) {
        this.rootFormClass = rootFormClass;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RowSource rowSource = (RowSource) o;

        if (rootFormClass != null ? !rootFormClass.equals(rowSource.rootFormClass) : rowSource.rootFormClass != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return rootFormClass != null ? rootFormClass.hashCode() : 0;
    }
}
