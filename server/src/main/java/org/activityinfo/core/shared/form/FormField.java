package org.activityinfo.core.shared.form;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import org.activityinfo.core.shared.Cuid;
import java.lang.String;
import org.activityinfo.core.shared.criteria.ClassCriteria;
import org.activityinfo.core.shared.criteria.Criteria;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Set;

/**
 * The smallest logical unit of data entry. A single field can yield
 * multiple RDFS properties.
 */
public class FormField implements FormElement {

    private final Cuid id;
    private String label;
    private String description;
    private String unit;
    private FormFieldType type;
    private Criteria range;
    private String calculation;
    private boolean readOnly;
    private boolean visible = true;
    private Set<Cuid> superProperties = Sets.newHashSet();
    private boolean required;
    private FormFieldCardinality cardinality;

    public FormField(Cuid id) {
        Preconditions.checkNotNull(id);
        this.id = id;
    }

    public FormFieldCardinality getCardinality() {
        return cardinality;
    }

    public void setCardinality(FormFieldCardinality cardinality) {
        this.cardinality = cardinality;
    }

    public Cuid getId() {
        return id;
    }

    @NotNull
    public String getLabel() {
        return Strings.nullToEmpty(label);
    }

    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return an extended description of this field, presented to be
     * presented to the user during data entry
     */
    @NotNull
    public String getDescription() {
        return Strings.nullToEmpty(description);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @NotNull
    public String getUnit() {
        return Strings.nullToEmpty(unit);
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

    public void setType(FormFieldType type) {
        this.type = type;
    }

    /**
     *
     * @return
     */
    public Criteria getRange() {
        return range;
    }

    public void setRange(Criteria range) {
        this.range = range;
    }

    public void setRange(Cuid classId) {
        this.range = new ClassCriteria(classId);
    }

    /**
     *
     * @return true if this field requires a response before submitting the form
     */
    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    /**
     * @return the expression used to calculate this field's value if it is
     * not provided by the user
     */
    public String getCalculation() {
        return calculation;
    }

    public void setCalculation(String calculation) {
        this.calculation = calculation;
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

    public Set<Cuid> getSuperProperties() {
        return superProperties;
    }

    public void addSuperProperty(Cuid propertyId) {
        superProperties.add(propertyId);
    }

    public void setSuperProperties(Set<Cuid> superProperties) {
        this.superProperties = superProperties;
    }

    public void setSuperProperty(Cuid superProperty) {
        this.superProperties = Collections.singleton(superProperty);
    }

    public boolean isSubPropertyOf(Cuid parentProperty) {
        return this.superProperties.contains(parentProperty);
    }
}
