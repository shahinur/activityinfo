package org.activityinfo.model.type;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.activityinfo.model.resource.IsRecord;
import org.activityinfo.model.resource.Record;

import java.util.List;

public class ListFieldValue implements FieldValue, IsRecord {
    private final ImmutableList<? extends FieldValue> elements;

    private ListFieldValue(List<? extends FieldValue> elements) {
        this.elements = ImmutableList.copyOf(elements);
    }

    public static FieldValue valueOf(FieldValue newElement) {
        return ListFieldValue.valueOf(newElement);
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

    @Override
    public Record asRecord() {
        List<Record> elements = Lists.newArrayList();
        for(FieldValue value : this.elements) {
            assert value instanceof IsRecord;
            elements.add(((IsRecord) value).asRecord());
        }

        return new Record().set("elements", elements);
    }
}
