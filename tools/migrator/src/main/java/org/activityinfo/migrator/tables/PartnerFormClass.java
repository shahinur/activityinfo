package org.activityinfo.migrator.tables;

import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormFieldType;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.shared.CuidAdapter;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.activityinfo.model.shared.CuidAdapter.databaseId;
import static org.activityinfo.model.shared.CuidAdapter.partnerFormClass;

public class PartnerFormClass extends SimpleTableMigrator {

    @Override
    protected String query() {
        return "SELECT * FROM userdatabase";
    }

    @Override
    protected Resource toResource(ResultSet rs) throws SQLException {
        int databaseId = rs.getInt("databaseId");

        FormClass form = new FormClass(partnerFormClass(databaseId))
        .setOwnerId(databaseId(databaseId))
        .setLabel("Partner");

        form.addField(CuidAdapter.NAME_FIELD)
        .setRequired(true)
        .setLabel("Name")
        .setType(FormFieldType.FREE_TEXT);

        form.addField(CuidAdapter.FULL_NAME_FIELD)
        .setLabel("Full Name")
        .setRequired(true)
        .setType(FormFieldType.FREE_TEXT);

        return form.asResource();
    }
}
