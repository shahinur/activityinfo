package org.activityinfo.store.cloudsql;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.resource.ResourceTree;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.service.store.ResourceNotFound;
import org.activityinfo.service.store.ResourceTreeRequest;

import javax.annotation.Nullable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Constructs a {@link org.activityinfo.model.resource.ResourceTree} in response to
 * a {@link org.activityinfo.service.store.ResourceTreeRequest}.
 */
public class ResourceTreeBuilder {

    private static final Logger LOGGER = Logger.getLogger(ResourceTreeBuilder.class.getName());

    private final StoreConnection connection;
    private final StoreCache cache;

    private final ResourceTreeRequest request;

    private final Map<ResourceId, ResourceNode> nodeMap = Maps.newHashMap();

    public ResourceTreeBuilder(StoreConnection connection, StoreCache storeCache, ResourceTreeRequest request) {
        this.connection = connection;
        this.cache = storeCache;
        this.request = request;
    }

    public ResourceTree build() throws SQLException {
        ResourceId rootId = request.getRootId();
        ResourceNode rootNode = getCachedSubTree(rootId);

        if(rootNode == null) {
            rootNode = fetchRootNode(rootId);

            Set<ResourceId> parents = Sets.newHashSet(rootNode.getId());
            while(!parents.isEmpty()) {
                parents = fetchChildren(parents);
            }

            cache.put(rootNode);
        }
        return new ResourceTree(rootNode);
    }


    private ResourceNode fetchRootNode(ResourceId rootId) throws SQLException {

        if(rootId.equals(Resources.ROOT_ID)) {
            return fetchGlobalRoot(rootId);

        } else {
            String query =
                    "select id, class_id, owner_id, label, version, sub_tree_version " +
                    "from resource where id = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, rootId.asString());
                ResultSet resultSet = statement.executeQuery();

                if (!resultSet.next()) {
                    throw new ResourceNotFound(rootId);
                }

                return createNodeAndAddToMap(resultSet);
            }
        }
    }

    private ResourceNode fetchGlobalRoot(ResourceId rootId) throws SQLException {
        long version = fetchGlobalVersion();
        ResourceNode globalRoot = new ResourceNode(Resources.ROOT_ID);
        globalRoot.setVersion(version);
        globalRoot.setSubTreeVersion(version);
        return globalRoot;
    }

    public long getSubTreeVersion(ResourceId id) throws SQLException {

        StoreCache.CacheItem<Long> cacheRegion = cache.subTreeVersion(id);
        Long cachedVersion = cacheRegion.get();

        if(cachedVersion != null) {
            return cachedVersion;

        } else if(id.equals(Resources.ROOT_ID)) {
            return fetchGlobalVersion();

        } else {
            // Otherwise fetch from database
            try (PreparedStatement statement = connection.prepareStatement(
                    "select sub_tree_version from resource where id = ?")) {

                statement.setString(1, id.asString());
                ResultSet resultSet = statement.executeQuery();
                if (!resultSet.next()) {
                    throw new ResourceNotFound(id);
                }

                long subTreeVersion = resultSet.getLong(1);
                cacheRegion.put(subTreeVersion);

                return subTreeVersion;
            }
        }
    }

    private long fetchGlobalVersion() throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "select current_version from global_version")) {

            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                throw new IllegalStateException("global version is missing");
            }
            return resultSet.getInt(1);
        }
    }


    @Nullable
    private ResourceNode getCachedSubTree(ResourceId rootId) throws SQLException {

        // get the current version of the subtree
        long subTreeVersion = getSubTreeVersion(rootId);

        // fetch by key
        return cache.resourceNode(rootId, subTreeVersion).get();
    }


    private Set<ResourceId> fetchChildren(Set<ResourceId> parents) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT id, class_id, owner_id, label, version, sub_tree_version FROM resource WHERE ");
        sql.append(QueryBuilder.where().ownedBy(parents).ofClass(request.getFormClassIds()));

        Set<ResourceId> toFetch = Sets.newHashSet();

        try(Statement statement = connection.createStatement()) {

            try(ResultSet resultSet = statement.executeQuery(sql.toString())) {
                while(resultSet.next()) {

                    ResourceNode child = createNodeAndAddToMap(resultSet);

                    // add this node to the list of nodes for which we need to fetch
                    // children.
                    toFetch.add(child.getId());

                    // Add the child to it's parent's child list
                    nodeMap.get(child.getOwnerId()).getChildren().add(child);
                }
            }
        }
        return toFetch;
    }

    private ResourceNode createNodeAndAddToMap(ResultSet resultSet) throws SQLException {
        ResourceNode node = new ResourceNode(ResourceId.valueOf(resultSet.getString(1)));
        node.setClassId(ResourceId.valueOf(resultSet.getString(2)));
        node.setOwnerId(ResourceId.valueOf(resultSet.getString(3)));
        node.setLabel(resultSet.getString(4));
        node.setVersion(resultSet.getLong(5));
        node.setSubTreeVersion(resultSet.getLong(6));

        nodeMap.put(node.getId(), node);

        return node;
    }

}
