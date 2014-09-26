package org.activityinfo.model.type;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.activityinfo.model.resource.IsRecord;
import org.activityinfo.model.resource.Record;

import java.util.List;

public class ListFieldValue implements FieldValue {
    private final ImmutableList<? extends FieldValue> elements;

    private ListFieldValue(List<? extends FieldValue> elements) {
        this.elements = ImmutableList.copyOf(elements);
    }

    public static FieldValue valueOf(FieldValue newElement) {
        return new ListFieldValue(ImmutableList.of(newElement));
    }

    public static ListFieldValue valueOf(List<? extends FieldValue> values) {
        if(values.isEmpty()) {
            return null;
        } else {
            return new ListFieldValue(values);
        }
    }

    public ListFieldValue withAppended(FieldValue value) {
        return new ListFieldValue(new ImmutableList.Builder<FieldValue>().addAll(elements).add(value).build());
    }

    public ImmutableList<? extends FieldValue> getElements() {
        return elements;
    }

    @Override
    public FieldTypeClass getTypeClass() {
        return ListFieldType.TYPE_CLASS;
    }


    public static ListFieldValue ofSubForms(List<? extends IsRecord> subForms) {
        ImmutableList.Builder<Record> listBuilder = new ImmutableList.Builder<>();
        for(IsRecord subForm : subForms) {
            Record record = subForm.asRecord();
            listBuilder.add(record);
        }
        return new ListFieldValue(listBuilder.build());
    }

    public List<Record> asRecordList() {
        List<Record> serializedElements = Lists.newArrayList();
        for (FieldValue element : elements) {
            if (element instanceof Record) {
                serializedElements.add((Record) element);
            }
        }
        return serializedElements;
    }
}
