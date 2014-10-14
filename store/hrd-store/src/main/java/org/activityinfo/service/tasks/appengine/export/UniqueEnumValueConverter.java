package org.activityinfo.service.tasks.appengine.export;

import com.google.common.collect.Iterables;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.enumerated.EnumFieldValue;
import org.activityinfo.model.type.enumerated.EnumType;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * Converts an enum value to a single column
 */
public class UniqueEnumValueConverter implements FieldValueConverter<EnumFieldValue> {
    private final Map<ResourceId, String> labelMap;

    public UniqueEnumValueConverter(FormField field, EnumType type) {
        this.labelMap = type.labelMap();
    }

    @Override
    public Object convertValue(@Nonnull EnumFieldValue fieldValue) {
        if(fieldValue.getResourceIds().size() == 1) {
            return labelMap.get(Iterables.getOnlyElement(fieldValue.getResourceIds()));
        } else {
            return null;
        }
    }
}
