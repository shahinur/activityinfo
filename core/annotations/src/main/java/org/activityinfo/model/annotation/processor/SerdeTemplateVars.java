package org.activityinfo.model.annotation.processor;

import org.apache.velocity.runtime.parser.node.SimpleNode;

import java.util.List;

public class SerdeTemplateVars extends TemplateVars {
    String pkg;
    String beanClass;
    String formClassId;
    String simpleClassName;
    String serdeClass;

    List<ScalarField> fields;
    List<ListField> listFields;

    private static final SimpleNode TEMPLATE = parsedTemplateForResource("serde.vm");


    @Override
    SimpleNode parsedTemplate() {
        return TEMPLATE;
    }
}
