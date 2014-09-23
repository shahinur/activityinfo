package org.activityinfo.io.load.text;

import org.activityinfo.service.store.FormImportOptions;
import org.activityinfo.service.store.FormImportReader;
import org.activityinfo.service.store.ImportWriter;

import java.io.IOException;
import java.io.InputStream;

public class TextFileReader implements FormImportReader {


    @Override
    public void load(FormImportOptions options, InputStream inputStream, ImportWriter writer) throws IOException {

    }
}
