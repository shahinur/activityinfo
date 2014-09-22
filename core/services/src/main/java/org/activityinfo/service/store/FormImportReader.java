package org.activityinfo.service.store;

import java.io.IOException;
import java.io.InputStream;

public interface FormImportReader {

    void load(FormImportOptions options, InputStream inputStream, ImportWriter writer) throws IOException;

}
