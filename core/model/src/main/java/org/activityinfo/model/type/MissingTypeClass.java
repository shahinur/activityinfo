package org.activityinfo.model.type;

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
    public String getLabel() {
        return "Missing";
    }

    @Override
    public FieldType createType() {
        throw new UnsupportedOperationException();
    }

}
