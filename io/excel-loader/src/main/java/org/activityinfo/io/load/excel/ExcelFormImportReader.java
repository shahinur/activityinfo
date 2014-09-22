package org.activityinfo.io.load.excel;

import org.activityinfo.service.store.FormImportOptions;
import org.activityinfo.service.store.FormImportReader;
import org.activityinfo.service.store.ImportWriter;
import org.apache.poi.openxml4j.opc.OPCPackage;

import java.io.IOException;
import java.io.InputStream;

public class ExcelFormImportReader implements FormImportReader {

    @Override
    public void load(FormImportOptions options, InputStream inputStream, ImportWriter writer) throws IOException {

        // The package open is instantaneous, as it should be.
        try {
            OPCPackage p = OPCPackage.open(inputStream);
            ExcelStreamingImporter importer = new ExcelStreamingImporter(p, options, writer);
            importer.process();

        } catch(Exception e) {
            throw new IOException(e);
        }
    }
}
