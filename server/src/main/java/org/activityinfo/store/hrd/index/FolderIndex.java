package org.activityinfo.store.hrd.index;

import com.google.appengine.api.datastore.*;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.system.FolderClass;

import java.util.List;

public class FolderIndex {

    public static final String PARENT_KIND = "Folder";

    public static final String ITEM_KIND = "FolderItem";

    public static boolean isFolderItem(Resource resource) {
        return getLabel(resource) != null;
    }

    public static Key folderKey(ResourceId ownerId) {
        return KeyFactory.createKey(PARENT_KIND, ownerId.asString());
    }

    public static Key itemKey(Resource resource) {
        return KeyFactory.createKey(folderKey(resource.getOwnerId()), PARENT_KIND, resource.getId().asString());
    }

    private static String getLabel(Resource resource) {
        String classId = resource.isString("classId");
        if(FormClass.CLASS_ID.asString().equals(classId)) {
            return resource.isString(FormClass.LABEL_FIELD_ID);

        } else if(FolderClass.CLASS_ID.asString().equals(classId)) {
            return resource.isString(FolderClass.LABEL_FIELD_ID.asString());
        }
        return null;
    }

    public static List<Entity> createEntities(Resource resource) {

        List<Entity> indexEntities = Lists.newArrayList();

        String classId = resource.getString("classId");

        if(FolderClass.CLASS_ID.asString().equals(classId)) {
            indexEntities.add(toEntity(folderKey(resource.getId()), resource));
        }

        if(!resource.getOwnerId().equals(Resources.ROOT_ID)) {
            indexEntities.add(toEntity(itemKey(resource), resource));
        }

        return indexEntities;
    }

    public static ResourceNode queryNode(DatastoreService datastoreService, ResourceId ownerId) throws
            EntityNotFoundException {

        ResourceNode root = fromEntity(datastoreService.get(folderKey(ownerId)));

        Query query = new Query(ITEM_KIND, folderKey(ownerId));

        List<Entity> entities = datastoreService.prepare(query).asList(FetchOptions.Builder.withLimit(500));
        root.setChildren(Lists.transform(entities, new Function<Entity, ResourceNode>() {
            @Override
            public ResourceNode apply(Entity input) {
                return fromEntity(input);
            }
        }));
        return root;
    }

    private static Entity toEntity(Key key, Resource resource) {
        Entity item = new Entity(key);
        item.setUnindexedProperty("label", getLabel(resource));
        item.setUnindexedProperty("classId", resource.isString("classId"));
        item.setUnindexedProperty("version", resource.getVersion());
        return item;
    }


    public static ResourceNode fromEntity(Entity input) {
        ResourceNode node = new ResourceNode(ResourceId.valueOf(input.getKey().getName()));
        node.setClassId(ResourceId.valueOf((String)input.getProperty("classId")));
        node.setLabel((String)input.getProperty("label"));
        node.setVersion((Long)input.getProperty("version"));
        return node;
    }
}
