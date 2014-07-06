package org.activityinfo.model.type;

import com.bedatadriven.rebar.time.calendar.LocalDate;
import com.google.common.collect.Sets;
import org.activityinfo.model.form.FormFieldCardinality;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.Reference;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.component.ComponentReader;
import org.activityinfo.model.type.component.NullComponentReader;

import java.util.Date;
import java.util.Set;

public class ReferenceType implements FieldType {

    public enum TypeClass implements FieldTypeClass {
        INSTANCE {
            @Override
            public String getId() {
                return "ref";
            }

            @Override
            public FieldType createType(Record parameters) {
                return new ReferenceType()
                    .setCardinality(FormFieldCardinality.valueOf(parameters.getString("cardinality")))
                    .setRangeFromReferenceSet(parameters.getReferenceSet("range"));
            }
        }
    }


    private FormFieldCardinality cardinality;
    private Set<ResourceId> range;

    public ReferenceType() {
    }

    @Override
    public FieldTypeClass getTypeClass() {
        return TypeClass.INSTANCE;
    }

    public FormFieldCardinality getCardinality() {
        return cardinality;
    }

    public ReferenceType setCardinality(FormFieldCardinality cardinality) {
        this.cardinality = cardinality;
        return this;
    }

    /**
     * @return the set of FormClasses to which fields of this type can refer.
     */
    public Set<ResourceId> getRange() {
        return range;
    }

    private ReferenceType setRangeFromReferenceSet(Set<Reference> range) {
        Set<ResourceId> resources = Sets.newHashSet();
        for(Reference ref : range) {
            resources.add(ref.getId());
        }
        setRange(resources);
        return this;
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
    public ComponentReader getStringReader(final String fieldName, String componentId) {
        return new ComponentReader() {
            @Override
            public Object read(Resource resource) {
                return resource.getReference(fieldName).getId().asString();
            }
        };
    }

    @Override
    public ComponentReader<LocalDate> getDateReader(String name, String componentId) {
       return new NullComponentReader<>();
    }
}
