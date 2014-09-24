package org.activityinfo.model.annotation.processor;

import org.apache.velocity.runtime.parser.node.SimpleNode;

import java.util.List;
import java.util.SortedSet;

public class SerdeTemplateVars extends TemplateVars {
    String pkg;
    String beanClass;
    String formClassId;
    String simpleClassName;
    String serdeClass;
    SortedSet<String> imports;

    String generated;
    String record;
    String list;
    String arrayList;

    List<Field> fields;
    List<ListField> listFields;

    private static final SimpleNode TEMPLATE = parsedTemplateForResource("serde.vm");


    @Override
    SimpleNode parsedTemplate() {
        return TEMPLATE;
    }
}
