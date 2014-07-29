package org.activityinfo.migrator.tables;

import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;
import com.google.api.client.util.Sets;
import org.activityinfo.model.resource.Reference;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.shared.CuidAdapter;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.migrator.ResourceMigrator;
import org.activityinfo.migrator.resource.Geometry;
import org.activityinfo.model.resource.Resource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.activityinfo.model.shared.CuidAdapter.*;

public class LocationTable extends ResourceMigrator {

    @Override
    public Iterable<Resource> getResources(Connection connection) throws SQLException {

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

        List<Resource> locations = Lists.newArrayList();

        int currentLocationId = -1;
        Resource location = null;
        Set<ResourceId> adminUnits = Sets.newHashSet();

        try(Statement statement = connection.createStatement()) {
            try(ResultSet rs = statement.executeQuery(sql)) {
                while(rs.next()) {

                    int locationId = rs.getInt("LocationID");
                    if(locationId != currentLocationId) {
                        if(location != null) {
                            location.set(CuidAdapter.ADMIN_FIELD, normalize(parentMap, adminUnits));
                            locations.add(location);
                        }
                        currentLocationId = locationId;
                        location = toResource(rs);
                        adminUnits = Sets.newHashSet();

                        ResourceId adminUnit = resourceId(ADMIN_ENTITY_DOMAIN, rs.getInt("AdminEntityId"));
                        if(adminUnit != null) {
                            adminUnits.add(adminUnit);
                        }
                    }
                }
                if(location != null) {
                    location.set(CuidAdapter.ADMIN_FIELD, normalize(parentMap, adminUnits));
                    locations.add(location);
                }
            }
        }

        return locations;
    }

    private ArrayList<Reference> normalize(Map<ResourceId, ResourceId> parentMap, Set<ResourceId> adminUnits) {
        // don't include the parents of adminentities: they are implied
        Set<ResourceId> parents = Sets.newHashSet();
        for(ResourceId adminUnitId : adminUnits) {
            ResourceId parentId = parentMap.get(adminUnitId);
            parents.add(parentId);
        }
        List<Reference> references = Lists.newArrayList();
        for(ResourceId adminUnitId : adminUnits) {
            if(!parents.contains(adminUnitId)) {
                references.add(Reference.to(adminUnitId));
            }
        }

        if(references.isEmpty()) {
            return null;
        } else {
            return Lists.newArrayList(references);
        }
    }

    private Map<ResourceId, ResourceId> queryParents(Connection connection) throws SQLException {

        String sql = "SELECT * FROM adminentity";

        Map<ResourceId, ResourceId> parents = Maps.newHashMap();

        try(Statement statement = connection.createStatement()) {
            try(ResultSet rs = statement.executeQuery(sql)) {
                while(rs.next()) {
                    ResourceId entityId = resourceId(ADMIN_ENTITY_DOMAIN, rs.getInt("AdminEntityId"));
                    ResourceId parentId = resourceId(ADMIN_ENTITY_DOMAIN, rs.getInt("AdminEntityParentId"));

                    if(parentId != null) {
                        assert !rs.wasNull();

                        parents.put(entityId, parentId);
                    }
                }
            }
        }
        return parents;
    }

    protected Resource toResource(ResultSet rs) throws SQLException {
        ResourceId locationTypeId = resourceId(LOCATION_TYPE_DOMAIN, rs.getInt("LocationTypeId"));

        return Resources.createResource()
        .setId(resourceId(LOCATION_DOMAIN, rs.getInt("LocationId")))
        .setOwnerId(locationTypeId)
        .set("class", locationTypeId)
        .set(NAME_FIELD, rs.getString("name"))
        .set(GEOMETRY_FIELD, Geometry.point(rs));
    }
}
