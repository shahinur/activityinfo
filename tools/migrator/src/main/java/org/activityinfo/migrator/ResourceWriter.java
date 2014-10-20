package org.activityinfo.migrator;

import org.activityinfo.model.resource.Resource;

import java.util.Date;

public interface ResourceWriter {

    void beginResources() throws Exception;

    void writeResource(int userId, Resource resource, Date dateCreated, Date dateDeleted, long snapshotVersion) throws Exception;

    void endResources() throws Exception;

    void close() throws Exception;
}
