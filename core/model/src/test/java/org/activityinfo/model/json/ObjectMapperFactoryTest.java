package org.activityinfo.model.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.resource.UserResource;
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

        FormClass formClass = new FormClass(Resources.generateId());
        formClass.setOwnerId(Resources.ROOT_ID);
        formClass.setLabel("A Form");

        String json = objectMapper.writeValueAsString(formClass);

        System.out.println(json);

        ObjectNode jsonObject = (ObjectNode) objectMapper.readTree(json);
        assertThat(jsonObject.get("@id").asText(), equalTo(formClass.getId().asString()));
    }

    @Test
    public void userResourceSerialization() throws IOException {
        FormClass formClass = new FormClass(Resources.generateId());
        formClass.setOwnerId(Resources.ROOT_ID);
        formClass.setLabel("A Form");

        UserResource userResource = UserResource.userResource(formClass.asResource()).
                setOwner(false).
                setEditAllowed(true);

        String json = objectMapper.writeValueAsString(userResource);

        System.out.println(json);

        UserResource deserialized = objectMapper.readValue(json, UserResource.class);

        assertThat(deserialized, equalTo(userResource));

    }
}