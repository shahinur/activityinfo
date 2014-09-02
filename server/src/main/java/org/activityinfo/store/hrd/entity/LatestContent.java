package org.activityinfo.store.hrd.entity;


import com.google.appengine.api.datastore.*;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Iterators;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;

import java.util.Iterator;

/**
 * An entity within the {@code ResourceGroup} which contains the
 * content (properties) of the latest version of the resource.
 *
 * The {@code OWNER_PROPERTY} is indexed, allowing queries on owned entities
 */
public class LatestContent {

    public static final String KIND = "C";

    public static final String NAME = "C";

    public static final String OWNER_PROPERTY = "o";

    public static final String VERSION_PROPERTY = "v";

    public static final String LABEL_PROPERTY = "L";

    private final Key key;

    public LatestContent(Key rootKey, ResourceId id) {
        this.key = KeyFactory.createKey(rootKey, KIND, id.asString());
    }

    public Resource get(DatastoreService datastore, Transaction tx) throws EntityNotFoundException {
        Entity entity = datastore.get(tx, key);
        return deserializeResource(entity);
    }

    public Resource get(DatastoreService datastore) throws EntityNotFoundException {
        return get(datastore, (Transaction)null);
    }

    private static Resource deserializeResource(Entity entity) {
        Resource resource = Resources.createResource();
        resource.setId(ResourceId.valueOf(entity.getKey().getParent().getName()));
        resource.setVersion((Long)entity.getProperty(VERSION_PROPERTY));
        resource.setOwnerId(ResourceId.valueOf((String) entity.getProperty(OWNER_PROPERTY)));
        Content.readProperties(entity, resource);
        return resource;
    }

    public Entity createEntity(Resource resource) {
        Entity entity = new Entity(key);
        entity.setUnindexedProperty(VERSION_PROPERTY, resource.getVersion());
        entity.setProperty(OWNER_PROPERTY, resource.getOwnerId().asString());

        Optional<String> label = ResourceLabels.getLabel(resource);
        if(label.isPresent()) {
            entity.setProperty(LABEL_PROPERTY, label.get());
        }
        Content.writeProperties(resource, entity);

        return entity;
    }

    public static Iterator<Resource> queryInstances(DatastoreService datastore, ResourceId formClassId) {

        Query.Filter classFilter = new Query.FilterPredicate(OWNER_PROPERTY,
                Query.FilterOperator.EQUAL,
                formClassId.asString());

        Query query = new Query(KIND).setFilter(classFilter);

        Iterator<Entity> entityIterator = datastore.prepare(query).asIterator();

        return Iterators.transform(entityIterator, new Function<Entity, Resource>() {
            @Override
            public Resource apply(Entity input) {
                return deserializeResource(input);
            }
        });
    }

    public static Resource get(DatastoreService datastoreService, ResourceId resourceId) throws EntityNotFoundException {
        return new ResourceGroup(resourceId).getLatestContent(resourceId).get(datastoreService);
    }

    public Key getKey() {
        return key;
    }
}
