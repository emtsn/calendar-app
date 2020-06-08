package model.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.awt.*;
import java.io.IOException;

public class ColorDeserializer extends StdDeserializer<Color> {
    public ColorDeserializer() {
        super(Color.class);
    }

    @Override
    public Color deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException, JsonProcessingException {
        JsonNode colorNode = jsonParser.getCodec().readTree(jsonParser);
        return new Color(colorNode.get("red").asInt(), colorNode.get("green").asInt(),
                colorNode.get("blue").asInt(), colorNode.get("alpha").asInt());
    }

}
