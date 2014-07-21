package org.activityinfo.legacy.shared.adapter.bindings;

import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.legacy.shared.model.DTO;
import org.activityinfo.legacy.shared.model.EntityDTO;

import java.util.Map;

import static org.activityinfo.model.legacy.CuidAdapter.getLegacyIdFromCuid;

/**
 * Created by alex on 2/22/14.
 */
public class NestedFieldBinding implements FieldBinding<EntityDTO> {
    private final ResourceId fieldId;
    private final char domain;
    private final String propertyName;

    public NestedFieldBinding(ResourceId fieldId, char domain, String propertyName) {
        this.fieldId = fieldId;
        this.domain = domain;
        this.propertyName = propertyName;
    }

    @Override
    public void updateInstanceFromModel(FormInstance instance, EntityDTO model) {
        DTO value = model.get(propertyName);
        if (value instanceof EntityDTO) {
            instance.set(fieldId, CuidAdapter.cuid(domain, ((EntityDTO) value).getId()));
        }
    }

    @Override
    public void populateChangeMap(FormInstance instance, Map<String, Object> changeMap) {
        ResourceId resourceId = instance.getInstanceId(fieldId);
        if (resourceId != null) {
            changeMap.put(propertyName + "Id", getLegacyIdFromCuid(resourceId));
        }
    }
}
