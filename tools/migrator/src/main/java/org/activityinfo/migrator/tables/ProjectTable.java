package org.activityinfo.migrator.tables;

import com.google.common.collect.Sets;
import org.activityinfo.migrator.ResourceMigrator;
import org.activityinfo.migrator.ResourceWriter;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.system.ApplicationProperties;
import org.activityinfo.model.type.primitive.TextType;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Set;

import static org.activityinfo.model.legacy.CuidAdapter.*;

public class ProjectTable extends ResourceMigrator {


    @Override
    public void getResources(Connection connection, ResourceWriter writer) throws Exception {

        String sql = "SELECT P.* " +
                     "FROM project P " +
                     "WHERE P.dateDeleted is null ";

        Set<Integer> databases = Sets.newHashSet();

        try(Statement statement = connection.createStatement()) {
            try(ResultSet rs = statement.executeQuery(sql)) {

                while(rs.next()) {
                    int databaseId = rs.getInt("DatabaseId");
                    if(!databases.contains(databaseId)) {
                        writer.write(projectForm(databaseId));
                        databases.add(databaseId);
                    }

                    ResourceId id = CuidAdapter.resourceId(PROJECT_DOMAIN, rs.getInt("ProjectId"));
                    ResourceId classId = projectFormClass(databaseId);

                    FormInstance instance = new FormInstance(id, classId);
                    instance.set(field(classId, NAME_FIELD), rs.getString("name"));
                    instance.set(field(classId, FULL_NAME_FIELD), rs.getString("description"));

                    writer.write(instance.asResource());
                }
            }
        }
    }

    private Resource projectForm(int databaseId) {

        ResourceId formClassId = CuidAdapter.projectFormClass(databaseId);
        ResourceId ownerId = CuidAdapter.databaseId(databaseId);

        FormClass form = new FormClass(formClassId);
        form.setOwnerId(ownerId);
        form.setLabel("Projects");
        form.addElement(new FormField(field(formClassId, NAME_FIELD)).setType(TextType.INSTANCE)
            .setLabel("Name")
            .setSuperProperty(ApplicationProperties.LABEL_PROPERTY)
            .setRequired(true));

        form.addElement(new FormField(field(formClassId, FULL_NAME_FIELD))
            .setType(TextType.INSTANCE)
            .setLabel("Description")
            .setSuperProperty(ApplicationProperties.LABEL_PROPERTY)
            .setRequired(true));

        return form.asResource();

    }
}
