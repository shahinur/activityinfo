package org.activityinfo.model.type;

import com.google.common.collect.Sets;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceIdPrefixType;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * A type that represents a link or reference to another {@code Resource}
 */
public class ReferenceType implements ParametrizedFieldType {


    public static class TypeClass implements ParametrizedFieldTypeClass, RecordFieldTypeClass {

        private TypeClass() {
        }

        @Override
        public String getId() {
            return "REFERENCE";
        }

        @Override
        public String getLabel() {
            return "Reference";
        }

        @Override
        public FieldType createType() {
            return new ReferenceType()
                    .setCardinality(Cardinality.SINGLE)
                    .setRange(Collections.<ResourceId>emptySet());
        }

        @Override
        public FieldType deserializeType(Record parameters) {
            ReferenceType type = new ReferenceType();
            String cardinalityEncoded = parameters.isString("cardinality");
            if(cardinalityEncoded == null) {
                type.setCardinality(Cardinality.SINGLE);
            } else {
                type.setCardinality(Cardinality.valueOf(cardinalityEncoded));
            }
            Record record = parameters.isRecord("range");
            if(record != null) {
                type.setRange(ReferenceValue.fromRecord(record).getResourceIds());
            } else {
                // previous encoding
                type.setRange(parameters.getStringList("range"));
            }
            return type;
        }

        @Override
        public FieldValue deserialize(Record record) {
            return ReferenceValue.fromRecord(record);
        }

        @Override
        public FormClass getParameterFormClass() {
            FormField rangeField = new FormField(ResourceId.valueOf("range"));
            rangeField.setLabel("Other Form");
            rangeField.setDescription("Choose the form to which the field should be linked. " +
                                      "When filling out the form, you will be able to choose from " +
                                      "among the submissions to that form.");

            FormClass formClass = new FormClass(ResourceIdPrefixType.TYPE.id("ref"));
            formClass.addElement(rangeField);
            return formClass;
        }
    }

    public static final TypeClass TYPE_CLASS = new TypeClass();

    private Cardinality cardinality;
    private Set<ResourceId> range;

    public ReferenceType() {
    }

    @Override
    public ParametrizedFieldTypeClass getTypeClass() {
        return TYPE_CLASS;
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
            formClassIds.add(ResourceId.valueOf(id));
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
                .set("classId", getTypeClass().getParameterFormClass().getId())
                .set("range", new ReferenceValue(range).asRecord())
                .set("cardinality", cardinality);
    }

    @Override
    public boolean isValid() {
        return true;
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

    public static FieldType multiple(Collection<ResourceId> formClassIds) {
        ReferenceType type = new ReferenceType();
        type.setCardinality(Cardinality.MULTIPLE);
        type.setRange(Sets.newHashSet(formClassIds));
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ReferenceType that = (ReferenceType) o;

        if (cardinality != that.cardinality) {
            return false;
        }
        if (!range.equals(that.range)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = cardinality.hashCode();
        result = 31 * result + range.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ReferenceType{" +
               "cardinality=" + cardinality +
               ", range=" + range +
               '}';
    }
}
