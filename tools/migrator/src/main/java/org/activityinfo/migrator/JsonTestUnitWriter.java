package org.activityinfo.migrator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.activityinfo.model.json.ObjectMapperFactory;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class JsonTestUnitWriter implements ResourceWriter {
    private final File file;
    private List<Resource> resources;

    private final ObjectMapper objectMapper = ObjectMapperFactory.get();

    public JsonTestUnitWriter(File file) throws IOException {
        this.file = file;
        this.resources = Lists.newArrayList();
    }

    @Override
    public void beginResources() throws Exception {

    }

    @Override
    public void writeResource(Resource resource, Date dateCreated, Date dateDeleted) throws IOException {
        resources.add(resource.copy());
    }

    @Override
    public void endResources() throws Exception {

    }

    @Override
    public void writeUserIndex(Multimap<ResourceId, ResourceId> resources) throws Exception {

    }

    @Override
    public void close()  {

    }

    public void finish() throws IOException {
        objectMapper.writeValue(file, resources);
    }
}
