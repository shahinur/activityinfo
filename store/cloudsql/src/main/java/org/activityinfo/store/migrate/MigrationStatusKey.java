package org.activityinfo.store.migrate;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import org.activityinfo.store.hrd.entity.workspace.WorkspaceEntityGroup;
import org.activityinfo.store.hrd.tx.IsKey;

public class MigrationStatusKey implements IsKey<MigrationStatus> {

    public static final String KIND = "MigratedVersion";
    public static final String KEY_NAME = "Version";

    private final Key key;

    public MigrationStatusKey(WorkspaceEntityGroup workspace) {
        this.key = KeyFactory.createKey(workspace.getRootKey(), KIND, KEY_NAME);
    }

    @Override
    public Key unwrap() {
        return key;
    }

    @Override
    public MigrationStatus wrapEntity(Entity entity) {
        return new MigrationStatus(this, entity);
    }
}
