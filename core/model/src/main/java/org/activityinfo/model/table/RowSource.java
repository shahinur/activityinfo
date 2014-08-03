package org.activityinfo.model.table;

import org.activityinfo.model.resource.IsRecord;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.ReferenceValue;

public class RowSource implements IsRecord {

    private ResourceId rootFormClass;
    private String criteriaExpression;

    public RowSource(ResourceId rootFormClass) {
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
}
