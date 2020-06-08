package model.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import model.WeeklyEvent;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalTime;

public class WeeklyEventDeserializer extends StdDeserializer<WeeklyEvent> {
    public WeeklyEventDeserializer() {
        super(WeeklyEvent.class);
    }

    // EFFECTS: creates a WeeklyEvent from jsonParser
    @Override
    public WeeklyEvent deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String name = node.get("name").asText();
        int dayOfWeek = node.get("dayOf").asInt();
        int startTimeHour = node.get("startTime").get("hour").asInt();
        int startTimeMinute = node.get("startTime").get("minute").asInt();
        int endTimeHour = node.get("endTime").get("hour").asInt();
        int endTimeMinute = node.get("endTime").get("minute").asInt();
        return new WeeklyEvent(name, DayOfWeek.of(dayOfWeek),
                LocalTime.of(startTimeHour, startTimeMinute), LocalTime.of(endTimeHour, endTimeMinute));
    }

}
