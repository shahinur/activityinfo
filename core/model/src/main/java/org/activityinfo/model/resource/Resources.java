package org.activityinfo.model.resource;

import java.util.Date;
import java.util.Objects;

public class Resources {

    /**
     * The resource id of the user database.
     *
     * All registered users are resources owned by this
     * resource.
     */
    public static final ResourceId USER_DATABASE_ID = ResourceId.valueOf("_users");

    public static final String ROOT_RESOURCE_ID = "_root";
    public static final int RADIX = 10;
    public static final ResourceId ROOT_ID = ResourceId.valueOf("_root");
    public static long COUNTER = 1;

    public static Resource createResource() {
        return new Resource();
    }

    public static Resource createResource(Record record) {
        Resource resource = new Resource();
        resource.setId(generateId());
        resource.getProperties().putAll(record.getProperties());
        return resource;
    }

    /**
     * @return  {@code} true if {@code x} and {@code y} have the same identity
     * and have equal properties
     */
    public static boolean deepEquals(Resource x, Resource y) {
        if(x == y) {
            return true;
        }
        if(!Objects.equals(x.getId(), y.getId()) ||
           !Objects.equals(x.getOwnerId(), y.getOwnerId())) {
            return false;
        }

        if(x.getProperties().size() != y.getProperties().size()) {
            return false;
        }
        for(String propertyName : x.getProperties().keySet()) {
            if(!Objects.equals(x.get(propertyName), y.get(propertyName))) {
                return false;
            }
        }
        return true;
    }

    public static ResourceId generateId() {
        return ResourceId.valueOf("c" + Long.toString(new Date().getTime(), Character.MAX_RADIX) +
                                  Long.toString(COUNTER++, Character.MAX_RADIX));
    }
}
