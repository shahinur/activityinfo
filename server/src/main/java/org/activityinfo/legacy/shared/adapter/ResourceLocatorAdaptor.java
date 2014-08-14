package org.activityinfo.legacy.shared.adapter;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.gson.JsonParser;
import com.google.gwt.http.client.*;
import org.activityinfo.core.client.InstanceQuery;
import org.activityinfo.core.client.QueryResult;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.shared.Projection;
import org.activityinfo.core.shared.criteria.Criteria;
import org.activityinfo.legacy.client.Dispatcher;
import org.activityinfo.legacy.shared.command.result.ResourceResult;
import org.activityinfo.legacy.shared.model.GetResource;
import org.activityinfo.legacy.shared.model.PutResource;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.resource.*;
import org.activityinfo.model.system.ApplicationClassProvider;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.model.table.TableServiceAsync;
import org.activityinfo.promise.Promise;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Exposes a legacy {@code Dispatcher} implementation as new {@code ResourceLocator}
 */
public class ResourceLocatorAdaptor implements ResourceLocator {

    private final ApplicationClassProvider systemClassProvider = new ApplicationClassProvider();

    private final Dispatcher dispatcher;

    private final TableServiceAsync tableService;

    private final ProjectionAdapter projectionAdapter;

    private final JsonParser jsonParser = new JsonParser();

    public ResourceLocatorAdaptor(Dispatcher dispatcher, TableServiceAsync tableService) {
        this.dispatcher = dispatcher;
        this.tableService = tableService;
        this.projectionAdapter = new ProjectionAdapter(tableService);
    }

    @Override
    public Promise<FormClass> getFormClass(ResourceId classId) {
        if(classId.asString().startsWith("_")) {
            return Promise.resolved(systemClassProvider.get(classId));
        } else {
            return dispatcher.execute(new GetResource(classId)).then(new FormClassDeserializer());
        }
    }

    @Override
    public Promise<FormInstance> getFormInstance(ResourceId instanceId) {
        return dispatcher.execute(new GetResource(instanceId)).then(new Function<ResourceResult, FormInstance>() {
            @Override
            public FormInstance apply(ResourceResult input) {
                return FormInstance.fromResource(input.parseResource());
            }
        });
    }

    @Override
    public Promise<List<Resource>> get(Set<ResourceId> resourceIds) {
        return dispatcher.execute(new GetResource(resourceIds)).then(new Function<ResourceResult, List<Resource>>() {
            @Override
            public List<Resource> apply(ResourceResult input) {
                return input.parseResources();
            }
        });
    }

    @Override
    public Promise<TableData> queryTable(TableModel tableModel) {
        return tableService.query(tableModel);
    }

    @Override
    public Promise<Void> persist(IsResource resource) {
        return dispatcher.execute(new PutResource(resource)).thenDiscardResult();
    }

    @Override
    public Promise<Void> persist(List<? extends IsResource> resources) {
        final List<Promise<Void>> promises = Lists.newArrayList();
        if (resources != null && !resources.isEmpty()) {
            for (final IsResource resource : resources) {
                promises.add(persist(resource));
            }
        }
        return Promise.waitAll(promises);
    }

    @Override
    public Promise<List<FormInstance>> queryInstances(Criteria criteria) {
        throw new UnsupportedOperationException("deprecated");
    }

    @Override
    public Promise<QueryResult> queryProjection(InstanceQuery query) {
        return projectionAdapter.query(query);
    }

    @Override
    public Promise<ResourceTree> getTree(final ResourceId rootId) {

        final Promise<ResourceTree> result = new Promise<>();

        RequestBuilder request = new RequestBuilder(RequestBuilder.GET, "/service/store/tree/" + rootId.asString());
        request.setHeader("Accept", "application/json");
        request.setCallback(new RequestCallback() {
            @Override
            public void onResponseReceived(Request request, Response response) {
                ResourceNode rootNode;
                try {
                    String json = response.getText();
                    rootNode = ResourceNode.fromJson(jsonParser.parse(json).getAsJsonObject());
                } catch(Exception e) {
                    result.onFailure(e);
                    return;
                }

                result.onSuccess(new ResourceTree(rootNode));
            }

            @Override
            public void onError(Request request, Throwable throwable) {
                result.onFailure(throwable);
            }
        });
        try {
            request.send();
        } catch (RequestException e) {
            result.onFailure(e);
        }

        return result;
    }

    @Override
    public Promise<List<Projection>> query(InstanceQuery query) {
        return projectionAdapter.query(query).then(new Function<QueryResult, List<Projection>>() {
            @Override
            public List<Projection> apply(QueryResult input) {
                return input.getProjections();
            }
        });
    }

    @Override
    public Promise<Void> remove(Collection<ResourceId> resources) {
        throw new UnsupportedOperationException("todo");
    }
}
