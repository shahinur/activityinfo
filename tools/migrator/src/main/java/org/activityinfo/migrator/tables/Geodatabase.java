package org.activityinfo.migrator.tables;

import com.google.common.base.Preconditions;
import org.activityinfo.migrator.ResourceMigrator;
import org.activityinfo.migrator.ResourceWriter;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.system.FolderClass;
import org.activityinfo.model.type.primitive.TextType;

import java.sql.Connection;

import static org.activityinfo.model.legacy.CuidAdapter.*;

public class Geodatabase extends ResourceMigrator {

    public static final ResourceId COUNTRY_FORM_CLASS_ID = ResourceId.valueOf("_country");

    @Override
    public void getResources(Connection connection, ResourceWriter writer) throws Exception {
        writeGeodatabase(writer);
        writeCountryForm(writer);
    }

    private void writeGeodatabase(ResourceWriter writer) throws Exception {
        Resource resource = Resources.createResource();
        resource.setId(GEODB_ID);
        resource.set("classId", "_folder");
        resource.setOwnerId(Resources.ROOT_ID);
        resource.set(FolderClass.LABEL_FIELD_ID.asString(), "Geodatabase");

        Preconditions.checkNotNull(resource.getId());

        writer.writeResource(resource);
    }

    private void writeCountryForm(ResourceWriter writer) throws Exception {
        FormClass countryForm = new FormClass(COUNTRY_FORM_CLASS_ID);
        countryForm.setOwnerId(GEODB_ID);
        countryForm.setLabel("Country");
        countryForm.addElement(
            new FormField(field(COUNTRY_FORM_CLASS_ID, NAME_FIELD))
                .setLabel("Name")
                .setType(TextType.INSTANCE)
                .setRequired(true));
        countryForm.addElement(
            new FormField(field(COUNTRY_FORM_CLASS_ID, CODE_FIELD))
                .setLabel("Code")
                .setDescription("ISO 3166-1 alpha-2 code")
                .setType(TextType.INSTANCE)
                .setRequired(true));

        writer.writeResource(countryForm.asResource());
    }
}
