package org.activityinfo.store.tasks.export;

import org.activityinfo.model.type.FieldValue;

import java.util.Collections;
import java.util.List;

class Column {

    private final String heading;
    private final FieldValueConverter converter;

    public Column(String heading, FieldValueConverter converter) {
        this.heading = heading;
        this.converter = converter;
    }

    public static List<Column> unique(FieldValueConverter converter) {
        return Collections.singletonList(new Column(null, converter));
    }


    public String getHeading() {
        return heading;
    }

    public Object convert(FieldValue value) {
        return converter.convertValue(value);
    }
}
