package org.activityinfo.io.load;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;

public interface FileHandler {

    boolean accept(FileSource fileSource);

    void load(LoadContext context, FileSource fileSource) throws IOException, InvalidFormatException;
}
