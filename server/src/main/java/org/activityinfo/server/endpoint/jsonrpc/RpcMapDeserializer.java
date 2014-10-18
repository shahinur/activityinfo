package org.activityinfo.server.endpoint.jsonrpc;

import com.extjs.gxt.ui.client.data.RpcMap;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class RpcMapDeserializer extends JsonDeserializer<RpcMap> {


    @Override
    public RpcMap deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        ObjectNode root = mapper.readTree(jp);

        RpcMap map = new RpcMap();
        Iterator<Map.Entry<String, JsonNode>> fieldIt = root.fields();
        while (fieldIt.hasNext()) {
            Map.Entry<String, JsonNode> field = fieldIt.next();
            if (field.getValue().isNumber()) {
                map.put(field.getKey(), field.getValue().numberValue());
            } else if (field.getValue().isBoolean()) {
                map.put(field.getKey(), field.getValue().asBoolean());
            } else if (field.getValue().isTextual()) {
                map.put(field.getKey(), field.getValue().asText());
            }
        }
        return map;
    }
}
