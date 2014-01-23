package org.activityinfo.model.shared.form;

import com.google.common.collect.Sets;
import org.activityinfo.model.LocalizedString;
import org.activityinfo.model.shared.Iri;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotNull;

/**
 * The smallest logical unit of data entry. A single field can yield
 * multiple RDFS properties.
 *
 *
 */
public class FormField implements FormElement {

    private final Iri id;
    private LocalizedString label;
    private LocalizedString description;
    private FormFieldType type;
    private Set<Iri> range;
    private String calculation;
    private boolean readOnly;
    private boolean visible = true;
    private List<Iri> dimensions;

    public FormField(Iri id) {
        this.id = id;
    }

    public Iri getId() {
        return id;
    }

    public LocalizedString getLabel() {
        return label;
    }

    public void setLabel(LocalizedString label) {
        this.label = label;
    }

    /**
     * @return an extended description of this field, presented to be
     * presented to the user during data entry
     */
    public LocalizedString getDescription() {
        return description;
    }

    public void setDescription(LocalizedString description) {
        this.description = description;
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

    public Set<Iri> getRange() {
        return range;
    }

    public void setRange(Set<Iri> range) {
        this.range = range;
    }

    public void setRange(Iri range) {
        this.range = Collections.singleton(range);
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

    public List<Iri> getDimensions() {
        return dimensions;
    }

    public void setDimensions(List<Iri> dimensions) {
        this.dimensions = dimensions;
    }




}
