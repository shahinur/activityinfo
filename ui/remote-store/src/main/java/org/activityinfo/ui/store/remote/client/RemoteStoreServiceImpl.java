package org.activityinfo.ui.store.remote.client;

import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import org.activityinfo.model.resource.*;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.promise.Promise;
import org.activityinfo.ui.store.remote.client.table.JsTableDataBuilder;

import java.util.List;

public class RemoteStoreServiceImpl implements RemoteStoreService {

    public static interface ResourceMapper extends ObjectMapper<Resource> {}

    public static interface NodeListMapper extends ObjectMapper<List<ResourceNode>> {}

    public static interface ResourceTreeMapper extends ObjectMapper<ResourceTree> {

        public static final ResourceTreeMapper INSTANCE = GWT.create(ResourceTreeMapper.class);
    }

    public static final ResourceMapper RESOURCE_MAPPER = GWT.create(ResourceMapper.class);

    public static final NodeListMapper NODE_LIST_MAPPER = GWT.create(NodeListMapper.class);


    private RestEndpoint store;

    public RemoteStoreServiceImpl(RestEndpoint store) {
        this.store = store;
    }

    @Override
    public Promise<Resource> get(ResourceId resourceId) {
        return store.resolve("resource").resolve(resourceId.asString()).get(RESOURCE_MAPPER);
    }

    @Override
    public Promise<List<ResourceNode>> queryRoots() {
        return store.resolve("query").resolve("roots").get(NODE_LIST_MAPPER);
    }

    @Override
    public Promise<TableData> queryTable(TableModel tableModel) {
        return store.resolve("query").resolve("table")
                .post(Resources.toJson(tableModel.asRecord()))
                .then(new JsTableDataBuilder());
    }

    @Override
    public Promise<ResourceTree> queryTree(ResourceId rootId) {

        JSONObject request = new JSONObject();
        request.put("rootId", new JSONString(rootId.asString()));

        return store.resolve("query").resolve("tree")
                .post(request.toString(), ResourceTreeMapper.INSTANCE);

    }
}
