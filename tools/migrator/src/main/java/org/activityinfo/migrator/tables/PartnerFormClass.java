package org.activityinfo.migrator.tables;

import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormFieldType;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.system.ApplicationProperties;
import org.activityinfo.model.type.primitive.TextType;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.activityinfo.model.legacy.CuidAdapter.*;
import static org.activityinfo.model.legacy.CuidAdapter.databaseId;
import static org.activityinfo.model.legacy.CuidAdapter.partnerFormClass;

public class PartnerFormClass extends SimpleTableMigrator {

    @Override
    protected String query() {
        return "SELECT * FROM userdatabase";
    }

    @Override
    protected Resource toResource(ResultSet rs) throws SQLException {
        int databaseId = rs.getInt("databaseId");

        ResourceId classId = partnerFormClass(databaseId);
        FormClass form = new FormClass(classId)
        .setOwnerId(databaseId(databaseId))
        .setLabel("Partner");

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
