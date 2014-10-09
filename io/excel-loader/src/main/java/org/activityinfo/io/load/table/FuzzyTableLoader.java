package org.activityinfo.io.load.table;

import com.google.common.collect.Lists;
import org.activityinfo.io.load.FileSource;
import org.activityinfo.io.load.LoadContext;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.service.store.StoreLoader;

import java.util.List;

public class FuzzyTableLoader {

    private LoadContext context;
    private String formLabel;

    private List<ColumnBuffer> columns = Lists.newArrayList();

    private int rowIndex = 0;

    public FuzzyTableLoader(LoadContext context, String formLabel) {
        this.context = context;
        this.formLabel = formLabel;
    }

    public FuzzyTableLoader(LoadContext context, FileSource fileSource) {
        this.context = context;
        this.formLabel = fileSource.getFilename();
    }

    public void pushDouble(int columnIndex, double value) {
        ensureColumns(columnIndex);
        columns.get(columnIndex).pushNumber(rowIndex, value);
    }

    public void pushString(int columnIndex, String value) {
        ensureColumns(columnIndex);
        columns.get(columnIndex).pushString(rowIndex, value);
    }

    public void pushBoolean(int columnIndex, boolean value) {
        ensureColumns(columnIndex);
        columns.get(columnIndex).pushBool(rowIndex, value);
    }

    private void ensureColumns(int columnIndex) {
        while(columnIndex >= columns.size()) {
            columns.add(new ColumnBuffer());
        }
    }

    public void nextRow() {
        rowIndex++;
    }

    public void done() {
        StoreLoader storeLoader = context.getStoreLoader();
        List<FieldReader> readers = Lists.newArrayList();

        FormClass form = new FormClass(Resources.generateId());
        form.setLabel(formLabel);
        form.setOwnerId(context.getParentId());

        for(ColumnBuffer buffer : columns) {
            if(buffer.getHeader() != null) {
                FormField field = new FormField(Resources.generateId());
                field.setType(buffer.guessType(1));
                field.setLabel(buffer.getHeader());
                form.addElement(field);

                if(field.getType() instanceof QuantityType) {
                    readers.add(new QuantityFieldReader(field.getId(), buffer));
                } else {
                    readers.add(new TextFieldReader(field.getId(), buffer));
                }
            }
        }
        storeLoader.create(form.asResource(), true);

        for(int i=1;i!=rowIndex;++i) {
            FormInstance instance = new FormInstance(Resources.generateId(), form.getId());
            for(FieldReader reader : readers) {
                reader.read(instance, i);
            }
            storeLoader.create(instance.asResource(), false);
        }
    }
}
