package org.activityinfo.model.type;

import com.bedatadriven.rebar.time.calendar.LocalDate;
import com.google.common.collect.Sets;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.component.ComponentReader;
import org.activityinfo.model.type.component.NullComponentReader;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ReferenceType implements FieldType {

    public enum TypeClass implements FieldTypeClass {
        INSTANCE {

            @Override
            public String getId() {
                return "REFERENCE";
            }

            @Override
            public String getLabel() {
                return "Reference";
            }

            @Override
            public FieldType createType(Record parameters) {
                ReferenceType type = new ReferenceType();
                type.setCardinality(Cardinality.valueOf(parameters.getString("cardinality")));
                type.setRange(parameters.getStringList("range"));
                return type;
            }

            @Override
            public FieldType createType() {
                return new ReferenceType()
                        .setCardinality(Cardinality.SINGLE)
                        .setRange(Collections.<ResourceId>emptySet());
            }

            @Override
            public FormClass getParameterFormClass() {
                FormClass formClass = new FormClass(ResourceId.create("_ref"));
                // todo
                return formClass;
            }
        }
    }

    private Cardinality cardinality;
    private Set<ResourceId> range;

    public ReferenceType() {
    }

    @Override
    public FieldTypeClass getTypeClass() {
        return TypeClass.INSTANCE;
    }

    public Cardinality getCardinality() {
        return cardinality;
    }

    public ReferenceType setCardinality(Cardinality cardinality) {
        this.cardinality = cardinality;
        return this;
    }

    /**
     * @return the set of FormClasses to which fields of this type can refer.
     */
    public Set<ResourceId> getRange() {
        return range;
    }


    public void setRange(ResourceId formClassId) {
        this.range = Collections.singleton(formClassId);
    }


    private void setRange(List<String> range) {
        Set<ResourceId> formClassIds = Sets.newHashSet();
        for(String id : range) {
            formClassIds.add(ResourceId.create(id));
        }
        setRange(formClassIds);
    }


    public ReferenceType setRange(Set<ResourceId> range) {
        this.range = range;
        return this;
    }

    @Override
    public Record getParameters() {
        return new Record()
                .set("range", Reference.to(range))
                .set("cardinality", cardinality);
    }

    @Override
    public ComponentReader<String> getStringReader(final String fieldName, String componentId) {
        return new ComponentReader<String>() {
            @Override
            public String read(Resource resource) {
                return resource.getRecord(fieldName).getString("id");
            }
        };
    }

    @Override
    public ComponentReader<LocalDate> getDateReader(String name, String componentId) {
       return new NullComponentReader<>();
    }


    /**
     * Convenience constructor for ReferenceTypes with single cardinality
     * @param formClassId the id of the form class which is the range of this field
     * @return a new ReferenceType
     */
    public static ReferenceType single(ResourceId formClassId) {
        ReferenceType type = new ReferenceType();
        type.setCardinality(Cardinality.SINGLE);
        type.setRange(Collections.singleton(formClassId));
        return type;
    }

    /**
     * Convenience constructor for ReferenceTypes with single cardinality
     * @param formClassIds the ids of the form class which constitute the range of this field
     * @return a new ReferenceType
     */
    public static ReferenceType single(Iterable<ResourceId> formClassIds) {
        ReferenceType type = new ReferenceType();
        type.setCardinality(Cardinality.SINGLE);
        type.setRange(Sets.newHashSet(formClassIds));
        return type;
    }

}
