package org.activityinfo.store.hrd.entity.workspace;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;
import com.google.common.annotations.VisibleForTesting;
import org.activityinfo.model.json.ObjectMapperFactory;
import org.activityinfo.model.json.RecordSerialization;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.resource.ResourceId;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Lazily-deserialized wrapper of a Record
 */
public class SerializedRecord {

    public static final String CONTENTS_PROPERTY = "P";

    private Record record;
    private String json;

    private SerializedRecord() {}

    public static SerializedRecord fromEntity(Entity entity) {
        Text json = (Text) entity.getProperty(CONTENTS_PROPERTY);
        if(json == null) {
            return null;
        }

        SerializedRecord s = new SerializedRecord();
        s.json = json.getValue();
        return s;
    }

    public static SerializedRecord of(Record record) {
        if(record == null) {
            return null;
        }

        SerializedRecord s = new SerializedRecord();
        s.record = record;
        return s;
    }


    public Record get() {
        if(record == null) {
            record = fromJson(json);
        }
        return record;
    }

    public ResourceId getClassId() {
        return get().getClassId();
    }

    public String toJson() {
        if(json == null) {
            json = toJson(record);
        }
        return json;
    }


    public void writeToEntity(Entity entity) {
        entity.setProperty(CONTENTS_PROPERTY, new Text(toJson()));
    }

    static Record fromJson(String json) {
        try {
            JsonParser jp = ObjectMapperFactory.get().getFactory().createParser(json);
            jp.nextToken();
            assert jp.getCurrentToken() == JsonToken.START_OBJECT;
            return RecordSerialization.readProperties(jp);
        } catch (Exception e) {
            throw new IllegalStateException("Exception parsing JSON", e);
        }
    }

    @VisibleForTesting
    public static String toJson(Record record) {
        try {
            StringWriter writer = new StringWriter();
            JsonGenerator json = ObjectMapperFactory.get().getFactory().createGenerator(writer);
            json.writeStartObject();
            RecordSerialization.writeProperties(json, record);
            json.writeEndObject();
            json.close();
            return writer.toString();
        } catch (IOException e) {
            throw new RuntimeException("Exception serializing property record: " + e.getMessage(), e);
        }
    }

}
