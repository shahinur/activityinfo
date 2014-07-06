package org.activityinfo.model.type;


import com.bedatadriven.rebar.time.calendar.LocalDate;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.type.component.ComponentReader;

import java.util.Date;

public interface FieldType {

    public static final String DEFAULT_COMPONENT = "_default";

    /**
     * @return the {@code FieldTypeClass} of which this {@code FieldType}
     * is a member
     */
    FieldTypeClass getTypeClass();

    /**
     *
     * @return a {@code Record} containing this type's parameters.
     */
    Record getParameters();

    /**
     * @return a ComponentReader for this types's given component
     */
    ComponentReader<String> getStringReader(String fieldName, String componentId);

    ComponentReader<LocalDate> getDateReader(String name, String componentId);
}
