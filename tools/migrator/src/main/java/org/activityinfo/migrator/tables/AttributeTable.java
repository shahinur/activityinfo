package org.activityinfo.migrator.tables;

import com.google.common.base.Preconditions;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.activityinfo.model.shared.CuidAdapter.*;

public class AttributeTable extends SimpleTableMigrator {

    public static final String key(int activityId, int groupId, int attributeId) {
        return "Attribute:" + activityId + ":" + groupId + ":" + attributeId;
    }

    @Override
    protected Resource toResource(ResultSet rs) throws SQLException {
        ResourceId groupId = resourceId(ATTRIBUTE_GROUP_DOMAIN, rs.getInt("attributegroupId"));

        return Resources.createResource()
        .setId(resourceId(ATTRIBUTE_DOMAIN, rs.getInt("attributeId")))
        .setOwnerId(groupId)
        .set(CLASS_FIELD, groupId)
        .set(NAME_FIELD, Preconditions.checkNotNull(rs.getString("name")));
    }
}
