package org.activityinfo.ui.store.remote.client.serde;

import com.github.nmorel.gwtjackson.client.stream.JsonReader;
import com.github.nmorel.gwtjackson.client.stream.JsonToken;

import java.io.IOException;
import java.io.StringReader;

public class TestJsonReader implements JsonReader {

    private com.google.gson.stream.JsonReader reader;

    public TestJsonReader(com.google.gson.stream.JsonReader reader) {
        this.reader = reader;
    }

    public TestJsonReader(String json) {
        this(new com.google.gson.stream.JsonReader(new StringReader(json)));
    }

    @Override
    public void setLenient(boolean lenient) {
        reader.setLenient(lenient);
    }

    @Override
    public void beginArray() {
        try {
            reader.beginArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void endArray() {
        try {
            reader.endArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void beginObject() {
        try {
            reader.beginObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void endObject() {
        try {
            reader.endObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean hasNext() {
        try {
            return reader.hasNext();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JsonToken peek() {
        try {
            return translateToken(reader.peek());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private JsonToken translateToken(com.google.gson.stream.JsonToken token) throws IOException {
        return JsonToken.valueOf(token.name());
    }

    @Override
    public String nextName() {
        try {
            return reader.nextName();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String nextString() {
        try {
            return reader.nextString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean nextBoolean() {
        try {
            return reader.nextBoolean();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void nextNull() {
        try {
            reader.nextNull();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public double nextDouble() {
        try {
            return reader.nextDouble();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long nextLong() {
        try {
            return reader.nextLong();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int nextInt() {
        try {
            return reader.nextInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        try {
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void skipValue() {
        try {
            reader.skipValue();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String nextValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getLineNumber() {
        return 1;
    }

    @Override
    public int getColumnNumber() {
        return 0;
    }

    @Override
    public String getInput() {
        throw new UnsupportedOperationException();
    }
}
