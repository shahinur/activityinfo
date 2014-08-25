package org.activityinfo.ui.store.remote.client.resource;

import com.google.common.base.Function;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Response;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.resource.ResourceTree;

public class ResourceTreeParser implements Function<Response, ResourceTree> {
    @Override
    public ResourceTree apply(Response input) {
        ResourceTreeOverlay overlay = JsonUtils.safeEval(input.getText());
        ResourceNode root = parse(overlay.getRootNode());
        return new ResourceTree(root);
    }

    public static ResourceNode parse(ResourceNodeOverlay overlay) {
        ResourceNode node = new ResourceNode(ResourceId.valueOf(overlay.getString("id")));
        node.setOwnerId(ResourceId.valueOf(overlay.getString("ownerId")));
        node.setClassId(ResourceId.valueOf(overlay.getString("classId")));
        node.setLabel(overlay.getString("label"));
        node.setVersion(overlay.getLong("version"));
        node.setSubTreeVersion(overlay.getLong("subTreeVersion"));
        for(int i=0;i!=overlay.getChildren().length();++i) {
            node.getChildren().add(parse(overlay.getChildren().get(i)));
        }
        return node;
    }

}
