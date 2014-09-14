package org.activityinfo.model.annotation.processor;

import com.google.common.collect.Lists;

import javax.lang.model.element.VariableElement;
import java.util.List;

public class FormModel {

    private VariableElement classIdElement;
    private List<FieldModel> fields = Lists.newArrayList();


    public void addField(FieldModel fieldModel) {

    }

    public void setClassId(VariableElement classIdElement) {
        this.classIdElement = classIdElement;
    }
}
