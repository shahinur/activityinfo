package org.activityinfo.core.shared.form;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.Iri;
import org.activityinfo.core.shared.LocalizedString;
import org.activityinfo.core.shared.Resource;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

/**
 * The basic unit of data collection and storage. A form is similar to a Table
 * but is richer in that it contains instructions to the data collector, validation rules,
 * and potentially relationships with other UserForms.
 * <p/>
 * <p>UserForms are a superset of an OWL class. The user can define them as subclasses or superclasses of
 * another FormClass or RDFS class.</p>
 */
public class FormClass implements Resource, FormElementContainer {


    /**
     * Because FormClasses are themselves FormInstances, they have a class id of their own
     */
    public static final Cuid CLASS_ID = new Cuid("_class");

    /**
     * Instances of FormClass have one FormField: a label, which has its own
     * FormField id. It is defined at the application level to be a subproperty of
     * {@code _label}
     */
    public static final Cuid LABEL_FIELD_ID = new Cuid("_class_label");


    @NotNull
    private Cuid id;
    private Cuid parentId;

    private LocalizedString label;
    private Set<Iri> superClasses = Sets.newHashSet();
    private Set<Iri> subClasses = Sets.newHashSet();
    private List<FormElement> elements = Lists.newArrayList();

    public FormClass(Cuid id) {
        Preconditions.checkNotNull(id);
        this.id = id;
    }

    public FormClass copy() {
        final FormClass copy = new FormClass(this.getId());
        copy.setParentId(this.getParentId());
        copy.getElements().addAll(this.getElements());
        copy.getSubClasses().addAll(this.getSubClasses());
        copy.getSuperClasses().addAll(this.getSuperClasses());
        copy.setLabel(this.getLabel());
        return copy;
    }

    public Cuid getParentId() {
        return parentId;
    }

    public void setParentId(Cuid parentId) {
        this.parentId = parentId;
    }

    public FormElementContainer getParent(FormElement childElement) {
        return getContainerElementsImpl(this, childElement);
    }

    private static FormElementContainer getContainerElementsImpl(FormElementContainer container, final FormElement searchElement) {
        if (container.getElements().contains(searchElement)) {
            return container;
        }
        for (FormElement elem : container.getElements()) {
            if (elem instanceof FormElementContainer) {
                final FormElementContainer result = getContainerElementsImpl((FormElementContainer) elem, searchElement);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }


    public List<Cuid> getElementIds() {
        final List<Cuid> list = Lists.newArrayList();
        for (FormElement element : elements) {
            list.add(element.getId());
        }
        return list;
    }

    public void removeElementsById(final List<Cuid> list) {
        Iterables.removeIf(elements, new Predicate<FormElement>() {
            @Override
            public boolean apply(FormElement input) {
                return list.contains(input.getId());
            }
        });
    }

    public Cuid getId() {
        return id;
    }

    public void setId(Cuid id) {
        this.id = id;
    }

    public LocalizedString getLabel() {
        return label;
    }

    public void setLabel(LocalizedString label) {
        this.label = label;
    }

    public Set<Iri> getSuperClasses() {
        return superClasses;
    }

    public void setSuperClasses(Set<Iri> superClasses) {
        this.superClasses = superClasses;
    }

    public Set<Iri> getSubClasses() {
        return subClasses;
    }

    public void setSubClasses(Set<Iri> subClasses) {
        this.subClasses = subClasses;
    }

    public List<FormElement> getElements() {
        return elements;
    }

    public List<FormField> getFields() {
        final List<FormField> fields = Lists.newArrayList();
        collectFields(fields, getElements());
        return fields;
    }

    private static void collectFields(List<FormField> fields, List<FormElement> elements) {
        for (FormElement element : elements) {
            if (element instanceof FormField) {
                fields.add((FormField) element);
            } else if (element instanceof FormSection) {
                final FormSection formSection = (FormSection) element;
                collectFields(fields, formSection.getElements());
            }
        }
    }

    public void setElements(List<FormElement> elements) {
        this.elements = elements;
    }

    public void addElement(FormElement element) {
        elements.add(element);
    }
    
    @Override
    public String toString() {
        return "<FormClass: " + getLabel() + ">";
    }
}
