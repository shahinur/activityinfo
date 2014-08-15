package org.activityinfo.service.jaxrs;

import org.activityinfo.model.resource.ResourceId;

import javax.ws.rs.core.EntityTag;

public final class EntityTags {

    private static final boolean WEAK = true;

    public static EntityTag ofResource(ResourceId id, long version) {
        return new EntityTag(id.asString() + "@" + Long.toHexString(version), WEAK);
    }
}
