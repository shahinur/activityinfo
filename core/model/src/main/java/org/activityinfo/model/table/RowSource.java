package org.activityinfo.model.table;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.activityinfo.model.resource.IsRecord;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.ReferenceValue;

public class RowSource implements IsRecord {

    private ResourceId rootFormClass;
    private String criteriaExpression;

    @JsonCreator
    public RowSource(@JsonProperty("rootFormClass") ResourceId rootFormClass) {
        this.rootFormClass = rootFormClass;
    }

    public ResourceId getRootFormClass() {
        return rootFormClass;
    }

    public RowSource setRootFormClass(ResourceId rootFormClass) {
        this.rootFormClass = rootFormClass;
        return this;
    }

    public String getCriteriaExpression() {
        return criteriaExpression;
    }

    public void setCriteriaExpression(String criteriaExpression) {
        this.criteriaExpression = criteriaExpression;
    }

    @Override
    public Record asRecord() {
        Record record = new Record();
        record.set("rootFormClass", new ReferenceValue(rootFormClass).asRecord());
        record.set("criteria", criteriaExpression);
        return record;
    }

    public static RowSource fromRecord(Record record) {
        ReferenceValue rootFormClassRef = ReferenceValue.fromRecord(record.isRecord("rootFormClass"));
        RowSource source = new RowSource(rootFormClassRef.getResourceId());
        source.setCriteriaExpression(record.isString("criteriaExpression"));
        return source;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RowSource rowSource = (RowSource) o;

        if (criteriaExpression != null ? !criteriaExpression.equals(rowSource.criteriaExpression) :
                rowSource.criteriaExpression != null) {
            return false;
        }
        if (!rootFormClass.equals(rowSource.rootFormClass)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = rootFormClass.hashCode();
        result = 31 * result + (criteriaExpression != null ? criteriaExpression.hashCode() : 0);
        return result;
    }
}
