package org.activityinfo.store.hrd.entity;

import com.google.appengine.api.datastore.*;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.service.store.ResourceNotFound;
import org.activityinfo.store.EntityDeletedException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.Iterator;

import static org.activityinfo.store.hrd.entity.Content.*;

/**
 * An entity within the {@code ResourceGroup} which contains the
 * content (properties) of the latest version of the resource.
 * <p/>
 * The {@code OWNER_PROPERTY} is indexed, allowing queries on owned entities
 */
public class LatestContent {

    public static final String KIND = "C";

    /**
     * We need an index to match the ResourceId to its workspace
     */
    public static final String RESOURCE_ID_PROPERTY = "ID";

    public static final String ROW_INDEX_PROPERTY = "R";


    private final Key rootKey;
    private final ResourceId resourceId;
    private final Key key;

    public LatestContent(Key rootKey, ResourceId id) {
        this.rootKey = rootKey;
        this.resourceId = id;
        this.key = KeyFactory.createKey(rootKey, KIND, id.asString());
    }

    public Resource get(WorkspaceTransaction tx) throws EntityNotFoundException {
        Entity entity = tx.get(key);
        assertDeleted(entity);
        assertAncestorNotDeleted(tx, entity);
        return deserializeResource(entity);
    }

    private boolean isDeleted(Entity entity) {
        return entity.isUnindexedProperty(VERSION_PROPERTY);
    }

    private void assertDeleted(Entity entity) {
        if (isDeleted(entity)) {
            throw new WebApplicationException(Response.Status.GONE);
        }
    }

    private void assertAncestorNotDeleted(WorkspaceTransaction tx, Entity entity) throws EntityDeletedException {
        try {
            if (entity.getProperty(OWNER_PROPERTY) instanceof String) {
                String ownerId = (String) entity.getProperty(OWNER_PROPERTY);
                boolean isRoot = Resources.ROOT_ID.asString().equals(ownerId);
                Key ownerKey = KeyFactory.createKey(rootKey, KIND, isRoot ? rootKey.getName() : ownerId);
                Entity ownerEntity = tx.get(ownerKey);
                assertDeleted(ownerEntity);
                if (!isRoot) {
                    assertAncestorNotDeleted(tx, ownerEntity);
                }
            } else {
                // if owner is blank then we assume that it's root, otherwise it must not be empty
                return;
            }
        } catch (EntityNotFoundException e) {
            throw new EntityDeletedException();
        }
    }

    public Resource get(DatastoreService datastore) throws EntityNotFoundException {
        Entity entity = datastore.get(key);
        return deserializeResource(entity);
    }

    public Resource get(DatastoreService datastore, Transaction tx) throws EntityNotFoundException {
        Entity entity = datastore.get(tx, key);
        return deserializeResource(entity);
    }

    public ResourceNode getAsNode(WorkspaceTransaction tx) throws EntityNotFoundException {
        Entity entity = tx.get(key);
        return deserializeResourceNode(entity);
    }

    public ResourceNode getAsNode(DatastoreService datastore) throws EntityNotFoundException {
        Entity entity = datastore.get(key);
        return deserializeResourceNode(entity);
    }

    public Iterable<ResourceId> getChildIds(WorkspaceTransaction tx) throws EntityNotFoundException {
        return Iterables.transform(getAsNode(tx).getChildren(), new Function<ResourceNode, ResourceId>() {
            @Override
            public ResourceId apply(ResourceNode resourceNode) {
                return resourceNode.getId();
            }
        });
    }

    /**
     * Updates the LatestContent entity to reflect the content of {@code resource}
     *
     * @param tx       the transaction in which this change is to be effected
     * @param resource the latest version of the resource
     * @param rowIndex
     */
    public void create(WorkspaceTransaction tx, Resource resource, Optional<Long> rowIndex) {
        Entity entity = new Entity(key);
        entity.setProperty(VERSION_PROPERTY, resource.getVersion());
        entity.setProperty(OWNER_PROPERTY, resource.getOwnerId().asString());
        entity.setProperty(CLASS_PROPERTY, resource.getValue().getClassId().asString());
        entity.setProperty(RESOURCE_ID_PROPERTY, resource.getId().asString());
        Content.writeProperties(resource, entity);

        if (FolderIndex.isFolderItem(resource)) {
            entity.setProperty(LABEL_PROPERTY, FolderIndex.getLabelAndAssertNonEmpty(resource));
        }

        if (FormMetadata.isFormInstance(resource)) {
            if (rowIndex.isPresent()) {
                entity.setProperty(ROW_INDEX_PROPERTY, rowIndex.get());
            } else {
                FormMetadata metadata = new FormMetadata(rootKey, resource);
                long nextRowIndex = metadata.addInstance(tx, resource.getVersion());

                entity.setProperty(ROW_INDEX_PROPERTY, nextRowIndex);
            }
        }

        tx.put(entity);
    }


    public void update(WorkspaceTransaction tx, Resource resource) {
        Entity entity;
        try {
            entity = tx.get(key);

            assertAncestorNotDeleted(tx, entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFound(resource.getId());
        }

        entity.setProperty(VERSION_PROPERTY, resource.getVersion());
        entity.setProperty(OWNER_PROPERTY, resource.getOwnerId().asString());
        entity.setProperty(CLASS_PROPERTY, resource.getValue().getClassId().asString());
        Content.writeProperties(resource, entity);

        if (FolderIndex.isFolderItem(resource)) {
            entity.setProperty(LABEL_PROPERTY, FolderIndex.getLabelAndAssertNonEmpty(resource));
        }

        if (FormMetadata.isFormInstance(resource)) {
            FormMetadata metadata = new FormMetadata(rootKey, resource);
            metadata.updateLatestVersion(tx, resource.getVersion());
        }

        tx.put(entity);
    }

    public void delete(WorkspaceTransaction tx) throws EntityNotFoundException {
        Entity entity = tx.get(key);
        unindexProperty(entity, VERSION_PROPERTY);
        unindexProperty(entity, OWNER_PROPERTY);
        unindexProperty(entity, CLASS_PROPERTY);
        unindexProperty(entity, CONTENTS_PROPERTY);
        unindexProperty(entity, LABEL_PROPERTY);

        tx.put(entity);
    }

    private void unindexProperty(Entity entity, String propertyName) {
        entity.setUnindexedProperty(propertyName, entity.getProperty(propertyName));
    }


    /**
     * Returns a list of resources, ordered by their row index to ensure
     * that row order is stable over time.
     */
    public Iterator<Resource> queryFormInstances(WorkspaceTransaction tx) {

        Query.Filter classFilter = new Query.FilterPredicate(OWNER_PROPERTY,
                Query.FilterOperator.EQUAL,
                resourceId.asString());

        Query query = new Query(KIND)
                .setFilter(classFilter)
                .addSort(ROW_INDEX_PROPERTY)
                .setAncestor(rootKey);

        Iterator<Entity> entityIterator = tx.prepare(query).asIterator();

        return Iterators.transform(entityIterator, new Function<Entity, Resource>() {
            @Override
            public Resource apply(Entity input) {
                return deserializeResource(input);
            }
        });
    }

    public Key getKey() {
        return key;
    }

    public static ResourceId workspaceFromKey(Key key) {
        return ResourceId.valueOf(key.getParent().getName());
    }

}
