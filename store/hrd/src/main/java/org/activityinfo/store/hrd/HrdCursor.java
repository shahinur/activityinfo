package org.activityinfo.store.hrd;

import org.activityinfo.model.record.Record;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.service.store.ResourceCursor;
import org.activityinfo.store.hrd.entity.workspace.LatestVersion;

import java.util.Iterator;

public class HrdCursor implements ResourceCursor {

    private Iterator<LatestVersion> it;
    private LatestVersion current = null;

    public HrdCursor(Iterator<LatestVersion> it) {
        this.it = it;
    }

    @Override
    public ResourceId getResourceId() {
        return current.getResourceId();
    }

    @Override
    public Record getRecord() {
        return current.getRecord();
    }

    @Override
    public long getVersion() {
        return current.getVersion();
    }

    @Override
    public long getInitialVersion() {
        return current.getInitialVersion();
    }

    @Override
    public boolean next() {
        if(it.hasNext()) {
            current = it.next();
            return true;
        } else {
            return false;
        }
    }


    @Override
    public void close() throws Exception {
    }

}
