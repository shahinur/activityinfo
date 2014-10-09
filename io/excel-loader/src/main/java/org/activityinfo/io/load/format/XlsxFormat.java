package org.activityinfo.io.load.format;


import org.activityinfo.io.load.FileSource;
import org.activityinfo.io.load.LoadContext;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

public class XlsxFormat implements ZipFormatHandler {


    @Override
    public boolean acceptZipFile(FileSource source, Set<String> entries) {
        // We can tell whether this zip file is an XLSX document by checking for
        // some standard entries
        return entries.containsAll(Arrays.asList(
            "[Content_Types].xml", "xl/_rels/workbook.xml.rels", "xl/workbook.xml"));
    }

    @Override
    public void load(LoadContext context, FileSource fileSource) throws IOException, InvalidFormatException {

        try(XlsxParser parser = new XlsxParser(context, fileSource)) {
            parser.readSheets();
        }
    }
}
