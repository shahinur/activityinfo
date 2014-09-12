package org.activityinfo.model.analysis;

import org.activityinfo.model.resource.*;
import org.activityinfo.model.type.*;
import org.activityinfo.model.type.expr.ExprFieldType;
import org.activityinfo.model.type.expr.ExprValue;
import org.activityinfo.model.type.number.Quantity;
import org.activityinfo.model.type.primitive.BooleanFieldValue;
import org.activityinfo.model.type.primitive.TextValue;

public class AbstractModel<T> implements IsRecord {

    private PropertyBag propertyBag = new PropertyBag();


    protected final <V extends FieldValue & IsRecord> T set(String name, V fieldValue) {
        propertyBag.set(name, fieldValue.asRecord());
        return (T) this;
    }

    protected final T set(String name, String value) {
        propertyBag.set(name, value);
        return (T) this;
    }

    protected final T set(String name, ResourceId reference) {
        if(reference == null) {
            propertyBag.remove(name);
        } else {
            propertyBag.set(name, new ReferenceValue(reference).asRecord());
        }
        return (T) this;
    }


    protected final String getString(String name) {
        return propertyBag.getString(name);
    }

    protected final ResourceId getReference(String name) {
        ReferenceValue value = get(name, ReferenceType.TYPE_CLASS);
        if(value != null) {
            return value.getResourceId();
        }
        return null;
    }

    protected final String getExprValue(String name) {
        ExprValue value = get(name, ExprFieldType.TYPE_CLASS);
        if(value != null) {
            return value.getExpression();
        }
        return null;
    }

    protected final String getString(String name, String defaultValue) {
        String string = propertyBag.isString(name);
        if(string == null) {
            return defaultValue;
        }
        return string;
    }

    protected final <V extends FieldValue> V get(String name, RecordFieldTypeClass<V> typeClass) {
        Record record = propertyBag.isRecord(name);
        if(record != null) {
            String typeName = record.getString(FieldValue.TYPE_CLASS_FIELD_NAME);
            if(typeClass.getId().equals(typeName)) {
               return typeClass.deserialize(record);
            }
        }
        return null;
    }

    protected final FieldValue get(ResourceId fieldId) {
        Object value = propertyBag.get(fieldId.asString());
        if(value == null) {
            return null;

        } else if(value instanceof String) {
            return TextValue.valueOf((String) value);

        } else if(value instanceof Boolean) {
            Boolean booleanValue = (Boolean) value;
            return BooleanFieldValue.valueOf(booleanValue);

        } else if(value instanceof Record) {
            Record record = (Record)value;
            return TypeRegistry.get().deserializeFieldValue(record);

        } else if(value instanceof Double) {
            return new Quantity((Double) value);

        } else {
            throw new UnsupportedOperationException(fieldId.asString() + " = " + value);
        }
    }

    protected final Resource createRecord(ResourceId id, ResourceId owner) {
        Resource resource = Resources.createResource();
        resource.setId(id);
        resource.setOwnerId(owner);
        resource.setAll(propertyBag);
        return resource;
    }

    @Override
    public Record asRecord() {
        Record record = new Record();
        record.setAll(propertyBag);
        return record;
    }
}
