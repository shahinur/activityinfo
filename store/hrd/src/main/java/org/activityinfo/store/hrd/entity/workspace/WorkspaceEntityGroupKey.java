package org.activityinfo.store.hrd.entity.workspace;

import org.activityinfo.store.hrd.tx.IsEntity;
import org.activityinfo.store.hrd.tx.IsKey;

/**
 * Marker interface for keys of entities that are stored within the
 * Workspace entity group.
 */
public interface WorkspaceEntityGroupKey<T extends IsEntity> extends IsKey<T> {

    WorkspaceEntityGroup getWorkspace();

}
