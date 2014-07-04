package org.activityinfo.core.shared.form;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.activityinfo.core.shared.Cuid;
import java.lang.String;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * A logical group of form fields
 */
public class FormSection implements FormElement, FormElementContainer {


    private final Cuid id;
    private String label;
    private final List<FormElement> elements = Lists.newArrayList();

    public FormSection(Cuid id) {
        Preconditions.checkNotNull(id);
        this.id = id;
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


    public List<FormElement> getElements() {
        return elements;
    }

    @Override
    public void addElement(FormElement element) {
        elements.add(element);
    }

    @Override
    public String toString() {
        return "FormSection{" +
                "id=" + id +
                ", label=" + label +
                '}';
    }
}
