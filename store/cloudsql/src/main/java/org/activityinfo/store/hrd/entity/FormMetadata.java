package org.activityinfo.store.hrd.entity;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.common.base.Preconditions;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;

/**
 * We add additional metadata and indices for instances of
 * users FormClass's so that we can efficiently execute table-like queries
 */
public class FormMetadata {

    public static final String KIND = "F";

    private static final String COUNT_PROPERTY = "count";
    private static final String VERSION_PROPERTY = "version";

    private final Key workspaceKey;
    private final ResourceId formClassId;
    private final Key key;


    public FormMetadata(Key workspaceKey, ResourceId formClassId) {
        Preconditions.checkNotNull(formClassId, "formClassId");
        this.workspaceKey = workspaceKey;
        this.formClassId = formClassId;
        this.key = KeyFactory.createKey(workspaceKey, KIND, this.formClassId.asString());
    }

    public FormMetadata(Key workspaceKey, Resource resource) {
        this(workspaceKey, formClassOf(resource));
    }

    public static boolean isFormInstance(Resource resource) {
        return formClassOf(resource) != null;
    }

    private static ResourceId formClassOf(Resource resource) {
        String classId = resource.isString("classId");
        if(classId == null) {
            return null;
        }
        if(classId.equals(FormClass.CLASS_ID.asString())) {
            return resource.getId();
        }
        if(!classId.startsWith("_")) {
            return ResourceId.valueOf(classId);
        }
        return null;
    }

    /**
     *
     * @return the latest version of any of this FormClass's instances
     */
    public long getLatestVersion(WorkspaceTransaction tx) {
        Entity entity = getOrCreateEntity(tx);
        return (Long)entity.getProperty(VERSION_PROPERTY);
    }

    /**
     * Updates the FormClass's latest version number
     */
    public void updateLatestVersion(WorkspaceTransaction tx, long latestVersion) {
        Entity entity = getOrCreateEntity(tx);
        entity.setUnindexedProperty(VERSION_PROPERTY, latestVersion);
        tx.put(entity);
    }


    /**
     *
     * Increments this form's row count and updates the latest version of this
     * FormInstance to the version of the new instance.
     *
     * @return the row index of the resource
     */
    public long addInstance(WorkspaceTransaction tx, long instanceVersion) {
        Entity entity = getOrCreateEntity(tx);
        long count = (long) entity.getProperty(COUNT_PROPERTY);

        entity.setProperty(COUNT_PROPERTY, count+1);
        entity.setProperty(VERSION_PROPERTY, instanceVersion);
        tx.put(entity);

        return count+1;
    }


    private Entity getOrCreateEntity(WorkspaceTransaction tx)  {
        try {
            return tx.get(key);
        } catch (EntityNotFoundException e) {
            Entity entity = new Entity(key);
            entity.setProperty(VERSION_PROPERTY, 0L);
            entity.setProperty(COUNT_PROPERTY, 0L);
            return entity;
        }
    }

}
