package org.activityinfo.store.hrd.entity.workspace;

import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.number.Quantity;
import org.activityinfo.model.type.number.QuantityType;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

public class SerializedRecordTest {

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
        Assert.assertThat(formResource.getValue().getRecordList("elements").get(0).getRecord("defaultValue").asMap(),
            Matchers.hasEntry(CoreMatchers.equalTo("@type"), Matchers.equalTo((Object) QuantityType.TYPE_CLASS.getId())));

        String json = SerializedRecord.toJson(formResource.getValue());

        System.out.println(json);

        Resource readResource = Resources.createResource();
        readResource.setId(formId);
        readResource.setValue(SerializedRecord.fromJson(json));

        Assert.assertThat(readResource.getValue().getRecordList("elements").get(0).getRecord("defaultValue").asMap(),
            Matchers.hasEntry(CoreMatchers.equalTo("@type"), Matchers.equalTo((Object) QuantityType.TYPE_CLASS.getId())));

        FormClass readClass = FormClass.fromResource(readResource);
        Assert.assertThat(readClass.getFields().get(0).getDefaultValue(), Matchers.equalTo((FieldValue) new Quantity(41, "%")));

    }
}