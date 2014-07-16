package org.activityinfo.ui.client.component.formdesigner.palette;

import com.google.common.collect.Lists;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.ReferenceType;
import org.activityinfo.model.type.TypeRegistry;
import org.activityinfo.model.type.enumerated.EnumType;

import java.util.List;

public class FieldTemplates {


    public static List<FieldTemplate> list() {
        List<FieldTemplate> items = Lists.newArrayList();

        // Add all type classes to the palette except for reference types:
        // we will handle those specially
        for(FieldTypeClass typeClass : TypeRegistry.get().getTypeClasses()) {
            if(typeClass != ReferenceType.TypeClass.INSTANCE &&
               typeClass != EnumType.TypeClass.INSTANCE) {
                items.add(new TypeClassTemplate(typeClass));
            }
        }

        // ReferenceTypes are a bit abstract, we will provide a number of
        // concrete types that make will hopefully make sense to the user

        items.add(new CheckboxTemplate());
        items.add(new RadioButtonTemplate());
        
        return items;
    }
}
