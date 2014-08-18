package org.activityinfo.ui.store.remote.client.serde;

import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.GWT;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.resource.ResourceTree;
import org.activityinfo.service.store.UpdateResult;

import java.util.List;

public class ObjectMappers {

    static interface ResourceMapper extends ObjectMapper<Resource> {}
    static interface NodeListMapper extends ObjectMapper<List<ResourceNode>> { }
    static interface ResourceTreeMapper extends ObjectMapper<ResourceTree> { }
    static interface UpdateResultMapper extends ObjectMapper<UpdateResult> { }

    public static final ObjectMapper<Resource> RESOURCE = GWT.create(ResourceMapper.class);
    public static final ObjectMapper<List<ResourceNode>> NODE_LIST = GWT.create(NodeListMapper.class);
    public static final ObjectMapper<ResourceTree> RESOURCE_TREE = GWT.create(ResourceTreeMapper.class);
    public static final ObjectMapper<UpdateResult> UPDATE_RESULT = GWT.create(UpdateResultMapper.class);

}
