package org.activityinfo.model.form;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.Reference;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.ReferenceType;
import org.activityinfo.model.type.TextType;
import org.activityinfo.model.type.geo.GeoPointType;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.model.type.time.LocalDateType;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The smallest logical unit of data entry. A single field can yield
 * multiple RDFS properties.
 */
public class FormField extends FormElement {
    private final ResourceId id;
    private String name;
    private String label;
    private String description;
    private String unit;
    private FormFieldType type;
    private Set<ResourceId> range;
    private String expression;
    private boolean readOnly;
    private boolean visible = true;
    private Set<ResourceId> superProperties = Sets.newHashSet();
    private boolean required;
    private FormFieldCardinality cardinality;
    private String calculation;

    public FormField(ResourceId id) {
        checkNotNull(id);
        this.id = id;
    }

    public FormField(ResourceId formClassId, String name) {
        this.id = FieldId.fieldId(formClassId, name);
        this.name = name;
    }

    public FormFieldCardinality getCardinality() {
        return cardinality;
    }

    public FormField setCardinality(FormFieldCardinality cardinality) {
        this.cardinality = cardinality;
        return this;
    }

    public ResourceId getId() {
        return id;
    }

    public String getName() { return name; }

    @NotNull
    public String getLabel() {
        return label;
    }

    public FormField setLabel(String label) {
        this.label = label;
        return this;
    }

    /**
     * @return an extended description of this field, presented to be
     * presented to the user during data entry
     */
    @NotNull
    public String getDescription() {
        return description;
    }

    public FormField setDescription(String description) {
        this.description = description;
        return this;
    }

    @NotNull
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * @return this field's type
     */
    public FormFieldType getType() {
        return type;
    }

    public FormField setType(FormFieldType type) {
        this.type = type;
        return this;
    }

    /**
     *
     * @return the set of ids of FormClasses that this reference field
     * may take as a value
     */
    public Set<ResourceId> getRange() {
        return range;
    }

    public FormField setRange(Set<ResourceId> range) {
        this.range = range;
        return this;
    }

    public FormField setRange(ResourceId classId) {
        this.range = Sets.newHashSet(classId);
        return this;
    }

    /**
     *
     * @return true if this field requires a response before submitting the form
     */
    public boolean isRequired() {
        return required;
    }

    public FormField setRequired(boolean required) {
        this.required = required;
        return this;
    }

    /**
     * @return the expression used to calculate this field's value if it is
     * not provided by the user
     */
    public String getExpression() {
        return expression;
    }

    public boolean isCalculated() {
        return !Strings.isNullOrEmpty(expression);
    }

    public FormField setExpression(String expression) {
        this.expression = expression;
        return this;
    }

    /**
     * @return true if this field is read-only.
     */
    boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    /**
     * @return true if this field is visible to the user
     */
    boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FormField formField = (FormField) o;

        if (id != null ? !id.equals(formField.id) : formField.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "FormField{" +
                "id=" + id +
                ", label=" + label +
                ", type=" + type +
                '}';
    }

    public Set<ResourceId> getSuperProperties() {
        return superProperties;
    }

    public void addSuperProperty(ResourceId propertyId) {
        superProperties.add(propertyId);
    }

    public void setSuperProperties(Set<ResourceId> superProperties) {
        this.superProperties = superProperties;
    }

    public void setSuperProperty(ResourceId superProperty) {
        this.superProperties = Collections.singleton(superProperty);
    }

    public boolean isSubPropertyOf(ResourceId parentProperty) {
        return this.superProperties.contains(parentProperty);
    }

    @Override
    public Record asRecord() {
        assert type != null : id + " has no type";

        Record record = new Record();
        record.set("name", name);
        record.set("label", label);
        record.set("type", type);
        record.set("required", required);

        record.set("expression", expression);

        switch(type) {
            case QUANTITY:
                record.set("unit", checkNotNull(getUnit()));
                break;
            case REFERENCE:
                record.set("range", Reference.to(checkNotNull(getRange(), id + " is missing a range")));
                record.set("cardinality", checkNotNull(getCardinality(),  id + " is missing cardinality"));
                break;
        }

        return record;
    }

    public static FormElement fromRecord(ResourceId formClassId, Record record) {
        FormField formField = new FormField(formClassId, record.getString("name"))
            .setLabel(record.getString("label"))
            .setType(FormFieldType.valueOf(record.getString("type")))
            .setRequired(record.getBoolean("required", false));

        if(record.has("expression")) {
            formField.setExpression(record.getString("expression"));
        }

        switch(formField.getType()) {
            case QUANTITY:
                formField.setUnit(checkNotNull(record.getString("unit")));
                break;
            case REFERENCE:
                formField.setRange(toRange(record.getReferenceSet("range")));
                formField.setCardinality(FormFieldCardinality.valueOf(record.getString("cardinality")));
                break;
        }


        return formField;
    }

    private static Set<ResourceId> toRange(Set<Reference> range) {
        Set<ResourceId> ids = Sets.newHashSet();
        for(Reference reference : range) {
            ids.add(reference.getId());
        }
        return ids;
    }

    public FieldType getFieldType() {
        switch(type) {
            case QUANTITY:
                return new QuantityType().setUnits(unit);

            case NARRATIVE:
                return new TextType().setMultiLine(true);

            case FREE_TEXT:
                return new TextType();

            case LOCAL_DATE:
                return LocalDateType.INSTANCE;

            case GEOGRAPHIC_POINT:
                return GeoPointType.INSTANCE;

            case REFERENCE:
                return new ReferenceType()
                        .setRange(range)
                        .setCardinality(cardinality);
        }

        throw new UnsupportedOperationException();
    }

    public void setCalculation(String calculation) {
        this.calculation = calculation;
    }

    public String getCalculation() {
        return calculation;
    }
}
