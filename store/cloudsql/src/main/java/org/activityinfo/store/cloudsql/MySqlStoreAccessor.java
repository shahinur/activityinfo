package org.activityinfo.store.cloudsql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.json.ObjectMapperFactory;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.service.store.ResourceCursor;
import org.activityinfo.service.store.StoreAccessor;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

public class MySqlStoreAccessor implements StoreAccessor {

    private final StoreConnection connection;
    private final AuthenticatedUser user;

    private final ObjectMapper objectMapper = ObjectMapperFactory.get();

    public MySqlStoreAccessor(StoreConnection connection, AuthenticatedUser user) {
        this.connection = connection;
        this.user = user;
    }

    @Override
    public ResourceCursor openCursor(ResourceId formClassId) {

        ResultSet resultSet;
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT version, content FROM resource WHERE class_id = ? order by sequence");
            statement.setString(1, formClassId.asString());
            statement.setFetchDirection(ResultSet.FETCH_FORWARD);
            resultSet = statement.executeQuery();

            return new MySqlResourceCursor(resultSet);

        } catch(Exception e) {
            connection.close(true);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Resource get(ResourceId id) throws SQLException, IOException {

        Set<Resource> resources = Sets.newHashSet();
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT content, version FROM resource WHERE id = ?")) {

            statement.setString(1, id.asString());

            try (ResultSet rs = statement.executeQuery()) {

                if (!rs.next()) {
                    throw new IllegalArgumentException("not found: id = " + id);
                }

                String json = rs.getString(1);
                Resource resource = objectMapper.readValue(json, Resource.class);
                resource.setVersion(rs.getLong(2));
                resources.add(resource);
                return resource;
            }
        }
    }

    @Override
    public void close() {
        connection.close();
    }
}
