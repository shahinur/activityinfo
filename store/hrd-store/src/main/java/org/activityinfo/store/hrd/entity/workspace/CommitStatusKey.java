package org.activityinfo.store.hrd.entity.workspace;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import org.activityinfo.store.hrd.tx.IsKey;

public class CommitStatusKey implements IsKey<CommitStatus> {
    public static final String KIND = "TX";

    private final Key key;

    public CommitStatusKey(WorkspaceEntityGroup workspace, long transactionId) {
        this.key = KeyFactory.createKey(workspace.getRootKey(), KIND, transactionId);
    }

    public long getTransactionId() {
        return key.getId();
    }

    @Override
    public Key unwrap() {
        return key;
    }

    public String cacheKey() {
        return KeyFactory.keyToString(key);
    }

    @Override
    public CommitStatus wrapEntity(Entity entity) {
        return new CommitStatus(this, entity);
    }
}
