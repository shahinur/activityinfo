package org.activityinfo.ui.client.component.formdesigner.palette;

import com.google.common.collect.Lists;
import org.activityinfo.model.type.FieldTypeClass;

import java.util.List;

public class FieldTemplates {


    public static List<FieldTemplate> list() {
        List<FieldTemplate> items = Lists.newArrayList();

        // use only types supported by backend for the moment

//        // Add all type classes to the palette except for reference + enum types:
//        // we will handle those specially
//        for(FieldTypeClass typeClass : TypeRegistry.get().getTypeClasses()) {
//            if(typeClass != ReferenceType.TypeClass.INSTANCE &&
//               typeClass != EnumType.TypeClass.INSTANCE) {
//                items.add(new TypeClassTemplate(typeClass));
//            }
//        }
//
//        // ReferenceTypes are a bit abstract, we will provide a number of
//        // concrete types that make will hopefully make sense to the user

        items.add(new TypeClassTemplate(FieldTypeClass.QUANTITY));
        items.add(new TypeClassTemplate(FieldTypeClass.FREE_TEXT));
        items.add(new TypeClassTemplate(FieldTypeClass.NARRATIVE));
        items.add(new TypeClassTemplate(FieldTypeClass.BOOLEAN));

        items.add(new CheckboxTemplate());
        items.add(new RadioButtonTemplate());
        
        return items;
    }
}
