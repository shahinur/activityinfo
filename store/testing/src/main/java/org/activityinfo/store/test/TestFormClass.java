package org.activityinfo.store.test;

import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.record.RecordBuilder;
import org.activityinfo.model.record.Records;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.type.Cardinality;
import org.activityinfo.model.type.barcode.BarcodeType;
import org.activityinfo.model.type.barcode.BarcodeValue;
import org.activityinfo.model.type.enumerated.EnumFieldValue;
import org.activityinfo.model.type.enumerated.EnumType;
import org.activityinfo.model.type.enumerated.EnumValue;
import org.activityinfo.model.type.expr.CalculatedFieldType;
import org.activityinfo.model.type.geo.GeoPoint;
import org.activityinfo.model.type.geo.GeoPointType;
import org.activityinfo.model.type.image.ImageRowValue;
import org.activityinfo.model.type.image.ImageType;
import org.activityinfo.model.type.image.ImageValue;
import org.activityinfo.model.type.number.Quantity;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.model.type.primitive.TextType;
import org.activityinfo.service.blob.BlobId;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class TestFormClass {

    public ResourceId workspaceId;
    public final FormClass formClass;
    public final FormField name;
    public final FormField serialNo;
    public final FormField age;
    public final EnumType genderType;
    public final FormField gender;
    public final EnumType problemType;
    public final FormField problems;
    public final FormField pos;
    public final FormField yearOfBirth;
    public final FormField picture;

    public TestFormClass(ResourceId workspaceId) {
        this.workspaceId = workspaceId;

        formClass = new FormClass(Resources.generateId());
        formClass.setLabel("My Form");
        formClass.setOwnerId(workspaceId);

        name = new FormField(Resources.generateId());
        name.setLabel("Name");
        name.setType(TextType.INSTANCE);
        formClass.addElement(name);

        serialNo = new FormField(Resources.generateId());
        serialNo.setLabel("Serial Num.");
        serialNo.setType(BarcodeType.INSTANCE);
        formClass.addElement(serialNo);

        age = new FormField(Resources.generateId());
        age.setCode("age");
        age.setLabel("Respondent's age");
        age.setType(new QuantityType("years"));
        formClass.addElement(age);

        genderType = new EnumType(Cardinality.SINGLE, Arrays.asList(
            new EnumValue(Resources.generateId(), "Male"),
            new EnumValue(Resources.generateId(), "Female")));

        gender = new FormField(Resources.generateId());
        gender.setLabel("Gender");
        gender.setType(genderType);
        formClass.addElement(gender);

        problemType = new EnumType(Cardinality.MULTIPLE, Arrays.asList(
            new EnumValue(Resources.generateId(), "Water"),
            new EnumValue(Resources.generateId(), "Education"),
            new EnumValue(Resources.generateId(), "Other")));

        problems = new FormField(Resources.generateId());
        problems.setLabel("Problems facing your village, family");
        problems.setType(problemType);
        formClass.addElement(problems);

        pos = new FormField(Resources.generateId());
        pos.setLabel("Geographic Position");
        pos.setType(GeoPointType.INSTANCE);
        formClass.addElement(pos);

        yearOfBirth = new FormField(Resources.generateId());
        yearOfBirth.setLabel("Year of Birth");
        yearOfBirth.setType(new CalculatedFieldType("2014-AGE"));
        formClass.addElement(yearOfBirth);

        picture = new FormField(Resources.generateId());
        picture.setLabel("Picture");
        picture.setType(new ImageType(Cardinality.SINGLE));
        formClass.addElement(picture);

    }

    public Iterable<Resource> instances(final int count) {
        return new Iterable<Resource>() {
            @Override
            public Iterator<Resource> iterator() {
                return instanceGenerator(count);
            }
        };
    }

    public Iterator<Resource> instanceGenerator(final int count) {
        return new UnmodifiableIterator<Resource>() {

            public int i = 0;

            @Override
            public boolean hasNext() {
                return i < count;
            }

            @Override
            public Resource next() {
                RecordBuilder record = Records.builder(formClass.getId())
                    .set(name.getName(), (i % 2 == 0) ? ("Bob " + i) : null)
                    .set(serialNo.getName(), BarcodeValue.valueOf(Integer.toHexString(Integer.valueOf(i).hashCode())))
                    .set(age.getName(), new Quantity(i, "years"))
                    .set(gender.getName(), genderType.getValues().get(i % 2))
                    .set(pos.getName(), new GeoPoint(i, i * 2))
                    .set(problems.getName(), problemSet(problemType, i))
                    .set(picture.getName(), new ImageValue(new ImageRowValue("image/jpeg", "image.png",
                        BlobId.generate().asString())));

                i++;

                return Resources.newResource(formClass.getId(), record.build());
            }
        };
    }


    public EnumFieldValue problemSet(EnumType problemType, int i) {
        List<ResourceId> problemSet = Lists.newArrayList();
        for(int j=0;j!=problemType.getValues().size();++j) {
            if(i % (j+2) == 0) {
                problemSet.add(problemType.getValues().get(j).getId());
            }
        }
        return new EnumFieldValue(problemSet);
    }
}
