package model.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import model.MonthlyEvent;

import java.io.IOException;
import java.time.LocalTime;

public class MonthlyEventDeserializer extends StdDeserializer<MonthlyEvent> {
    public MonthlyEventDeserializer() {
        super(MonthlyEvent.class);
    }

    // EFFECTS: creates a MonthlyEvent from jsonParser
    @Override
    public MonthlyEvent deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String name = node.get("name").asText();
        int dayOfMonth = node.get("dayOf").asInt();
        int startTimeHour = node.get("startTime").get("hour").asInt();
        int startTimeMinute = node.get("startTime").get("minute").asInt();
        int endTimeHour = node.get("endTime").get("hour").asInt();
        int endTimeMinute = node.get("endTime").get("minute").asInt();
        return new MonthlyEvent(name, dayOfMonth,
                LocalTime.of(startTimeHour, startTimeMinute), LocalTime.of(endTimeHour, endTimeMinute));
    }


}
