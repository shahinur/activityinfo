package org.activityinfo.server.endpoint.jsonrpc;

import org.activityinfo.legacy.shared.command.Command;
import org.activityinfo.legacy.shared.command.GetSchema;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.deser.std.StdDeserializer;
import org.codehaus.jackson.node.ObjectNode;

import java.io.IOException;

public class CommandDeserializer extends StdDeserializer<Command> {

    public CommandDeserializer() {
        super(Command.class);
    }

    @Override
    public Command deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        ObjectNode root = (ObjectNode) mapper.readTree(jp);

        String typeName = root.path("type").asText();
        Class commandClass = lookupCommandClass(typeName);

        return (Command) mapper.readValue(root.path("command"), commandClass);
    }

    protected Class<?> lookupCommandClass(String type) {
        try {
            return Class.forName(GetSchema.class.getPackage().getName() + "." + type);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Couldn't resolve command type " + type);
        }
    }
}  