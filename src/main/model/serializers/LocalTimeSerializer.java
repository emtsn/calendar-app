package model.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.LocalTime;

public class LocalTimeSerializer extends StdSerializer<LocalTime> {
    public LocalTimeSerializer() {
        super(LocalTime.class);
    }

    // MODIFIES: jsonGenerator
    // EFFECTS: writes localTime onto a jsonGenerator
    @Override
    public void serialize(LocalTime localTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("hour", localTime.getHour());
        jsonGenerator.writeNumberField("minute", localTime.getMinute());
        jsonGenerator.writeEndObject();
    }
}
