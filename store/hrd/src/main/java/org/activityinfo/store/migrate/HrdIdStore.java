package org.activityinfo.store.migrate;

import com.google.appengine.api.datastore.*;
import com.google.common.base.Optional;
import org.activityinfo.migrator.filter.IdStore;
import org.activityinfo.model.resource.ResourceId;


public class HrdIdStore implements IdStore {

    private static final String KIND = "RevertedId";

    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    @Override
    public Optional<ResourceId> getNewId(ResourceId oldId) {
        try {
            Entity entity = datastore.get(null, key(oldId));
            String newId = (String) entity.getProperty("id");
            return Optional.of(ResourceId.valueOf(newId));

        } catch (EntityNotFoundException e) {
            return Optional.absent();
        }
    }

    @Override
    public void putNewId(ResourceId oldId, ResourceId newId) {
        Entity entity = new Entity(key(oldId));
        entity.setUnindexedProperty("id", newId.asString());
        datastore.put(null, entity);
    }

    private Key key(ResourceId oldId) {
        return KeyFactory.createKey(KIND, oldId.asString());
    }
}
