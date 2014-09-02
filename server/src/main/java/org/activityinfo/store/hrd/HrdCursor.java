package org.activityinfo.store.hrd;

import org.activityinfo.model.resource.Resource;
import org.activityinfo.service.store.ResourceCursor;

import java.util.Iterator;

public class HrdCursor implements ResourceCursor {

    private Iterator<Resource> it;

    public HrdCursor(Iterator<Resource> it) {
        this.it = it;
    }

    @Override
    public boolean hasNext() {
        return it.hasNext();
    }

    @Override
    public Resource next() {
        return it.next();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }


    @Override
    public void close() throws Exception {
    }

}
