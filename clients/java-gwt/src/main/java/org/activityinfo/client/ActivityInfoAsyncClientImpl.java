package org.activityinfo.client;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.client.cube.BucketOverlay;
import org.activityinfo.client.resource.*;
import org.activityinfo.client.table.JsTableDataBuilder;
import org.activityinfo.model.analysis.PivotTableModel;
import org.activityinfo.model.analysis.PivotTableModelClass;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.resource.*;
import org.activityinfo.model.table.Bucket;
import org.activityinfo.model.table.ColumnSet;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.model.table.TableModelClass;
import org.activityinfo.promise.Promise;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.store.UpdateResult;
import org.activityinfo.service.tasks.UserTask;
import org.activityinfo.service.tasks.UserTaskStatus;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class ActivityInfoAsyncClientImpl implements ActivityInfoAsyncClient {

    private static final Logger LOGGER = Logger.getLogger(ActivityInfoAsyncClientImpl.class.getName());

    private static final int TASK_POLL_INTERVAL_MS = 2500;

    private final RestEndpoint service;
    private final RestEndpoint store;

    private Random random = new Random();

    public ActivityInfoAsyncClientImpl() {
        this(new RestEndpoint("/service"));
    }

    public ActivityInfoAsyncClientImpl(RestEndpoint service) {
        this.service = service;
        this.store = this.service.resolve("store");
    }

    @Override
    public Promise<UserResource> get(ResourceId resourceId) {
        return store.resolve("resource").resolve(resourceId.asString()).getJson(new UserResourceParser());
    }

    @Override
    public Promise<List<ResourceNode>> getWorkspaces() {
        return store.resolve("query").resolve("roots").getJson(new ResourceNodeListParser());
    }

    @Override
    public Promise<ColumnSet> queryColumns(TableModel tableModel) {
        return store.resolve("query").resolve("table")
                .postJson(ResourceSerializer.toJson(TableModelClass.INSTANCE.toRecord(tableModel)))
                .then(new JsTableDataBuilder());
    }

    @Override
    public Promise<List<Bucket>> queryCube(PivotTableModel cubeModel) {
        return store.resolve("query").resolve("cube")
            .postJson(ResourceSerializer.toJson(PivotTableModelClass.INSTANCE.toRecord(cubeModel)))
            .then(new Function<Response, List<Bucket>>() {
                @Override
                public List<Bucket> apply(Response input) {
                    return BucketOverlay.parse(input.getText());
                }
            });
    }

    @Override
    public String getBlobDownloadUrl(BlobId blobId) {
        UrlBuilder builder = new UrlBuilder();
        builder.setProtocol(Window.Location.getProtocol());
        builder.setHost(Window.Location.getHost());
        builder.setPath("/service/blob/" + blobId.asString());
        return builder.buildString();
    }

    @Override
    public Promise<UpdateResult> create(Resource resource) {
        return store
                .resolve("resources")
                .postJson(ResourceSerializer.toJson(resource))
                .then(new Function<Response, UpdateResult>() {
                    @Nullable
                    @Override
                    public UpdateResult apply(Response input) {
                        return UpdateResultParser.parse(input.getText());
                    }
                });
    }

    @Override
    public Promise<List<UserTask>> getTasks() {
        return service
                .resolve("tasks")
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

    @Override
    public Promise<UserTask> startTask(Record taskModel) {
        return service
        .resolve("tasks")
        .resolve(generateTaskId())
        .postJson(ResourceSerializer.toJson(taskModel))
        .then(new Function<Response, UserTask>() {
            @Override
            public UserTask apply(Response input) {
                return UserTask.fromRecord(ResourceParser.parseRecord(input.getText()));
            }
        });
    }

    public Promise<UserTask> getTask(String taskId) {
        return service
                .resolve("tasks")
                .resolve(taskId)
                .getJson()
                .then(new Function<Response, UserTask>() {
                    @Override
                    public UserTask apply(Response input) {
                        return UserTask.fromRecord(ResourceParser.parseRecord(input.getText()));
                    }
                });
    }

    @Override
    public Promise<UserTask> executeTask(Record taskModel) {
        Promise<UserTask> finalResult = new Promise<>();
        waitForTask(startTask(taskModel), finalResult);
        return finalResult;
    }

    private String generateTaskId() {
        return Resources.generateId().asString();
    }

    private void waitForTask(Promise<UserTask> request, final Promise<UserTask> finalResult) {
        request.then(new AsyncCallback<UserTask>() {
            @Override
            public void onFailure(Throwable caught) {
                finalResult.onFailure(caught);
            }

            @Override
            public void onSuccess(final UserTask result) {
                if(result.getStatus() == UserTaskStatus.COMPLETE) {
                    finalResult.onSuccess(result);
                } else {
                    Scheduler.get().scheduleFixedDelay(new Scheduler.RepeatingCommand() {
                        @Override
                        public boolean execute() {
                            Promise<UserTask> updatedStatus = getTask(result.getId());
                            waitForTask(updatedStatus, finalResult);
                            return false;
                        }
                    }, TASK_POLL_INTERVAL_MS);
                }
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
                        return UpdateResultParser.parse(input.getText());
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
    public Promise<UpdateResult> remove(ResourceId resourceId) {
        return store
                .resolve("resource")
                .resolve(resourceId.asString())
                .deleteJson("")
                .then(new Function<Response, UpdateResult>() {
                    @Nullable
                    @Override
                    public UpdateResult apply(Response input) {
                        return UpdateResultParser.parse(input.getText());
                    }
                });
    }

    @Override
    public Promise<Void> ping() {
        return service.resolve("ping").get("nocache=" + random.nextInt()).thenDiscardResult();
    }
}
