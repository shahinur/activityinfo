package org.activityinfo.model.type;

import org.activityinfo.model.type.time.MissingFieldType;

public class MissingTypeClass implements FieldTypeClass {

    public static final String TYPE_ID = "MISSING";

    public static final MissingTypeClass INSTANCE = new MissingTypeClass();

    private MissingTypeClass() {
    }

    @Override
    public String getId() {
        return TYPE_ID;
    }

    @Override
    public FieldType createType() {
        return MissingFieldType.INSTANCE;
    }

}
