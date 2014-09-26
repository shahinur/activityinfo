package org.activityinfo.model.resource;

import org.activityinfo.model.record.IsRecord;
import org.activityinfo.model.record.Record;

import java.util.Date;

public class Resources {

    /**
     * The resource id of the user database.
     *
     * All registered users are resources owned by this
     * resource.
     */
    public static final ResourceId USER_DATABASE_ID = ResourceId.valueOf("_users");

    public static final int RADIX = 10;

    public static final ResourceId ROOT_ID = ResourceId.valueOf("_root");

    public static long COUNTER = 1;

    public static Resource createResource() {
        return new Resource();
    }

    public static Resource createResource(Record record) {
        Resource resource = new Resource();
        resource.setId(generateId());
        resource.setValue(record);
        return resource;
    }

    public static ResourceId generateId() {
        return ResourceId.valueOf("c" + Long.toString(new Date().getTime(), Character.MAX_RADIX) +
                                  Long.toString(COUNTER++, Character.MAX_RADIX));
    }

    /**
     * Creates a new resource with the given
     * @param parentId
     * @param value
     * @return
     */
    public static Resource newResource(ResourceId parentId, IsRecord value) {
        Resource resource = new Resource();
        resource.setId(generateId());
        resource.setOwnerId(parentId);
        resource.setValue(value.asRecord());
        resource.setVersion(0L);
        return resource;
    }
}
