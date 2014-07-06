package org.activityinfo.model.type;

import com.bedatadriven.rebar.time.calendar.LocalDate;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.type.component.ComponentReader;
import org.activityinfo.model.type.component.NullComponentReader;

public class TextType implements FieldType {

    public enum TypeClass implements FieldTypeClass {
        INSTANCE {
            @Override
            public String getId() {
                return "text";
            }

            @Override
            public FieldType createType(Record typeParameters) {
                return new TextType()
                        .setMultiLine(typeParameters.getBoolean("multiLine", false));
            }
        }
    }

    @Override
    public FieldTypeClass getTypeClass() {
        return TypeClass.INSTANCE;
    }

    private boolean multiLine;

    public boolean isMultiLine() {
        return multiLine;
    }

    public TextType setMultiLine(boolean multiLine) {
        this.multiLine = multiLine;
        return this;
    }

    @Override
    public Record getParameters() {
        return new Record()
                .set("multiLine", multiLine);
    }

    @Override
    public ComponentReader getStringReader(final String fieldName, String componentId) {
        assert DEFAULT_COMPONENT.equals(componentId);

        return new ComponentReader() {

            @Override
            public String read(Resource resource) {
                return resource.isString(fieldName);
            }
        };
    }

    @Override
    public ComponentReader<LocalDate> getDateReader(String name, String componentId) {
        return new NullComponentReader<>();
    }
}
