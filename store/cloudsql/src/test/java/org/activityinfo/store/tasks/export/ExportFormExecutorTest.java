package org.activityinfo.store.tasks.export;

import com.google.common.base.Charsets;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.io.ByteSource;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.record.RecordBuilder;
import org.activityinfo.model.record.Records;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.system.Folder;
import org.activityinfo.model.system.FolderClass;
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
import org.activityinfo.service.tasks.BlobResult;
import org.activityinfo.service.tasks.ExportFormTask;
import org.activityinfo.store.hrd.TestingEnvironment;
import org.activityinfo.store.tasks.TestingTaskContext;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.Rule;
import org.junit.Test;

import java.io.StringReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;

public class ExportFormExecutorTest {

    @Rule
    public TestingEnvironment environment = new TestingEnvironment();

    @Test
    public void test() throws Exception {

        Folder folder = new Folder();
        folder.setLabel("My Workspace");

        Resource workspace = Resources.newResource(Resources.ROOT_ID, FolderClass.INSTANCE.toRecord(folder));
        environment.getStore().create(environment.getUser(), workspace);

        FormClass formClass = new FormClass(Resources.generateId());
        formClass.setLabel("My Form");
        formClass.setOwnerId(workspace.getId());

        FormField name = new FormField(Resources.generateId());
        name.setLabel("Name");
        name.setType(TextType.INSTANCE);
        formClass.addElement(name);

        FormField serialNo = new FormField(Resources.generateId());
        serialNo.setLabel("Serial Num.");
        serialNo.setType(BarcodeType.INSTANCE);
        formClass.addElement(serialNo);

        FormField age = new FormField(Resources.generateId());
        age.setCode("age");
        age.setLabel("Respondent's age");
        age.setType(new QuantityType("years"));
        formClass.addElement(age);

        EnumType genderType = new EnumType(Cardinality.SINGLE, Arrays.asList(
            new EnumValue(Resources.generateId(), "Male"),
            new EnumValue(Resources.generateId(), "Female")));

        FormField gender = new FormField(Resources.generateId());
        gender.setLabel("Gender");
        gender.setType(genderType);
        formClass.addElement(gender);

        EnumType problemType = new EnumType(Cardinality.MULTIPLE, Arrays.asList(
            new EnumValue(Resources.generateId(), "Water"),
            new EnumValue(Resources.generateId(), "Education"),
            new EnumValue(Resources.generateId(), "Other")));

        FormField problems = new FormField(Resources.generateId());
        problems.setLabel("Problems facing your village, family");
        problems.setType(problemType);
        formClass.addElement(problems);

        FormField pos = new FormField(Resources.generateId());
        pos.setLabel("Geographic Position");
        pos.setType(GeoPointType.INSTANCE);
        formClass.addElement(pos);

        FormField yearOfBirth = new FormField(Resources.generateId());
        yearOfBirth.setLabel("Year of Birth");
        yearOfBirth.setType(new CalculatedFieldType("2014-AGE"));
        formClass.addElement(yearOfBirth);

        FormField picture = new FormField(Resources.generateId());
        picture.setLabel("Picture");
        picture.setType(new ImageType(Cardinality.SINGLE));
        formClass.addElement(picture);

        environment.getStore().create(environment.getUser(), formClass.asResource());

        // Populate data
        for(int i=0;i!=10;++i) {
            RecordBuilder record = Records.builder(formClass.getId())
                .set(name.getName(), (i % 2 == 0) ? ("Bob " + i) : null)
                .set(serialNo.getName(), BarcodeValue.valueOf(Integer.toHexString(Integer.valueOf(i).hashCode())))
                .set(age.getName(), new Quantity(i, "years"))
                .set(gender.getName(), genderType.getValues().get(i % 2))
                .set(pos.getName(), new GeoPoint(i, i * 2))
                .set(problems.getName(), problemSet(problemType, i))
                .set(picture.getName(), new ImageValue(new ImageRowValue("image/jpeg", "image.png",
                    BlobId.generate().asString())));

            Resource resource = Resources.newResource(formClass.getId(), record.build());

            environment.getStore().create(environment.getUser(), resource);
        }

        ExportFormTask task = new ExportFormTask();
        task.setFormClassId(formClass.getId());

        TestingTaskContext context = new TestingTaskContext(environment);
        ExportFormExecutor executor = new ExportFormExecutor();
        executor.execute(context, task);

        BlobResult blobResult = Iterables.getOnlyElement(context.getBlobResults());

        ByteSource byteSource = context.getBlob(BlobId.valueOf(blobResult.getBlobId()));

        String csvText = new String(byteSource.read(), Charsets.UTF_8);

        System.out.println(csvText);

        CSVParser parser = new CSVParser(new StringReader(csvText), CSVFormat.DEFAULT);
        Iterator<CSVRecord> iterator = parser.iterator();

        CSVRecord header = iterator.next();
        assertThat(header, hasItems(
            "Name",
            "Serial Num.",
            "Respondent's age",
            "Gender",
            "Problems facing your village, family - Water",
            "Problems facing your village, family - Education",
            "Problems facing your village, family - Other",
            "Geographic Position - Latitude",
            "Geographic Position - Longitude",
            "Year of Birth",
            "Picture"));
    }

    private EnumFieldValue problemSet(EnumType problemType, int i) {
        List<ResourceId> problemSet = Lists.newArrayList();
        for(int j=0;j!=problemType.getValues().size();++j) {
            if(i % (j+2) == 0) {
                problemSet.add(problemType.getValues().get(j).getId());
            }
        }
        return new EnumFieldValue(problemSet);
    }
}