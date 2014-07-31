package org.activityinfo.migrator;

import org.activityinfo.model.resource.Resource;

import java.io.IOException;

public interface ResourceWriter {

    void write(Resource resource) throws IOException;
}
