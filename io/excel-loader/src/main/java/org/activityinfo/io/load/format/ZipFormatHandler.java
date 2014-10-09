package org.activityinfo.io.load.format;

import org.activityinfo.io.load.FileSource;
import org.activityinfo.io.load.LoadContext;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.util.Set;

/**
 * Handler for file types based on the ZIP format, such as XLSX.
 */
public interface ZipFormatHandler {

    boolean acceptZipFile(FileSource source, Set<String> entries);

    void load(LoadContext context, FileSource fileSource) throws IOException, InvalidFormatException;

}
