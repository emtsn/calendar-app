package model.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import model.DateEvent;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

public class DateEventDeserializer extends StdDeserializer<DateEvent> {
    public DateEventDeserializer() {
        super(DateEvent.class);
    }

    // EFFECTS: creates a DateEvent from jsonParser
    @Override
    public DateEvent deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String name = node.get("name").asText();
        int startDateYear = node.get("date").get("year").asInt();
        int startDateMonth = node.get("date").get("monthValue").asInt();
        int startDateDay = node.get("date").get("dayOfMonth").asInt();
        int startTimeHour = node.get("startTime").get("hour").asInt();
        int startTimeMinute = node.get("startTime").get("minute").asInt();
        int endTimeHour = node.get("endTime").get("hour").asInt();
        int endTimeMinute = node.get("endTime").get("minute").asInt();
        return new DateEvent(name, LocalDate.of(startDateYear, startDateMonth, startDateDay),
                LocalTime.of(startTimeHour, startTimeMinute), LocalTime.of(endTimeHour, endTimeMinute));
    }
}
