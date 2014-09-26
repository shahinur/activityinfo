package org.activityinfo.model.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.type.number.Quantity;
import org.activityinfo.model.type.number.QuantityType;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertThat;

public class RecordSerializationTest {


    @Test
    public void defaultValueProperty() throws IOException {

        FormField field = new FormField(Resources.generateId());
        field.setLabel("Default Field");
        field.setCode("ABC");
        field.setDefaultValue(new Quantity(0, "%"));
        field.setType(new QuantityType().setUnits("%"));

        ObjectMapper objectMapper = ObjectMapperFactory.get();
        String json = objectMapper.writeValueAsString(field);
        Record readRecord = objectMapper.readValue(json, Record.class);
        assertThat(readRecord.getRecord("defaultValue").asMap(),
            hasEntry(equalTo("@type"), equalTo((Object) QuantityType.TYPE_CLASS.getId())));


    }

}