package org.activityinfo.model.type.component;

import org.activityinfo.model.resource.Resource;

public class NullComponentReader<T> implements ComponentReader<T> {
    @Override
    public T read(Resource resource) {
        return null;
    }
}
