package org.activityinfo.store.hrd.entity;

import com.google.appengine.api.datastore.*;
import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.service.store.ResourceNotFound;

import java.util.Iterator;

import static org.activityinfo.store.hrd.entity.Content.*;

/**
 * An entity within the {@code ResourceGroup} which contains the
 * content (properties) of the latest version of the resource.
 *
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
        return deserializeResource(entity);
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

    /**
     * Updates the LatestContent entity to reflect the content of {@code resource}
     * @param tx the transaction in which this change is to be effected
     * @param resource the latest version of the resource
     */
    public void create(WorkspaceTransaction tx, Resource resource) {
        Entity entity = new Entity(key);
        entity.setProperty(VERSION_PROPERTY, resource.getVersion());
        entity.setProperty(OWNER_PROPERTY, resource.getOwnerId().asString());
        entity.setProperty(CLASS_PROPERTY, resource.isString("classId"));
        Content.writeProperties(resource, entity);

        if(FolderIndex.isFolderItem(resource)) {
            entity.setProperty(LABEL_PROPERTY, FolderIndex.getLabelAndAssertNonEmpty(resource));
        }

        if(FormMetadata.isFormInstance(resource)) {
            FormMetadata metadata = new FormMetadata(rootKey, resource);
            long rowIndex = metadata.addInstance(tx, resource.getVersion());

            entity.setProperty(ROW_INDEX_PROPERTY, rowIndex);
        }

        tx.put(entity);
    }


    public void update(WorkspaceTransaction tx, Resource resource) {
        Entity entity;
        try {
            entity = tx.get(key);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFound(resource.getId());
        }
        entity.setProperty(VERSION_PROPERTY, resource.getVersion());
        entity.setProperty(OWNER_PROPERTY, resource.getOwnerId().asString());
        entity.setProperty(CLASS_PROPERTY, resource.isString("classId"));
        Content.writeProperties(resource, entity);

        if(FolderIndex.isFolderItem(resource)) {
            entity.setProperty(LABEL_PROPERTY, FolderIndex.getLabelAndAssertNonEmpty(resource));
        }

        if(FormMetadata.isFormInstance(resource)) {
            FormMetadata metadata = new FormMetadata(rootKey, resource);
            metadata.updateLatestVersion(tx, resource.getVersion());
        }
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
