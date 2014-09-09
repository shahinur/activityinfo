package org.activityinfo.store.hrd.entity;

import com.google.appengine.api.datastore.*;
import org.activityinfo.model.auth.AuthenticatedUser;

public interface WorkspaceTransaction extends AutoCloseable {
    Workspace getWorkspace();

    long incrementVersion();

    Entity get(Key key) throws EntityNotFoundException;

    void put(Entity entity);

    void put(Iterable<Entity> entities);

    PreparedQuery prepare(Query projection);

    void commit();

    @Override
    void close();

    AuthenticatedUser getUser();
}
