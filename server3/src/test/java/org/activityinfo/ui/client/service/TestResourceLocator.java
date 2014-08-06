package org.activityinfo.ui.client.service;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import org.activityinfo.core.client.InstanceQuery;
import org.activityinfo.core.client.QueryResult;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.shared.Projection;
import org.activityinfo.core.shared.criteria.Criteria;
import org.activityinfo.legacy.shared.adapter.ProjectionAdapter;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.resource.IsResource;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.model.table.TableService;
import org.activityinfo.promise.Promise;
import org.activityinfo.service.tables.TableServiceImpl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class TestResourceLocator implements ResourceLocator {

    private TestResourceStore store;
    private TableService tableService;
    private ProjectionAdapter projectionAdapter;

    /**
     * Constructs a {@code ResourceLocator} for an in-memory resource store whose
     * state is initially loaded from a JSON file.
     *
     * @param resourceName
     */
    public TestResourceLocator(String resourceName) throws IOException {
        store = new TestResourceStore().load(resourceName);
        tableService = new SerializationTestingTableService(new TableServiceImpl(store));
        projectionAdapter = new ProjectionAdapter(new TestTableServiceAsync(tableService));
    }


    @Override
    public Promise<FormClass> getFormClass(ResourceId formId) {
        return Promise.resolved(FormClass.fromResource(store.get(formId)));
    }

    @Override
    public Promise<FormInstance> getFormInstance(ResourceId formId) {
        return Promise.resolved(FormInstance.fromResource(store.get(formId)));
    }

    @Override
    public Promise<List<Resource>> get(Set<ResourceId> resourceIds) {
        List<Resource> resources = Lists.newArrayList();
        for(ResourceId id : resourceIds) {
            resources.add(store.get(id).copy());
        }
        return Promise.resolved(resources);
    }

    @Override
    public Promise<TableData> queryTable(TableModel tableModel) {
        TableData tableData = tableService.buildTable(tableModel);
        return Promise.resolved(tableData);
    }

    @Override
    public Promise<Void> persist(IsResource resource) {
        System.out.println("Persisting: " + resource);
        store.put(resource.asResource());
        return Promise.done();
    }

    @Override
    public Promise<Void> persist(List<? extends IsResource> resources) {
        for(IsResource resource : resources) {
            store.put(resource.asResource());
        }
        return Promise.done();
    }

    @Override
    public Promise<List<FormInstance>> queryInstances(Criteria criteria) {
        List<FormInstance> matching = Lists.newArrayList();
        for(Resource resource : store.all()) {
            if(resource.has("classId")) {
                FormInstance instance = FormInstance.fromResource(resource);
                if(criteria.apply(instance)) {
                    matching.add(instance);
                }
            }
        }
        return Promise.resolved(matching);
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
        for(ResourceId id : resources) {
            store.remove(id);
        }
        return Promise.done();
    }

    @Override
    public Promise<QueryResult> queryProjection(InstanceQuery query) {
        return projectionAdapter.query(query);
    }



    public void dump() throws IOException {

        JsonArray array = new JsonArray();
        for(Resource resource : store.all()) {
            array.add(Resources.toJsonObject(resource));
        }

        File tempFile = File.createTempFile("resources", ".json");
        FileWriter fileWriter = new FileWriter(tempFile);
        JsonWriter jsonWriter = new JsonWriter(fileWriter);
        jsonWriter.setLenient(true);
        jsonWriter.setIndent("  ");
        Streams.write(array, jsonWriter);
        fileWriter.close();

        System.out.println("Dumped resources to " + tempFile.getAbsolutePath());

    }
}
