package org.activityinfo.model.form;


import java.util.List;

public interface FormElementContainer {

    List<FormElement> getElements();

    FormElementContainer addElement(FormElement element);
}
