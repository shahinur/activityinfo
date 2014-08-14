package org.activityinfo.legacy.shared.adapter;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.activityinfo.core.client.InstanceQuery;
import org.activityinfo.core.client.QueryResult;
import org.activityinfo.core.shared.Projection;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.formTree.FieldPath;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.ReferenceValue;
import org.activityinfo.model.type.TypeRegistry;

import java.util.List;
import java.util.Map;
import java.util.Set;

class Projector {


    private Map<ResourceId, Resource> resourceMap;

    public Projector(Map<ResourceId, Resource> resourceMap) {
        this.resourceMap = resourceMap;
    }

    public QueryResult naiveQuery(InstanceQuery query) {
        List<Projection> results = Lists.newArrayList();
        for(Resource resource : resourceMap.values()) {
            if(resource.has("classId")) {

                FormInstance instance = FormInstance.fromResource(resource);
                if(query.getCriteria().apply(instance)) {
                    results.add(project(resource, query.getFieldPaths()));
                }
            }
        }
        return new QueryResult(results, results.size());
    }

    private Projection project(Resource resource, List<FieldPath> fieldPaths) {
        Projection projection = new Projection(resource.getId(), ResourceId.valueOf(resource.getString("classId")));
        for(FieldPath fieldPath : fieldPaths) {
            projection.setValue(fieldPath, getValue(resource, fieldPath));
        }
        return projection;
    }

    private Object getValue(Resource resource, FieldPath fieldPath) {
        Set<Object> values = Sets.newHashSet();
        collectValues(resource, fieldPath, values);
        if(values.size() == 1) {
            return values.iterator().next();
        } else {
            return null;
        }
    }

    private void collectValues(Resource resource, FieldPath fieldPath, Set<Object> values) {

        ResourceId rootFieldId = fieldPath.getRoot();
        if(resource.has("classId")) {
            if(!fieldPath.isNested()) {
                // leaf
                values.add(parseFieldValue(resource.get(rootFieldId.asString())));
            } else {
                Record record = resource.isRecord(rootFieldId.asString());
                if(record != null) {
                    ReferenceValue referenceValue = ReferenceValue.fromRecord(record);
                    for(ResourceId ref : referenceValue.getResourceIds()) {
                        Resource refResource = resourceMap.get(ref);
                        collectValues(refResource, fieldPath.relativeTo(rootFieldId), values);
                    }
                }
            }
        }
    }

    private Object parseFieldValue(Object value) {
        if(value instanceof Record) {
            return TypeRegistry.get().deserializeFieldValue((Record) value);
        }
        return value;
    }
}
