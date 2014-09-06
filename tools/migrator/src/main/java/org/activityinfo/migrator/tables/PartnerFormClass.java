package org.activityinfo.migrator.tables;

import org.activityinfo.migrator.filter.MigrationContext;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.system.ApplicationProperties;
import org.activityinfo.model.type.primitive.TextType;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.activityinfo.model.legacy.CuidAdapter.*;

public class PartnerFormClass extends SimpleTableMigrator {

    public PartnerFormClass(MigrationContext context) {
        super(context);
    }

    @Override
    protected String query() {
        return "SELECT * FROM userdatabase WHERE " + filter.databaseFilter();
    }

    @Override
    protected Resource toResource(ResultSet rs) throws SQLException {
        int databaseId = rs.getInt("databaseId");

        ResourceId classId = context.resourceId(PARTNER_FORM_CLASS_DOMAIN, databaseId);
        FormClass form = new FormClass(classId)
        .setOwnerId(context.resourceId(DATABASE_DOMAIN, databaseId))
        .setLabel("Partners");

        form.addField(field(classId, NAME_FIELD))
            .setRequired(true)
            .setLabel("Name")
            .setSuperProperty(ApplicationProperties.LABEL_PROPERTY)
            .setType(TextType.INSTANCE);

        form.addField(field(classId, FULL_NAME_FIELD))
            .setLabel("Full Name")
            .setSuperProperty(ApplicationProperties.DESCRIPTION_PROPERTY)
            .setRequired(true)
            .setType(TextType.INSTANCE);

        return form.asResource();
    }
}
