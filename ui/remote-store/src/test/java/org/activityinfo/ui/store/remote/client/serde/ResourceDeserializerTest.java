package org.activityinfo.ui.store.remote.client.serde;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.activityinfo.model.json.ObjectMapperFactory;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class ResourceDeserializerTest {

    @Test
    public void test() throws JsonProcessingException {

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
                .set("list", Arrays.asList("a", "b", "c")));

        ObjectMapper mapper = ObjectMapperFactory.get();
        String json = mapper.writeValueAsString(resource);

        ResourceDeserializer deserializer = new ResourceDeserializer();
        Resource reread = deserializer.doDeserialize(new TestJsonReader(json), null, null);

        Assert.assertThat(reread, CoreMatchers.equalTo(resource));
    }

}