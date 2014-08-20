package org.activityinfo.model.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.ResourceId;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class ObjectMapperFactoryTest {


    private ObjectMapper objectMapper;

    @Before
    public void setUp() throws Exception {
        objectMapper = ObjectMapperFactory.get();
    }

    @Test
    public void testAsResource() throws IOException {

        FormClass formClass = new FormClass(ResourceId.generateId());
        formClass.setOwnerId(ResourceId.ROOT_ID);
        formClass.setLabel("A Form");

        ObjectMapper mapper = objectMapper;
        String json = mapper.writeValueAsString(formClass);

        System.out.println(json);

        ObjectNode jsonObject = (ObjectNode) mapper.readTree(json);
        assertThat(jsonObject.get("@id").asText(), equalTo(formClass.getId().asString()));
    }
}