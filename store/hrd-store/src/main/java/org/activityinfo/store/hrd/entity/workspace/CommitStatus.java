package org.activityinfo.store.hrd.entity.workspace;


import com.google.appengine.api.datastore.Entity;
import com.google.common.base.Preconditions;
import org.activityinfo.store.hrd.tx.IsEntity;

/**
 * Stores the committed version of a bulk load
 */
public class CommitStatus implements IsEntity {

    public static final String VERSION = "V";
    public static final String USER_PROPERTY = "U";
    public static final String TIMESTAMP_PROPERTY = "T";


    private final CommitStatusKey key;
    private long commitVersion;
    private long userId;
    private long timestamp;

    public CommitStatus(CommitStatusKey key) {
        this.key = key;
    }

    public CommitStatus(CommitStatusKey key, Entity entity) {
        this.key = key;
        this.commitVersion = (Long)entity.getProperty(VERSION);
        this.userId = (Long)entity.getProperty(USER_PROPERTY);
        this.timestamp = (Long)entity.getProperty(TIMESTAMP_PROPERTY);
    }

    public CommitStatusKey getKey() {
        return key;
    }

    public long getTransactionId() {
        return key.getTransactionId();
    }

    public CommitStatus(WorkspaceEntityGroup workspace, long transactionId) {
        this.key = new CommitStatusKey(workspace, transactionId);
    }

    public long getCommitVersion() {
        return commitVersion;
    }

    public void setCommitVersion(long commitVersion) {
        this.commitVersion = commitVersion;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getCommitTime() {
        return timestamp;
    }

    public void setCommitTime(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public Entity toEntity() {
        Preconditions.checkState(commitVersion != 0, "Commit version has not been set.");
        Entity entity = new Entity(key.unwrap());
        entity.setProperty(VERSION, commitVersion);
        entity.setUnindexedProperty(TIMESTAMP_PROPERTY, timestamp);
        entity.setUnindexedProperty(USER_PROPERTY, userId);
        return entity;
    }
}
