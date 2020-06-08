package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import model.deserializers.MultiEventDeserializer;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@JsonDeserialize(using = MultiEventDeserializer.class)
public class MultiEvent extends DateEvent {
    private List<String> otherNames;

    public MultiEvent(String name, LocalDate date, LocalTime startTime, LocalTime endTime) {
        super(name, date, startTime, endTime);
        otherNames = new ArrayList<>();
    }

    // REQUIRES: events is sorted
    // MODIFIES: events
    // EFFECTS: merge or splits multiEvents
    public static void mergeOrSplitEvents(List<MultiEvent> events, boolean merge) {
        if (merge) {
            mergeEvents(events);
        } else {
            splitEvents(events);
        }
    }

    // REQUIRES: events is sorted
    // MODIFIES: events
    // EFFECTS: merge multiEvents from a list of multiEvents if they are on the same date
    public static void mergeEvents(List<MultiEvent> events) {
        for (int i = events.size() - 1; i > 0; i--) {
            MultiEvent curr = events.get(i);
            MultiEvent prev = events.get(i - 1);
            if (prev.isOnDate(curr.getDate())) {
                prev.addLink(curr);
                events.remove(i);
            }
        }
    }

    // MODIFIES: events
    // EFFECTS: split list of multiEvents
    public static void splitEvents(List<MultiEvent> events) {
        List<MultiEvent> oldEvents = new ArrayList<>(events);
        events.clear();
        for (MultiEvent event : oldEvents) {
            events.add(event);
            events.addAll(event.split());
        }
    }

    // EFFECTS: returns all of the other names
    public List<String> getOtherNames() {
        return otherNames;
    }

    // EFFECTS: sets all of the other names
    public void setOtherNames(List<String> otherNames) {
        this.otherNames = otherNames;
    }

    // EFFECTS: gets the names of all the multiEvents together
    @JsonIgnore
    public String getMergedName() {
        String retVal = getName();
        for (String otherName : otherNames) {
            retVal = retVal.concat(", " + otherName);
        }
        return retVal;
    }

    // MODIFIES: this
    // EFFECTS: adds a multiEvent to this event
    @JsonIgnore
    public void addLink(MultiEvent other) {
        otherNames.add(other.name);
        otherNames.addAll(other.otherNames);
    }

    // MODIFIES: this
    // EFFECTS: returns all of the multiEvents and clears multiEvents
    @JsonIgnore
    public List<MultiEvent> split() {
        List<MultiEvent> retVal = new ArrayList<>();
        for (String otherName : otherNames) {
            retVal.add(new MultiEvent(otherName, getDate(), getStartTime(), getEndTime()));
        }
        otherNames.clear();
        return retVal;
    }
}
