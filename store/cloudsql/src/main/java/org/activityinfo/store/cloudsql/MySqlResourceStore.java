package org.activityinfo.store.cloudsql;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.*;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.service.store.ResourceNotFound;
import org.activityinfo.service.store.ResourceStore;
import org.activityinfo.service.store.ResourceTreeRequest;
import org.activityinfo.service.store.UpdateResult;
import org.activityinfo.service.tables.TableBuilder;

import javax.inject.Provider;
import java.sql.*;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.activityinfo.store.cloudsql.QueryBuilder.where;

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

    private MySqlStoreAccessor openAccessor(AuthenticatedUser user) {
        return new MySqlStoreAccessor(open(), user);
    }

    @Override
    public Resource get(AuthenticatedUser user, final ResourceId resourceId) {
        Set<Resource> resource = get(user, Collections.singleton(resourceId));
        if(resource.isEmpty()) {
            throw new ResourceNotFound(resourceId);
        }
        return resource.iterator().next();
    }

    @Override
    public Set<Resource> get(AuthenticatedUser user, Set<ResourceId> resourceIds) {

        String sql = "SELECT content, version FROM resource WHERE " +
                 where().withId(resourceIds);

        Set<Resource> resources = Sets.newHashSet();
        try(StoreConnection connection = open();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                String json = resultSet.getString(1);
                Resource resource = Resources.fromJson(json);
                resource.setVersion(resultSet.getLong(2));
                resources.add(resource);
            }
            return resources;
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResourceTree queryTree(AuthenticatedUser user, ResourceTreeRequest request) {
        try(StoreConnection connection = open()) {
            ResourceTreeBuilder builder = new ResourceTreeBuilder(connection, cache, request);
            return builder.build();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TableData queryTable(AuthenticatedUser user, TableModel tableModel) {

        try(MySqlStoreAccessor accessor = openAccessor(user)) {
            TableBuilder builder = new TableBuilder(accessor);
            return builder.buildTable(tableModel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UpdateResult put(AuthenticatedUser user, Resource resource) {
        return put(user, resource.getId(), resource);
    }

    @Override
    public UpdateResult put(AuthenticatedUser user, ResourceId id, Resource resource) {

        Preconditions.checkArgument(id.equals(resource.getId()), "id == resource.getId()");

        try(StoreConnection connection = open()) {
            try(StoreWriter writer = new StoreWriter(connection, cache)) {

                return writer
                        .put(resource)
                        .byUser(user.getUserResourceId())
                        .execute();

            } catch (GlobalLockTimeoutException e) {
                throw new UnsupportedOperationException();

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ResourceNode> getUserRootResources(AuthenticatedUser userId) {
        try(
            StoreConnection connection = open();
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT id, class_id, owner_id, label, version, sub_tree_version FROM resource " +
                    "WHERE id IN (SELECT resource_id FROM user_root_index WHERE user_id = ?)")) {

            statement.setString(1, userId.getUserResourceId().asString());
            try (ResultSet resultSet = statement.executeQuery()) {
                List<ResourceNode> roots = Lists.newArrayList();
                while(resultSet.next()) {
                    ResourceId id = ResourceId.valueOf(resultSet.getString(1));
                    ResourceId classId = ResourceId.valueOf(resultSet.getString(2));

                    ResourceNode node = new ResourceNode(id, classId);
                    node.setOwnerId(ResourceId.valueOf(resultSet.getString(3)));
                    node.setLabel(resultSet.getString(4));
                    node.setVersion(resultSet.getLong(5));
                    node.setSubTreeVersion(resultSet.getLong(6));
                    node.setParent(true);
                    roots.add(node);
                }
                return roots;
            }
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
