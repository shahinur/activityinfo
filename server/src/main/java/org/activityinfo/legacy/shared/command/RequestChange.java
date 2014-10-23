package org.activityinfo.legacy.shared.command;

import com.extjs.gxt.ui.client.data.RpcMap;
import org.activityinfo.legacy.shared.command.result.VoidResult;
import org.activityinfo.legacy.shared.model.EntityDTO;

import java.util.HashMap;
import java.util.Map;

public final class RequestChange implements Command<VoidResult> {

    public static final String DELETE = "DELETE";
    public static final String UPDATE = "UPDATE";

    private RequestChange() {}

    private String entityType;
    private String entityId;
    private String changeType;
    private RpcMap propertyMap;

    public String getEntityType() {
        return entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getChangeType() {
        return changeType;
    }

    public RpcMap getPropertyMap() {
        return propertyMap;
    }

    public static Delete delete(EntityDTO entity) {
        return new Delete(entity);
    }

    public static Delete delete(String entityType, int id) {
        return new Delete(entityType, id);
    }

    public static UpdateEntity update(EntityDTO model, String... propertiesToChange) {

        Map<String, Object> map = new HashMap<>();
        for (String property : propertiesToChange) {
            map.put(property, model.get(property));
        }

        return new UpdateEntity(model.getEntityName(), model.getId(), map);
    }
}
