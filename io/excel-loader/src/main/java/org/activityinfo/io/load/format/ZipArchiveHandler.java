package org.activityinfo.io.load.format;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.activityinfo.io.load.FileFormatException;
import org.activityinfo.io.load.FileHandler;
import org.activityinfo.io.load.FileSource;
import org.activityinfo.io.load.LoadContext;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipArchiveHandler implements FileHandler {

    private static final Logger LOGGER = Logger.getLogger(ZipArchiveHandler.class.getName());

    private final List<ZipFormatHandler> formatHandlers = Lists.newArrayList();

    public ZipArchiveHandler() {
        formatHandlers.add(new XlsxFormat());
    }

    @Override
    public boolean accept(FileSource fileSource) {
        return fileSource.headMatches(0x50, 0x4B, 0x03, 0x04);
    }

    @Override
    public void load(LoadContext context, FileSource fileSource) throws IOException, InvalidFormatException {

        LOGGER.log(Level.INFO, "ZipArchiveHandler: " + fileSource.getFilename());

        Set<String> entries = readEntrySet(fileSource);
        Optional<ZipFormatHandler> format = matchFormat(fileSource, entries);
        if(format.isPresent()) {
            format.get().load(context, fileSource);
        } else {
            throw new FileFormatException(fileSource, FileFormatException.Kind.UNKNOWN_FORMAT);
        }
    }

    public Optional<ZipFormatHandler> matchFormat(FileSource fileSource, Set<String> entries) {
        for(ZipFormatHandler format : formatHandlers) {
            if(format.acceptZipFile(fileSource, entries)) {
                return Optional.of(format);
            }
        }
        return Optional.absent();
    }

    public Set<String> readEntrySet(FileSource fileSource) throws IOException {
        Set<String> entries = Sets.newHashSet();
        try(ZipInputStream zin = new ZipInputStream(fileSource.getContent().openStream())) {
            ZipEntry entry;
            while((entry = zin.getNextEntry()) != null) {
                entries.add(entry.getName());
            }
        }
        LOGGER.log(Level.INFO, "Entries: " + Iterables.limit(entries, 10));

        return entries;
    }
}
