package org.activityinfo.ui.store.remote.client;

import com.google.common.base.Function;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import org.activityinfo.model.resource.*;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.promise.Promise;
import org.activityinfo.service.store.RemoteStoreService;
import org.activityinfo.service.store.UpdateResult;
import org.activityinfo.ui.store.remote.client.serde.ObjectMappers;
import org.activityinfo.ui.store.remote.client.table.JsTableDataBuilder;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RemoteStoreServiceImpl implements RemoteStoreService {

    private static final Logger LOGGER = Logger.getLogger(RemoteStoreServiceImpl.class.getName());

    private RestEndpoint store;

    public RemoteStoreServiceImpl(RestEndpoint store) {
        this.store = store;
    }

    @Override
    public Promise<Resource> get(ResourceId resourceId) {
        return store.resolve("resource").resolve(resourceId.asString()).get(ObjectMappers.RESOURCE);
    }

    @Override
    public Promise<List<ResourceNode>> queryRoots() {
        return store.resolve("query").resolve("roots").get(ObjectMappers.NODE_LIST);
    }

    @Override
    public Promise<TableData> queryTable(TableModel tableModel) {
        return store.resolve("query").resolve("table")
                .postJson(Resources.toJson(tableModel.asRecord()))
                .then(new JsTableDataBuilder());
    }

    public Promise<UpdateResult> put(final Resource resource) {
        return store
                .resolve("resource")
                .resolve(resource.getId().asString())
                .putJson(Resources.toJson(resource))
                .then(new Function<Response, UpdateResult>() {
                    @Override
                    public UpdateResult apply(Response input) {
                        return ObjectMappers.UPDATE_RESULT.read(input.getText());
                    }
                });
    }


    @Override
    public Promise<ResourceTree> queryTree(ResourceId rootId) {

        JSONObject request = new JSONObject();
        request.put("rootId", new JSONString(rootId.asString()));

        return store.resolve("query").resolve("tree").postJson(request.toString())
        .then(new Function<Response, ResourceTree>() {
            @Override
            public ResourceTree apply(Response input) {
                LOGGER.log(Level.INFO, "tree: " + input.getText());
                return ObjectMappers.RESOURCE_TREE.read(input.getText());
            }
        });
    }
}
