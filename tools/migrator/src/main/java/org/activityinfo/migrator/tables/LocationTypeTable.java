package org.activityinfo.migrator.tables;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormFieldCardinality;
import org.activityinfo.model.form.FormFieldType;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.migrator.ResourceMigrator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.activityinfo.model.shared.CuidAdapter.*;

public class LocationTypeTable extends ResourceMigrator {


    @Override
    public Iterable<Resource> getResources(Connection connection) throws SQLException {

        Multimap<Integer, ResourceId> adminLevelsByCountry = queryLevels(connection);

        List<Resource> resources = Lists.newArrayList();

        // only select "real" location types - we will discard dummy location types
        // as part of the migration
        String sql = "SELECT * FROM locationtype WHERE boundadminlevelid IS NULL";

        try(Statement statement = connection.createStatement()) {
            try(ResultSet rs = statement.executeQuery(sql)) {
                while(rs.next()) {

                    ResourceId id = resourceId(LOCATION_TYPE_DOMAIN, rs.getInt("locationTypeId"));
                    int countryId = rs.getInt("countryId");
                    Preconditions.checkState(!rs.wasNull());

                    FormClass formClass = new FormClass(id)
                            .setOwnerId(owner(rs))
                            .setLabel(rs.getString("name"));

                    formClass.addField(NAME_FIELD)
                            .setLabel("Name")
                            .setRequired(true)
                            .setType(FormFieldType.FREE_TEXT);

                    formClass.addField(AXE_FIELD)
                            .setLabel("Secondary Name")
                            .setRequired(false)
                            .setType(FormFieldType.FREE_TEXT);

                    if(adminLevelsByCountry.containsKey(countryId)) {
                        formClass.addField(ADMIN_FIELD)
                                .setLabel("Administrative Unit")
                                .setRequired(false)
                                .setType(FormFieldType.REFERENCE)
                                .setRange(Sets.newHashSet(adminLevelsByCountry.get(countryId)))
                                .setCardinality(FormFieldCardinality.MULTIPLE);
                    }

                    formClass.addField(GEOMETRY_FIELD)
                            .setLabel("Geographic Position")
                            .setRequired(false)
                            .setType(FormFieldType.GEOGRAPHIC_POINT);

                    resources.add(formClass.asResource());
                }
            }
        }

        return resources;
    }

    private ResourceId owner(ResultSet rs) throws SQLException {
        int databaseId = rs.getInt("databaseid");
        if(rs.wasNull()) {
            return resourceId(COUNTRY_DOMAIN, rs.getInt("CountryId"));
        } else {
            return resourceId(DATABASE_DOMAIN, databaseId);
        }
    }

    private Multimap<Integer, ResourceId> queryLevels(Connection connection) throws SQLException {

        Multimap<Integer, ResourceId> map = HashMultimap.create();

        String sql = "SELECT * FROM adminlevel";

        try(Statement statement = connection.createStatement()) {
            try(ResultSet rs = statement.executeQuery(sql)) {
                while(rs.next()) {
                    int countryId = rs.getInt("CountryId");
                    ResourceId levelId = resourceId(ADMIN_LEVEL_DOMAIN, rs.getInt("AdminLevelId"));

                    map.put(countryId, levelId);
                }
            }
        }

        return map;
    }

}
