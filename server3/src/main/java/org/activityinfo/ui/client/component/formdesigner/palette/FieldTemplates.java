package org.activityinfo.ui.client.component.formdesigner.palette;

import com.google.common.collect.Lists;
import org.activityinfo.model.type.NarrativeType;
import org.activityinfo.model.type.geo.GeoPointType;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.model.type.primitive.TextType;
import org.activityinfo.model.type.time.LocalDateIntervalType;
import org.activityinfo.model.type.time.LocalDateType;

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

        items.add(new TypeClassTemplate(QuantityType.TYPE_CLASS));
        items.add(new TypeClassTemplate(TextType.TYPE_CLASS));
        items.add(new TypeClassTemplate(NarrativeType.TYPE_CLASS));
        items.add(new TypeClassTemplate(LocalDateType.TYPE_CLASS));
        items.add(new TypeClassTemplate(LocalDateIntervalType.TYPE_CLASS));

        items.add(new CheckboxTemplate());
        items.add(new RadioButtonTemplate());

        items.add(new TypeClassTemplate(GeoPointType.TYPE_CLASS));

        return items;
    }
}
