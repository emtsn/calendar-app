package model.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.awt.*;
import java.io.IOException;

public class ColorSerializer extends StdSerializer<Color> {
    public ColorSerializer() {
        super(Color.class);
    }

    // MODIFIES: jsonGenerator
    // EFFECTS: writes color onto a jsonGenerator
    @Override
    public void serialize(Color color, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("red", color.getRed());
        jsonGenerator.writeNumberField("blue", color.getBlue());
        jsonGenerator.writeNumberField("green", color.getGreen());
        jsonGenerator.writeNumberField("alpha", color.getAlpha());
        jsonGenerator.writeEndObject();
    }
}
