package org.activityinfo.service.store;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.*;
import org.activityinfo.model.system.FolderClass;
import org.hibernate.Session;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class TreeBuilder {

    private Session session;

    private Map<ResourceId, ResourceNode> nodeMap = Maps.newHashMap();

    private Set<ResourceId> classes = Sets.newHashSet(FormClass.CLASS_ID, FolderClass.CLASS_ID);

    public TreeBuilder(Session session) {
        this.session = session;
    }

    public ResourceTree build(ResourceId rootId) {
        String json = (String)this.session.createSQLQuery("select json from resource where id = ?")
                .setString(0, rootId.asString())
                .uniqueResult();

        Resource resource = Resources.fromJson(json);
        ResourceNode rootNode = toNode(resource);

        Set<ResourceId> parents = Sets.newHashSet(rootNode.getId());

        while(!parents.isEmpty()) {
            parents = fetchChildren(parents);
        }

        return new ResourceTree(rootNode);
    }

    private Set<ResourceId> fetchChildren(Set<ResourceId> parents) {

        List list = session.createSQLQuery(childQuery(parents)).list();

        Set<ResourceId> childIds = Sets.newHashSet();

        for(Object jsonEncodedResource : list) {
            Resource child = Resources.fromJson((String)jsonEncodedResource);
            childIds.add(child.getId());

            // create the new child node
            ResourceNode childNode = toNode(child);

            // Add the child to it's parent's child list
            nodeMap.get(child.getOwnerId()).getChildren().add(childNode);
        }

        return childIds;
    }

    private String childQuery(Set<ResourceId> parents) {
        StringBuilder sql = new StringBuilder();
        sql.append("select json from resource where ");
        appendCriteria(sql, "ownerId", parents);
        sql.append(" AND ");
        appendCriteria(sql, "classId", classes);
        return sql.toString();
    }

    private void appendCriteria(StringBuilder sql, String columnName, Set<ResourceId> ids) {
        sql.append(columnName);
        if(ids.size() == 1) {
            sql.append(" = '").append(Iterables.getOnlyElement(ids).asString()).append("'");
        } else {
            sql.append(" in (");
            boolean needsComma = false;
            for(ResourceId id : ids) {
                if(needsComma) {
                    sql.append(", ");
                }
                sql.append("'").append(id.asString()).append("'");
                needsComma = true;
            }
            sql.append(')');
        }
    }

    private ResourceNode toNode(Resource resource) {
        ResourceId classId = getClassId(resource);
        ResourceNode node = new ResourceNode(resource.getId(), classId);
        node.setLabel(getLabel(classId, resource));

        nodeMap.put(node.getId(), node);

        return node;
    }

    private ResourceId getClassId(Resource resource) {
        if(resource.has("classId")) {
            return ResourceId.create(resource.getString("classId"));
        } else {
            return null;
        }
    }

    private String getLabel(ResourceId classId, Resource resource) {
        if(FormClass.CLASS_ID.equals(classId)) {
            return resource.getString(FormClass.LABEL_FIELD_ID);
        } else if(FolderClass.CLASS_ID.equals(classId)) {
            return resource.getString(FolderClass.LABEL_FIELD_ID.asString());
        } else {
            // TODO: lookup form class
            return classId == null ? resource.getId().asString() : classId.toString();
        }
    }
}
