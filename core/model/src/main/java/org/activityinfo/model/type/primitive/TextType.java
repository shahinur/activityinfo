package org.activityinfo.model.type.primitive;

import com.bedatadriven.rebar.time.calendar.LocalDate;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.component.ComponentReader;
import org.activityinfo.model.type.component.NullComponentReader;

/**
 * A value type representing a single line of unicode text
 */
public class TextType implements FieldType {

    public static final FieldTypeClass TYPE_CLASS = new FieldTypeClass() {
        @Override
        public String getId() {
            return "FREE_TEXT";
        }

        @Override
        public String getLabel() {
            return "Text";
        }

        @Override
        public FieldType createType() {
            return INSTANCE;
        }
    };

    public static final TextType INSTANCE = new TextType();


    private TextType() {
    }

    @Override
    public FieldTypeClass getTypeClass() {
        return TYPE_CLASS;
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
