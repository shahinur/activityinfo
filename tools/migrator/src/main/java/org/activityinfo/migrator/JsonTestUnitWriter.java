package org.activityinfo.migrator;

import com.google.gson.JsonArray;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.Resources;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class JsonTestUnitWriter implements ResourceWriter {
    private final File file;
    private JsonArray array;

    public JsonTestUnitWriter(File file) throws IOException {
        this.array = new JsonArray();
        this.file = file;
    }

    @Override
    public void write(Resource resource) throws IOException {
        array.add(Resources.toJsonObject(resource));
    }

    public void finish() throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        JsonWriter jsonWriter = new JsonWriter(fileWriter);
        jsonWriter.setLenient(true);
        jsonWriter.setIndent("  ");
        Streams.write(array, jsonWriter);
        fileWriter.close();
    }
}
