package org.activityinfo.model.type.component;

import org.activityinfo.model.resource.Resource;

/**
 * Reads the string representation of a type's
 * component from a FormInstance resource.
 */
public interface ComponentReader<T> {

    T read(Resource resource);

}
