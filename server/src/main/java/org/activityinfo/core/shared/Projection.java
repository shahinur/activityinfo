package org.activityinfo.core.shared;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import org.activityinfo.model.formTree.FieldPath;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.ReferenceValue;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * A projection of a set of {@code FormField}s as well as nested
 * {@code FormField}s reachable through {@code REFERENCE} fields.
 *
 * @deprecated Query using {@link org.activityinfo.model.table.TableModel} instead
 */
@Deprecated
public class Projection {

    private final Map<FieldPath, Object> values = Maps.newHashMap();
    private final ResourceId rootInstanceId;
    private final ResourceId rootClassId;

    public Projection(ResourceId rootInstanceId, ResourceId rootClassId) {
        assert rootInstanceId != null;
        assert rootClassId != null;

        this.rootInstanceId = rootInstanceId;
        this.rootClassId = rootClassId;
    }

    public ResourceId getRootInstanceId() {
        return rootInstanceId;
    }


    public ResourceId getRootClassId() {
        return rootClassId;
    }

    public void setValue(FieldPath path, Object value) {
        if(value == null) {
            values.remove(path);
        } else {
            values.put(path, value);
        }
    }

    public Object getValue(FieldPath path) {
        return values.get(path);
    }

    public Set<ResourceId> getReferenceValue(FieldPath path) {
        Object value = values.get(path);
        if(value == null) {
            return Collections.emptySet();
        } else if(value instanceof ReferenceValue) {
            return ((ReferenceValue)value).getResourceIds();
        } else {
            return Collections.singleton((ResourceId) value);
        }
    }


    public Set<ResourceId> getReferenceValue(ResourceId fieldId) {
        return getReferenceValue(new FieldPath(fieldId));
    }

    public String getStringValue(FieldPath fieldPath) {
        Object value = values.get(fieldPath);
        if(value instanceof String) {
            return (String) value;
        }
        return null;
    }


    public String getStringValue(ResourceId rootFieldId) {
        return getStringValue(new FieldPath(rootFieldId));
    }

    @Override
    public String toString() {
        return "[" + Joiner.on(", ").withKeyValueSeparator("=").join(values) + "]";
    }

    public Map<FieldPath, Object> getValueMap() {
        return values;
    }
}
