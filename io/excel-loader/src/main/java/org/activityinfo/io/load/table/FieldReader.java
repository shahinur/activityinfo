package org.activityinfo.io.load.table;

import org.activityinfo.model.form.FormInstance;

public interface FieldReader {
    void read(FormInstance instance, int rowIndex);
}
