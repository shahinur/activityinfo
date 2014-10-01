package org.activityinfo.store.hrd.entity;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.Query;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.system.ApplicationProperties;
import org.activityinfo.store.BadRequestException;

import java.util.List;

public class FolderIndex {

    private final Key workspaceKey;

    public FolderIndex(Key workspaceKey) {
        this.workspaceKey = workspaceKey;
    }

    public static boolean isFolderItem(Resource resource) {
        ResourceId classId = resource.getValue().getClassId();
        if (classId != null) {
            if (ApplicationProperties.isFolderItem(classId)) {
                return true;
            }
        }
        return false;
    }

    public static String getLabelAndAssertNonEmpty(Resource resource) {
        // For the most part we want to be pretty generous about what we'll accept
        // from users because it's better to get their data safely in and mark it as invalid
        // and strand them in the middle of some refugee camp fighting with a form submission,
        // but for some basic classes like folders and forms, we need to enforce basic rules
        ResourceId classId = resource.getValue().getClassId();
        String labelFieldName = ApplicationProperties.getLabelPropertyName(classId);
        String label = resource.getValue().isString(labelFieldName);
        if (Strings.isNullOrEmpty(label)) {
            throw new BadRequestException(String.format("Resources of class %s must have a label property " +
                " with id %s", classId, labelFieldName));
        }
        return label;
    }

    public List<ResourceNode> queryFolderItems(WorkspaceTransaction tx, ResourceId folderId, boolean filterOutDeletedNodes) {
        Query query = new Query(LatestContent.KIND)
        .setAncestor(workspaceKey)
        .addProjection(new PropertyProjection(Content.VERSION_PROPERTY, Long.class))
        .addProjection(new PropertyProjection(Content.LABEL_PROPERTY, String.class))
        .addProjection(new PropertyProjection(Content.CLASS_PROPERTY, String.class))
        .addProjection(new PropertyProjection(Content.DELETED_PROPERTY, Boolean.class))
        .setFilter(new Query.FilterPredicate(Content.OWNER_PROPERTY,
            Query.FilterOperator.EQUAL, folderId.asString()));

        List<ResourceNode> nodes = Lists.newArrayList();
        for (Entity entity : tx.prepare(query).asIterable()) {
            boolean deleted = Content.isDeleted(entity);
            if (filterOutDeletedNodes && deleted) {
                continue;
            }

            ResourceId id = ResourceId.valueOf(entity.getKey().getName());
            ResourceNode node = new ResourceNode(id);
            node.setVersion((Long) entity.getProperty(Content.VERSION_PROPERTY));
            node.setLabel((String) entity.getProperty(Content.LABEL_PROPERTY));
            node.setOwnerId(folderId);
            if(entity.getProperty(Content.CLASS_PROPERTY) instanceof String) {
                node.setClassId(ResourceId.valueOf((String) entity.getProperty(Content.CLASS_PROPERTY)));
            }
            node.setDeleted(deleted);

            nodes.add(node);


        }

        return nodes;
    }

}
