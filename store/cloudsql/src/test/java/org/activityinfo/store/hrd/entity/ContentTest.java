package org.activityinfo.store.hrd.entity;

import com.google.appengine.api.datastore.Text;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.number.Quantity;
import org.activityinfo.model.type.number.QuantityType;
import org.hamcrest.Matchers;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertThat;

public class ContentTest {

    @Test
    public void defaultProperty() {
        FormField field = new FormField(Resources.generateId());
        field.setLabel("Default Field");
        field.setCode("ABC");
        field.setDefaultValue(new Quantity(41, "%"));
        field.setType(new QuantityType().setUnits("%"));

        ResourceId formId = Resources.generateId();
        FormClass form = new FormClass(formId);
        form.setOwnerId(Resources.ROOT_ID);
        form.setLabel("My Form");
        form.addElement(field);

        Resource formResource = form.asResource();
        assertThat(formResource.getValue().getRecordList("elements").get(0).getRecord("defaultValue").asMap(),
            hasEntry(equalTo("@type"), Matchers.equalTo((Object) QuantityType.TYPE_CLASS.getId())));

        String json = Content.writePropertiesAsString(formResource.getValue());

        System.out.println(json);

        Resource readResource = Resources.createResource();
        readResource.setId(formId);
        Content.readPropertiesFromString(readResource, new Text(json));

        assertThat(readResource.getValue().getRecordList("elements").get(0).getRecord("defaultValue").asMap(),
            hasEntry(equalTo("@type"), Matchers.equalTo((Object) QuantityType.TYPE_CLASS.getId())));

        FormClass readClass = FormClass.fromResource(readResource);
        assertThat(readClass.getFields().get(0).getDefaultValue(), Matchers.equalTo((FieldValue)new Quantity(41, "%")));

    }
}