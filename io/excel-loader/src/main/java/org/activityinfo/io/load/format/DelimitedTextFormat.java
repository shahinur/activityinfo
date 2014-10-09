package org.activityinfo.io.load.format;

import com.google.common.base.Charsets;
import org.activityinfo.io.load.FileHandler;
import org.activityinfo.io.load.FileSource;
import org.activityinfo.io.load.LoadContext;
import org.activityinfo.io.load.table.FuzzyTableLoader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DelimitedTextFormat implements FileHandler {

    @Override
    public boolean accept(FileSource fileSource) {
        return true;
    }

    @Override
    public void load(LoadContext context, FileSource fileSource) throws IOException, InvalidFormatException {

        try(InputStream in = fileSource.getContent().openBufferedStream()) {
            CSVParser parser = new CSVParser(
                new InputStreamReader(in, Charsets.UTF_8),
                    CSVFormat.DEFAULT.withHeader((String[])null));

            FuzzyTableLoader tableImporter = new FuzzyTableLoader(context, fileSource);
            for (CSVRecord row : parser) {
                for (int i = 0; i != row.size(); ++i) {
                    tableImporter.pushString(i, row.get(i));
                }
                tableImporter.nextRow();
            }
            tableImporter.done();
        }
    }
}
