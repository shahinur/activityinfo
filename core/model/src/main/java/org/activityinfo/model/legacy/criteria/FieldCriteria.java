package org.activityinfo.model.legacy.criteria;

import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.legacy.Projection;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.formTree.FieldPath;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Accepts an instance if the given field value matches
 * exactly.
 */
public class FieldCriteria implements Criteria {

    private FieldPath fieldPath;
    private Object value;

    public FieldCriteria(@Nonnull ResourceId fieldId, @Nonnull Object value) {
        this.fieldPath = new FieldPath(fieldId);
        this.value = value;
    }

    public FieldCriteria(FieldPath fieldPath, Object value) {
        this.fieldPath = fieldPath;
        this.value = value;
    }

    @Override
    public void accept(CriteriaVisitor visitor) {
        visitor.visitFieldCriteria(this);
    }

    @Override
    public boolean apply(@Nullable FormInstance input) {
        if(fieldPath.isNested()) {
            return true;
        } else {
            return Objects.equals(input.get(fieldPath.getRoot()), value);
        }
    }

    @Override
    public boolean apply(@Nonnull Projection input) {
        return Objects.equals(input.getValue(fieldPath), value);
    }

    public ResourceId getFieldId() {
        return fieldPath.getRoot();
    }

    public FieldPath getFieldPath() {
        return fieldPath;
    }

    public Object getValue() {
        return value;
    }
}
