package org.activityinfo.service.store;

import org.activityinfo.model.resource.Resource;

import java.util.Iterator;

public interface ResourceCursor extends Iterator<Resource>, AutoCloseable {
}
