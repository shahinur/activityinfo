package org.activityinfo.server.endpoint.jsonrpc;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.activityinfo.legacy.shared.command.Command;
import org.activityinfo.legacy.shared.command.GetSchema;

import java.io.IOException;

public class CommandDeserializer extends JsonDeserializer<Command> {

    @Override
    public Command deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        ObjectNode root = mapper.readTree(jp);

        String typeName = root.path("type").asText();

        return mapper.treeToValue(root.path("command"), lookupCommandClass(typeName));
    }

    @Override
    public Class<?> handledType() {
        return Command.class;
    }

    protected Class<? extends Command> lookupCommandClass(String type) {
        try {
            return Class.forName(GetSchema.class.getPackage().getName() + "." + type).asSubclass(Command.class);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Couldn't resolve command type " + type);
        }
    }
}  