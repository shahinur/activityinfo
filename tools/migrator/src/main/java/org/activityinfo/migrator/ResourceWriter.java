package org.activityinfo.migrator;

import com.google.common.collect.Multimap;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;

import java.io.IOException;
import java.sql.SQLException;

public interface ResourceWriter {

    void beginResources() throws Exception;

    void writeResource(Resource resource) throws Exception;

    void endResources() throws Exception;

    void writeUserIndex(Multimap<ResourceId, ResourceId> resources) throws Exception;

    void close() throws Exception;
}
