package org.activityinfo.ui.store.remote;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;

import java.util.Arrays;

public class TestGenerator {

    public static void main(String[] args) throws JsonProcessingException {

        Resource resource = Resources.createResource();
        resource.setId(ResourceId.generateId());
        resource.setOwnerId(ResourceId.ROOT_ID);
        resource.setVersion(42);
        resource.set("hello", "stringProperty");
        resource.set("boolProp", true);
        resource.set("intProp", 4);
        resource.set("recProp",
                new Record()
                        .set("subStrProp", "A")
                        .set("anotherInt", 34)
                        .set("boolprop", false)
                        .set("list", Arrays.asList(1, 3, "foo")));

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(resource);


    }
}
