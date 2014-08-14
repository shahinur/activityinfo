package org.activityinfo.migrator.tables;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.activityinfo.migrator.ResourceMigrator;
import org.activityinfo.migrator.ResourceWriter;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.ReferenceValue;
import org.activityinfo.model.type.geo.GeoPoint;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.activityinfo.model.legacy.CuidAdapter.*;

public class LocationTable extends ResourceMigrator {


    @Override
    public void getResources(Connection connection, ResourceWriter writer) throws Exception {

        Map<ResourceId, ResourceId> parentMap = queryParents(connection);

        // Select all locations, exclude "dummy" locations
        String sql = "SELECT L.*, LK.AdminEntityId " +
                     "FROM location L " +
                     "LEFT JOIN locationadminlink LK ON (LK.locationid = L.LocationID) " +
                     "WHERE locationtypeid NOT IN " +
                        "(SELECT locationtypeid " +
                                "FROM locationtype " +
                                "WHERE boundadminlevelid IS NOT NULL) " +
                     "ORDER BY L.LocationID";

        int currentLocationId = -1;
        FormInstance location = null;
        Set<ResourceId> adminUnits = Sets.newHashSet();

        try(Statement statement = connection.createStatement()) {
            try(ResultSet rs = statement.executeQuery(sql)) {
                while(rs.next()) {

                    int locationId = rs.getInt("LocationID");
                    if(locationId != currentLocationId) {
                        if(location != null) {
                            location.set(field(location.getClassId(), ADMIN_FIELD), normalize(parentMap, adminUnits));
                            writer.writeResource(location.asResource());
                        }
                        currentLocationId = locationId;
                        location = newLocationFormInstance(rs);
                        adminUnits = Sets.newHashSet();

                        int adminUnitId = rs.getInt("AdminEntityId");
                        if(!rs.wasNull()) {
                            adminUnits.add(resourceId(ADMIN_ENTITY_DOMAIN, adminUnitId));
                        }
                    }
                }
                if(location != null) {
                    location.set(field(location.getClassId(), ADMIN_FIELD), normalize(parentMap, adminUnits));
                    writer.writeResource(location.asResource());
                }
            }
        }

    }

    private ReferenceValue normalize(Map<ResourceId, ResourceId> parentMap, Set<ResourceId> adminUnits) {
        // don't include the parents of adminentities: they are implied
        Set<ResourceId> parents = Sets.newHashSet();
        for(ResourceId adminUnitId : adminUnits) {
            ResourceId parentId = parentMap.get(adminUnitId);
            parents.add(parentId);
        }
        List<ResourceId> references = Lists.newArrayList();
        for(ResourceId adminUnitId : adminUnits) {
            if(!parents.contains(adminUnitId)) {
                references.add(adminUnitId);
            }
        }

        if(references.isEmpty()) {
            return null;
        } else {
            return new ReferenceValue(references);
        }
    }

    private Map<ResourceId, ResourceId> queryParents(Connection connection) throws SQLException {

        String sql = "SELECT * FROM adminentity WHERE deleted = 0";

        Map<ResourceId, ResourceId> parents = Maps.newHashMap();

        try(Statement statement = connection.createStatement()) {
            try(ResultSet rs = statement.executeQuery(sql)) {
                while(rs.next()) {
                    int parentId = rs.getInt("AdminEntityParentId");
                    if(!rs.wasNull()) {
                        ResourceId entityId = resourceId(ADMIN_ENTITY_DOMAIN, rs.getInt("AdminEntityId"));
                        parents.put(entityId, resourceId(ADMIN_ENTITY_DOMAIN, parentId));
                    }
                }
            }
        }
        return parents;
    }

    protected FormInstance newLocationFormInstance(ResultSet rs) throws SQLException {
        ResourceId locationTypeId = resourceId(LOCATION_TYPE_DOMAIN, rs.getInt("LocationTypeId"));
        ResourceId locationId = resourceId(LOCATION_DOMAIN, rs.getInt("LocationId"));

        FormInstance instance = new FormInstance(locationId, locationTypeId);
        instance.set(field(locationTypeId, NAME_FIELD), rs.getString("name"));
        instance.set(field(locationTypeId, GEOMETRY_FIELD), readGeoPoint(rs));
        return instance;
    }

    private GeoPoint readGeoPoint(ResultSet rs) throws SQLException {
        double x = rs.getDouble("X");
        if(rs.wasNull()) {
            return null;
        }
        double y = rs.getDouble("Y");
        if(rs.wasNull()) {
            return null;
        }
        return GeoPoint.fromXY(x, y);
    }
}
