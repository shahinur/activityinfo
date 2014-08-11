package org.activityinfo.store.cloudsql;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.service.store.*;

import javax.inject.Provider;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Singleton
public class MySqlResourceStore implements ResourceStore {

    private final Provider<Connection> connectionProvider;
    private final StoreCache cache;

    @Inject
    public MySqlResourceStore(Provider<Connection> connectionProvider, MemcacheService memcacheService) {
        this.connectionProvider = connectionProvider;
        this.cache = new StoreCache(memcacheService);
    }

    private StoreConnection open() {
        return new StoreConnection(connectionProvider);
    }

    @Override
    public Resource get(final ResourceId resourceId) {

        try(StoreConnection connection = open()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT content, version FROM resource WHERE id = ?");
            statement.setString(1, resourceId.asString());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String json = resultSet.getString(1);
                Resource resource = Resources.fromJson(json);
                resource.setVersion(resultSet.getLong(2));
                return resource;
            } else {
                throw new ResourceNotFound(resourceId);
            }
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResourceCursor openCursor(ResourceId formClassId) {
        StoreConnection connection = open();
        ResultSet resultSet;
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT version, content FROM resource WHERE class_id = ? order by sequence");
            statement.setString(1, formClassId.asString());
            statement.setFetchDirection(ResultSet.FETCH_FORWARD);
            resultSet = statement.executeQuery();

            return new MySqlResourceCursor(connection, resultSet);

        } catch(Exception e) {
            connection.close(true);
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResourceTree queryTree(ResourceTreeRequest request) {
        try(StoreConnection connection = open()) {
            ResourceTreeBuilder builder = new ResourceTreeBuilder(connection, cache, request);
            return builder.build();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UpdateResult createResource(ResourceId userId, Resource resource) {
        try(StoreConnection connection = open()) {
            try(StoreWriter writer = new StoreWriter(connection, cache)) {

                return writer
                        .create(resource)
                        .byUser(userId)
                        .execute();

            } catch (GlobalLockTimeoutException e) {
                throw new UnsupportedOperationException();

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UpdateResult updateResource(ResourceId userId, Resource resource) {
        return null;
    }
}
