package org.activityinfo.model.type.primitive;

import org.activityinfo.model.form.FormClassVisitor;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.FieldTypeClass;

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
    public <T> T accept(FormField field, FormClassVisitor<T> visitor) {
        return visitor.visitTextField(field, this);
    }

    @Override
    public String toString() {
        return "TextType";
    }

}
