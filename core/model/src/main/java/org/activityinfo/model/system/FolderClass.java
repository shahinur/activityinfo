package org.activityinfo.model.system;

import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.record.RecordBeanClass;
import org.activityinfo.model.record.RecordBuilder;
import org.activityinfo.model.record.Records;
import org.activityinfo.model.resource.ResourceId;

public final class FolderClass implements RecordBeanClass<Folder> {

    public static final ResourceId CLASS_ID = ResourceId.valueOf("_folder");

    public static final FolderClass INSTANCE = new FolderClass();

    private FolderClass() { }

    public static final String DESCRIPTION_FIELD_NAME = "description";

    public static final ResourceId DESCRIPTION_FIELD_ID = ResourceId.valueOf("description");

    public static final String LABEL_FIELD_NAME = "label";

    public static final ResourceId LABEL_FIELD_ID = ResourceId.valueOf("label");


    @Override
    public ResourceId getClassId() { return CLASS_ID; }

    @Override
    public Folder toBean(Record record) {
        Folder bean = new Folder();
            bean.setDescription(record.isString("description"));
                bean.setLabel(record.isString("label"));
            return bean;
    }

    @Override
    public Record toRecord(Folder bean) {
        RecordBuilder recordBuilder = Records.builder(CLASS_ID);
            recordBuilder.set("description", bean.getDescription());
            recordBuilder.set("label", bean.getLabel());
            return recordBuilder.build();
    }

    @Override
    public FormClass get() {
        return getFormClass();
    }

    public static FormClass getFormClass() {
        FormClass formClass = new FormClass(CLASS_ID);

        formClass.addElement(new FormField(ResourceId.valueOf("description"))
        .setLabel("description")
        .setType(org.activityinfo.model.type.primitive.TextType.INSTANCE));

        formClass.addElement(new FormField(ResourceId.valueOf("label"))
        .setLabel("label")
        .setType(org.activityinfo.model.type.primitive.TextType.INSTANCE));
        return formClass;
    }
}
