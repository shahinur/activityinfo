package org.activityinfo.io.load;

import com.google.common.collect.Lists;
import org.activityinfo.io.load.format.DelimitedTextFormat;
import org.activityinfo.io.load.format.ZipArchiveHandler;

import java.util.List;

public class FileHandlers {
    private List<FileHandler> handlers = Lists.newArrayList();

    public FileHandlers() {
        handlers.add(new ZipArchiveHandler());
        handlers.add(new DelimitedTextFormat());
    }

    public FileHandler find(FileSource fileSource) {
        for(FileHandler handler : handlers) {
            if(handler.accept(fileSource)) {
                return handler;
            }
        }
        throw new FileFormatException(fileSource, FileFormatException.Kind.UNKNOWN_FORMAT);
    }
}
