package org.activityinfo.load;

import com.google.common.collect.Lists;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.primitive.TextType;

import java.util.List;
import java.util.Random;

public class FormDistribution {


    private final RandomText randomText;
    private final Random random = new Random();

    public FormDistribution() {

        randomText = new RandomText();

        // Create a new form to test against
        FormClass formClass = new FormClass(Resources.generateId());
        formClass.setOwnerId(ResourceId.valueOf("d518"));
        formClass.setLabel("Thousand monkeys");

        int numFields = 5+random.nextInt(25);

        for(int i=0;i!=numFields;++i) {
            formClass.addElement(generateRandomField());
        }

        System.out.println("Submitting form " + formClass.getId());

    }

    private FormField generateRandomField() {
        FormField field = new FormField(Resources.generateId());
        field.setLabel(randomText.sampleLabel());
        field.setType(generateRandomType());
        return field;
    }

    private TextType generateRandomType() {
        //        List<FieldTypeClass> typeClasses = getTypes();
        //        FieldTypeClass typeClass = typeClasses.get(random.nextInt(typeClasses.size()));
        //        if(typeClass instanceof ParametrizedFieldType) {
        //            return generateRandomParameters(typeClass)
        //        }
        return TextType.INSTANCE;
    }

    private List<FieldTypeClass> getTypes() {
        List<FieldTypeClass> range = Lists.newArrayList();
        range.add(TextType.TYPE_CLASS);
        //return Lists.newArrayList(TypeRegistry.get().getTypeClasses());

        return range;
    }
}
