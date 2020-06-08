package model.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import model.DateEvent;
import model.MultiEvent;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MultiEventDeserializer extends StdDeserializer<MultiEvent> {
    public MultiEventDeserializer() {
        super(MultiEvent.class);
    }

    // EFFECTS: creates a DateEvent from jsonParser
    @Override
    public MultiEvent deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
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
        List<String> otherNames = new ObjectMapper().convertValue(node.get("otherNames"), ArrayList.class);
        MultiEvent newMultiEvent = new MultiEvent(name, LocalDate.of(startDateYear, startDateMonth, startDateDay),
                LocalTime.of(startTimeHour, startTimeMinute), LocalTime.of(endTimeHour, endTimeMinute));
        newMultiEvent.setOtherNames(otherNames);
        return newMultiEvent;
    }
}
