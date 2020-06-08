package model.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.LocalDate;

public class LocalDateSerializer extends StdSerializer<LocalDate> {
    public LocalDateSerializer() {
        super(LocalDate.class);
    }

    // MODIFIES: jsonGenerator
    // EFFECTS: writes localDate onto a jsonGenerator
    @Override
    public void serialize(LocalDate localDate, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("year", localDate.getYear());
        jsonGenerator.writeNumberField("monthValue", localDate.getMonthValue());
        jsonGenerator.writeNumberField("dayOfMonth", localDate.getDayOfMonth());
        jsonGenerator.writeEndObject();
    }
}
