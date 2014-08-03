package org.activityinfo.model.form;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.*;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The smallest logical unit of data entry.
 */
public class FormField extends FormElement {
    private final ResourceId id;
    private String label;
    private String description;
    private String expression;
    private String relevanceConditionExpression;
    private FieldType type;
    private boolean readOnly;
    private boolean visible = true;
    private Set<ResourceId> superProperties = Sets.newHashSet();
    private boolean required;

    public FormField(ResourceId id) {
        checkNotNull(id);
        this.id = id;
    }

    public ResourceId getId() {
        return id;
    }

    @NotNull
    public String getLabel() {
        return label;
    }

    public FormField setLabel(String label) {
        assert label != null;
        this.label = label;
        return this;
    }

    public String getRelevanceConditionExpression() {
        return relevanceConditionExpression;
    }

    public void setRelevanceConditionExpression(String relevanceConditionExpression) {
        this.relevanceConditionExpression = relevanceConditionExpression;
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

    public FieldType getType() {
        assert type != null : "type is missing for " + id;
        return type;
    }

    public FormField setType(FieldType type) {
        this.type = type;
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

    public boolean hasRelevanceConditionExpression() {
        return !Strings.isNullOrEmpty(relevanceConditionExpression);
    }

    public FormField setExpression(String expression) {
        this.expression = expression;
        return this;
    }

    /**
     * @return true if this field is read-only.
     */
    public boolean isReadOnly() {
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
                ", type=" + type.getTypeClass().getLabel() +
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

    public FormField setSuperProperty(ResourceId superProperty) {
        this.superProperties = Collections.singleton(superProperty);
        return this;
    }

    public boolean isSubPropertyOf(ResourceId parentProperty) {
        return this.superProperties.contains(parentProperty);
    }

    @Override
    public Record asRecord() {
        assert type != null : id + " has no type";

        Record record = new Record();
        record.set("id", id.asString());
        record.set("label", label);
        record.set("type", toRecord(type));
        record.set("required", required);
        record.set("expression", expression);
        record.set("relevanceConditionExpression", relevanceConditionExpression);

        if(!superProperties.isEmpty()) {
            record.set("superProperties", new ReferenceValue(superProperties).asRecord());
        }

        return record;
    }

    private Record toRecord(FieldType type) {
        Record record = new Record();
        record.set("typeClass", type.getTypeClass().getId());
        if(type instanceof ParametrizedFieldType) {
            record.set("parameters", ((ParametrizedFieldType)type).getParameters());
        }
        return record;
    }

    public static FormElement fromRecord(Record record) {
        FormField formField = new FormField(ResourceId.create(record.getString("id")))
            .setLabel(record.getString("label"))
            .setType(typeFromRecord(record.getRecord("type")))
            .setRequired(record.getBoolean("required", false));

        if(record.has("expression")) {
            formField.setExpression(record.getString("expression"));
        }
        if (record.has("relevanceConditionExpression")) {
            formField.setRelevanceConditionExpression(record.getString("relevanceConditionExpression"));
        }
        if(record.has("superProperties")) {
            ReferenceValue superProperties = ReferenceValue.fromRecord(record.getRecord("superProperties"));
            formField.setSuperProperties(superProperties.getResourceIds());
        }

        return formField;
    }

    private static FieldType typeFromRecord(Record record) {
        String typeClassId = record.getString("typeClass");
        FieldTypeClass typeClass = TypeRegistry.get().getTypeClass(typeClassId);
        if(typeClass instanceof ParametrizedFieldTypeClass) {
            return ((ParametrizedFieldTypeClass)typeClass).deserializeType(record.getRecord("parameters"));
        } else {
            return typeClass.createType();
        }
    }

}
