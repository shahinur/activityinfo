package org.activityinfo.migrator.tables;

import com.google.common.base.Preconditions;
import org.activityinfo.migrator.ResourceMigrator;
import org.activityinfo.migrator.ResourceWriter;
import org.activityinfo.migrator.filter.MigrationContext;
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

    private final MigrationContext context;

    public Geodatabase(MigrationContext context) {
        this.context = context;
    }

    @Override
    public void getResources(Connection connection, ResourceWriter writer) throws Exception {
        writeGeodatabase(writer);
        writeCountryForm(writer);
    }

    private void writeGeodatabase(ResourceWriter writer) throws Exception {
        Resource resource = Resources.createResource();
        resource.setId(context.getIdStrategy().geoDbId());
        resource.set("classId", "_folder");
        resource.setOwnerId(context.getGeoDbOwnerId());
        resource.set(FolderClass.LABEL_FIELD_ID.asString(), "Geographic Reference");

        Preconditions.checkNotNull(resource.getId());

        writer.writeResource(resource, null, null);
    }

    private void writeCountryForm(ResourceWriter writer) throws Exception {
        ResourceId formClassId = context.getIdStrategy().countryFormClassId();
        FormClass countryForm = new FormClass(formClassId);
        countryForm.setOwnerId(context.getIdStrategy().geoDbId());
        countryForm.setLabel("Country");
        countryForm.addElement(
            new FormField(field(formClassId, NAME_FIELD))
                .setLabel("Name")
                .setType(TextType.INSTANCE)
                .setRequired(true));
        countryForm.addElement(
            new FormField(field(formClassId, CODE_FIELD))
                .setLabel("Code")
                .setDescription("ISO 3166-1 alpha-2 code")
                .setType(TextType.INSTANCE)
                .setRequired(true));

        writer.writeResource(countryForm.asResource(), null, null);
    }
}
