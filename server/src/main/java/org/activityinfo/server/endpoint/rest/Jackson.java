package org.activityinfo.server.endpoint.rest;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

import java.io.IOException;
import java.io.StringWriter;

public class Jackson {

    static JsonGenerator createJsonFactory(StringWriter writer) throws IOException {
        JsonFactory jfactory = new JsonFactory();
        JsonGenerator json = jfactory.createJsonGenerator(writer);
        DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
        json.setPrettyPrinter(prettyPrinter);
        return json;
    }

}
