package org.activityinfo.legacy.shared.adapter;

import com.google.common.base.Function;
import com.google.common.collect.*;
import org.activityinfo.legacy.client.Dispatcher;
import org.activityinfo.api2.client.InstanceQuery;
import org.activityinfo.fp.client.Promise;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.Projection;
import org.activityinfo.api2.shared.application.ApplicationProperties;
import org.activityinfo.api2.shared.criteria.Criteria;
import org.activityinfo.api2.shared.criteria.IdCriteria;
import org.activityinfo.api2.shared.form.FormClass;
import org.activityinfo.api2.shared.form.FormField;
import org.activityinfo.api2.shared.form.FormInstance;
import org.activityinfo.api2.shared.form.tree.FieldPath;
import org.activityinfo.fp.shared.BiFunction;

import java.util.*;

/**
 * Naive implementation that joins and projects a multi-level
 * instance query.
 */
class Joiner implements Function<InstanceQuery, Promise<List<Projection>>> {


    private final Criteria criteria;
    private final ClassProvider classProvider;
    private Set<FieldPath> fields;
    private List<FieldPath> joinFields;
    private Dispatcher dispatcher;

    public Joiner(Dispatcher dispatcher, List<FieldPath> fields, Criteria criteria) {
        this.dispatcher = dispatcher;

        // find any fields used to do joins
        this.joinFields = joinFields(fields);

        // create a set of all the fields we need to fetch.
        // make sure the join fields are included, otherwise
        // we have to maintain a separate structure for them.
        this.fields = Sets.newHashSet(fields);
        this.fields.addAll(joinFields);

        this.criteria = criteria;

        this.classProvider = new ClassProvider(dispatcher);
    }

    private List<FieldPath> joinFields(List<FieldPath> paths) {

        // given a set of paths like...
        // territory.province.name
        // territory.province.code
        // territory.name
        // attribute.label
        //
        // we need to find the fields to join on,
        // in increasing order of depth:

        // territory
        // attribute
        // territory.province

        Set<FieldPath> referenceFields = Sets.newHashSet();
        for(FieldPath path : paths) {
            if(path.isNested()) {
                for(int i=1;i<path.getDepth();i++) {
                    referenceFields.add(path.ancestor(i));
                }
            }
        }
        List<FieldPath> ordered = Lists.newArrayList(referenceFields);
        Collections.sort(ordered, new Comparator<FieldPath>() {
            @Override
            public int compare(FieldPath o1, FieldPath o2) {
                return o1.getDepth() - o2.getDepth();
            }
        });

        return ordered;
    }

    @Override
    public Promise<List<Projection>> apply(InstanceQuery instanceQuery) {


        Promise<List<FormInstance>> instances = query(criteria);
        Promise<List<FormClass>> classes = instances.join(new FetchFormClasses());

        Promise<List<Projection>> results = Promise.fmap(new ProjectFunction(null)).apply(classes, instances);

        // now schedule the joins
        for(FieldPath fieldToJoin : joinFields) {
            results = results.join(new FetchAndJoinFunction(fieldToJoin));
        }

        return results;
    }

    private Promise<List<FormInstance>> query(Criteria criteria) {
        return new QueryExecutor(dispatcher, criteria).execute();
    }

    private class ProjectFunction extends BiFunction<List<FormClass>,List<FormInstance>,List<Projection>> {

        private FieldPath prefix;

        private ProjectFunction(FieldPath prefix) {
            this.prefix = prefix;
        }

        @Override
        public List<Projection> apply(List<FormClass> formClasses, List<FormInstance> instances) {

            if(prefix != null) {
                throw new UnsupportedOperationException();
            }

            // build a map from property id -> projected field path
            Multimap<Cuid, FieldPath> map = HashMultimap.create();
            for(FieldPath path : fields) {
                map.put(path.getRoot(), new FieldPath(path.getRoot()));
            }

            // our map now contains
            // _label -> c00001._label
            // c23424 -> c00001.c23434

            // we now look at super properties to
            // additional bindings

            for(FormClass formClass : formClasses) {
                for(FormField field : formClass.getFields()) {
                    for(Cuid superPropertyId : field.getSuperProperties()) {
                        if(map.containsKey(superPropertyId)) {
                            map.putAll(field.getId(), map.get(superPropertyId));
                        }
                    }
                }
            }

            // now create our projections based on these mappings
            List<Projection> projections = Lists.newArrayList();
            for(FormInstance instance : instances) {

                Projection projection = new Projection(instance.getId());

                for(FieldPath classPath : map.get(ApplicationProperties.CLASS_PROPERTY)) {
                    projection.setValue(classPath, instance.getClassId());
                }

                for(Map.Entry<Cuid, Object> entry : instance.getValueMap().entrySet()) {
                    for(FieldPath targetPath : map.get(entry.getKey())) {
                        projection.setValue(targetPath, entry.getValue());
                    }
                }
                projections.add(projection);
            }
            return projections;
        }
    }

    private class FetchFormClasses implements Function<List<FormInstance>, Promise<List<FormClass>>> {

        @Override
        public Promise<List<FormClass>> apply(List<FormInstance> instances) {
            Set<Cuid> classIds = Sets.newHashSet();
            for(FormInstance instance : instances) {
                classIds.add(instance.getClassId());
            }

            return Promise.map(classIds, classProvider);
        }
    }

    /**
     * Fetches all instances that are referenced by the parent instances
     */
    private class FetchAndJoinFunction implements Function<List<Projection>, Promise<List<Projection>>> {

        private FieldPath referenceField;

        public FetchAndJoinFunction(FieldPath referenceField) {
            this.referenceField = referenceField;
        }

        @Override
        public Promise<List<Projection>> apply(List<Projection> projections) {

            // first collect the ids of the nested FormInstances
            Set<Cuid> instanceIds = Sets.newHashSet();
            for(Projection projection : projections) {
                instanceIds.addAll(projection.getReferenceValue(referenceField));
            }

            if(instanceIds.isEmpty()) {
                return Promise.resolved(projections);
            } else {
                return new QueryExecutor(dispatcher, new IdCriteria(instanceIds))
                    .execute()
                    .then(new JoinFunction(referenceField, projections));
            }
        }
    }

    private class JoinFunction implements Function<List<FormInstance>, List<Projection>> {

        private final FieldPath referenceField;
        private final List<Projection> projections;

        public JoinFunction(FieldPath referenceField, List<Projection> projections) {
            this.referenceField = referenceField;
            this.projections = projections;
        }

        @Override
        public List<Projection> apply(List<FormInstance> instances) {
            Map<Cuid, FormInstance> instanceMap = indexJoinedInstances(instances);
            for(Projection projection : projections) {
                Set<Cuid> referencedIds = projection.getReferenceValue(referenceField);
                for(Cuid referencedId : referencedIds) {
                    FormInstance referenceInstance = instanceMap.get(referencedId);
                    if(referenceInstance == null) {
                        throw new IllegalStateException("Missing referenced instance " + referencedId +
                            " (legacy id: " + CuidAdapter.getLegacyIdFromCuid(referencedId) + ") for field " +
                                    referenceField);
                    }
                    populateReferencedFields(projection, referenceInstance);
                }
            }
            return projections;
        }

        private Map<Cuid, FormInstance> indexJoinedInstances(List<FormInstance> instances) {
            Map<Cuid, FormInstance> instanceMap = Maps.newHashMap();
            for(FormInstance instance : instances) {
                instanceMap.put(instance.getId(), instance);
            }
            return instanceMap;
        }

        private void populateReferencedFields(Projection projection, FormInstance referencedInstance) {
            for(Map.Entry<Cuid, Object> entry : referencedInstance.getValueMap().entrySet()) {
                FieldPath path = new FieldPath(referenceField, entry.getKey());
                if(fields.contains(path)) {
                    projection.setValue(path, entry.getValue());
                }
            }
        }
    }
}