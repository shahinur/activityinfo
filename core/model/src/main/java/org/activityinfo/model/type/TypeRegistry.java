package org.activityinfo.model.type;

import com.google.common.collect.Maps;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.type.geo.GeoPointType;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.model.type.time.LocalDateType;

import java.util.Map;

/**
 * Global registry of {@code FieldTypeClass}es.
 */
public class TypeRegistry {

    private static TypeRegistry INSTANCE;

    public static TypeRegistry get() {
        if(INSTANCE == null) {
            INSTANCE = new TypeRegistry();
        }
        return INSTANCE;
    }

    private Map<String, FieldTypeClass> typeMap = Maps.newHashMap();

    private TypeRegistry() {
        register(ReferenceType.TypeClass.INSTANCE);
        register(TextType.TypeClass.INSTANCE);
        register(GeoPointType.INSTANCE);
        register(QuantityType.TypeClass.INSTANCE);
        register(LocalDateType.INSTANCE);
    }

    private void register(FieldTypeClass typeClass) {
        typeMap.put(typeClass.getId(), typeClass);
    }

    public FieldTypeClass getTypeClass(String typeId) {
        FieldTypeClass typeClass = typeMap.get(typeId);
        if(typeClass == null) {
            throw new RuntimeException("Unknown type: " + typeId);
        }
        return typeClass;
    }

    public static FieldTypeClass typeForValue(Record value) {
        String typeId = value.getString(FieldTypeClass.TYPE_FIELD_NAME);
        return get().getTypeClass(typeId);
    }
}
