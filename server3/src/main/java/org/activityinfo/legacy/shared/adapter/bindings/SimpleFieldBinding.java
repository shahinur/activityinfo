package org.activityinfo.legacy.shared.adapter.bindings;

import com.bedatadriven.rebar.time.calendar.LocalDate;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.legacy.shared.model.EntityDTO;

import java.util.Map;

/**
 * Created by alex on 2/22/14.
 */
public class SimpleFieldBinding implements FieldBinding<EntityDTO> {
    private final ResourceId fieldId;
    private final String propertyName;

    public SimpleFieldBinding(ResourceId fieldId, String propertyName) {
        this.fieldId = fieldId;
        this.propertyName = propertyName;
    }

    @Override
    public void updateInstanceFromModel(FormInstance instance, EntityDTO model) {
        Object value = model.get(propertyName);
        if (value != null) {
            if (value instanceof LocalDate) {
                final LocalDate localDate = (LocalDate) value;
                instance.set(fieldId, localDate.atMidnightInMyTimezone());
            } else {
                instance.set(fieldId, value);
            }
        }
    }

    @Override
    public void populateChangeMap(FormInstance instance, Map<String, Object> changeMap) {
        changeMap.put(propertyName, instance.get(fieldId));
    }
}
