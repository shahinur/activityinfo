package org.activityinfo.ui.store.remote.client;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import org.activityinfo.model.analysis.PivotTableModel;
import org.activityinfo.model.resource.*;
import org.activityinfo.model.table.Bucket;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.promise.Promise;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.store.RemoteStoreService;
import org.activityinfo.service.store.UpdateResult;
import org.activityinfo.service.tasks.UserTask;
import org.activityinfo.ui.store.remote.client.cube.BucketOverlay;
import org.activityinfo.ui.store.remote.client.resource.*;
import org.activityinfo.ui.store.remote.client.table.JsTableDataBuilder;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class RemoteStoreServiceImpl implements RemoteStoreService {

    private static final Logger LOGGER = Logger.getLogger(RemoteStoreServiceImpl.class.getName());

    private final RestEndpoint service;
    private final RestEndpoint store;

    public RemoteStoreServiceImpl(RestEndpoint service) {
        this.service = service;
        this.store = this.service.resolve("store");
    }

    @Override
    public Promise<UserResource> get(ResourceId resourceId) {
        return store.resolve("resource").resolve(resourceId.asString()).get(new UserResourceParser());
    }

    @Override
    public Promise<List<ResourceNode>> getWorkspaces() {
        return store.resolve("query").resolve("roots").get(new ResourceNodeListParser());
    }

    @Override
    public Promise<TableData> queryTable(TableModel tableModel) {
        return store.resolve("query").resolve("table")
                .postJson(ResourceSerializer.toJson(tableModel.asRecord()))
                .then(new JsTableDataBuilder());
    }

    @Override
    public Promise<List<Bucket>> queryCube(PivotTableModel cubeModel) {
        return store.resolve("query").resolve("cube")
            .postJson(ResourceSerializer.toJson(cubeModel.asRecord()))
            .then(new Function<Response, List<Bucket>>() {
                @Override
                public List<Bucket> apply(Response input) {
                    return BucketOverlay.parse(input.getText());
                }
            });
    }

    @Override
    public Promise<UpdateResult> create(Resource resource) {
        return store
                .resolve("resources")
                .postJson(ResourceSerializer.toJson(resource))
                .then(new Function<Response, UpdateResult>() {
                    @Nullable
                    @Override
                    public UpdateResult apply(@Nullable Response input) {
                        return UpdateResultParser.parse(input);
                    }
                });
    }

    @Override
    public Promise<UserTask> startImport(ResourceId ownerId, BlobId blobId) {
        return service
                .resolve("load")
                .postUrlEncoded("ownerId=" + ownerId.asString() + "&blobId=" + blobId)
                .then(new Function<Response, UserTask>() {
                    @Override
                    public UserTask apply(Response input) {
                        return UserTask.fromRecord(ResourceParser.parseRecord(input.getText()));
                    }
                });
    }

    @Override
    public Promise<List<UserTask>> getTasks() {
        return service
                .resolve("load")
                .getJson().then(new Function<Response, List<UserTask>>() {
                @Override
                public List<UserTask> apply(Response input) {
                    List<UserTask> tasks = Lists.newArrayList();
                    JsArray<JavaScriptObject> array = JsonUtils.safeEval(input.getText());
                    for(int i=0;i!=array.length();++i) {
                        tasks.add(UserTask.fromRecord(ResourceParser.parseRecord(array.get(i))));
                    }
                    return tasks;
                }
            });
    }

    public Promise<UpdateResult> put(final Resource resource) {
        return store
                .resolve("resource")
                .resolve(resource.getId().asString())
                .putJson(ResourceSerializer.toJson(resource))
                .then(new Function<Response, UpdateResult>() {
                    @Override
                    public UpdateResult apply(Response input) {
                        return UpdateResultParser.parse(input);
                    }
                });
    }


    @Override
    public Promise<FolderProjection> getFolder(ResourceId rootId) {

        JSONObject request = new JSONObject();
        request.put("rootId", new JSONString(rootId.asString()));

        return store.resolve("query")
                .resolve("tree")
                .postJson(request.toString())
                .then(new ResourceTreeParser());
    }

    @Override
    public Promise<Void> remove(Set<ResourceId> resources) {
        return Promise.rejected(new UnsupportedOperationException("todo"));
    }
}
