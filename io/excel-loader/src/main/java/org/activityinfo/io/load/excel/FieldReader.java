package org.activityinfo.io.load.excel;

import org.activityinfo.model.form.FormInstance;

public interface FieldReader {
    void read(FormInstance instance, int rowIndex);
}
