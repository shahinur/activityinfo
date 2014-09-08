package org.activityinfo.store.hrd.entity;

import com.google.appengine.api.datastore.*;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.system.FolderClass;
import org.activityinfo.store.cloudsql.BadRequestException;

import java.util.List;

public class FolderIndex {

    private final Key workspaceKey;

    public FolderIndex(Key workspaceKey) {
        this.workspaceKey = workspaceKey;
    }

    public static boolean isFolderItem(Resource resource) {
        String classId = resource.isString("classId");
        if (classId != null) {
            ResourceId id = ResourceId.valueOf(classId);
            if (id.equals(FormClass.CLASS_ID) ||
                id.equals(FolderClass.CLASS_ID)) {

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
        String classId = resource.getString("classId");
        String labelFieldName = getLabelPropertyName(classId);
        String label = resource.isString(labelFieldName);
        if (Strings.isNullOrEmpty(label)) {
            throw new BadRequestException(String.format("Resources of class %s must have a label property " +
                " with id %s", classId, labelFieldName));
        }
        return label;
    }

    public List<ResourceNode> queryFolderItems(WorkspaceTransaction tx, ResourceId folderId) {
        Query query = new Query(LatestContent.KIND)
        .setAncestor(workspaceKey)
        .addProjection(new PropertyProjection(Content.VERSION_PROPERTY, Long.class))
        .addProjection(new PropertyProjection(Content.LABEL_PROPERTY, String.class))
        .addProjection(new PropertyProjection(Content.CLASS_PROPERTY, String.class))
        .setFilter(new Query.FilterPredicate(Content.OWNER_PROPERTY,
            Query.FilterOperator.EQUAL, folderId.asString()));

        List<ResourceNode> nodes = Lists.newArrayList();
        for (Entity entity : tx.prepare(query).asIterable()) {
            nodes.add(Content.deserializeResourceNode(entity));
        }

        return nodes;
    }

    private static String getLabelPropertyName(String classId) {
        if (FormClass.CLASS_ID.asString().equals(classId)) {
            return FormClass.LABEL_FIELD_ID;

        } else if (FolderClass.CLASS_ID.asString().equals(classId)) {
            return FolderClass.LABEL_FIELD_ID.asString();
        }
        return null;
    }
}
