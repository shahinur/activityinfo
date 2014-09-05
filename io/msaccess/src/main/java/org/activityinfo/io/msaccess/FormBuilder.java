package org.activityinfo.io.msaccess;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Table;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.type.NarrativeType;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.model.type.primitive.BooleanType;
import org.activityinfo.model.type.primitive.TextType;

public class FormBuilder {

    private final Table table;

    public FormBuilder(Table table) {
        this.table = table;
    }

    public void createClass() {
        FormClass formClass = new FormClass(Resources.generateId());
        formClass.setLabel(table.getName());

        for(Column column : table.getColumns()) {
            FormField field = new FormField(Resources.generateId());
            field.setCode(column.getName());

            switch(column.getType()) {
                case INT:
                case DOUBLE:
                case FLOAT:
                case LONG:
                case NUMERIC:
                    field.setType(new QuantityType());
                    break;
                case BOOLEAN:
                    field.setType(BooleanType.INSTANCE);
                    break;
                case TEXT:
                    field.setType(TextType.INSTANCE);
                    break;
                case MEMO:
                    field.setType(NarrativeType.INSTANCE);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported column type: " + column.getType().name());
            }

            formClass.addElement(field);
        }
    }
}
