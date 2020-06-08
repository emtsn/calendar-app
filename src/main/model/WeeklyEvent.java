package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import model.deserializers.WeeklyEventDeserializer;
import utilities.TimeUtility;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;

import static utilities.TimeUtility.*;

@JsonDeserialize(using = WeeklyEventDeserializer.class)
public class WeeklyEvent extends RepeatEvent {

    public WeeklyEvent(String name, String dayOfWeek, String startTime, String endTime) {
        super(name, startTime, endTime, ChronoUnit.WEEKS, DayOfWeek.valueOf(dayOfWeek.toUpperCase()).getValue());
    }

    public WeeklyEvent(String name, DayOfWeek dayOfWeek, LocalTime startTime) {
        this(name, dayOfWeek, startTime, addLocalTimesLimit(startTime, BASE_LENGTH_TIME));
    }

    public WeeklyEvent(String name, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        super(name, startTime, endTime, ChronoUnit.WEEKS, dayOfWeek.getValue());
    }

    // EFFECTS: returns dayOfWeek
    @JsonIgnore
    public DayOfWeek getDayOfWeek() {
        return DayOfWeek.of(getDayOf());
    }

    // EFFECTS: returns the formatted string for the time scale of the event
    @Override
    public String getTimeScaleString() {
        return dowFullFormat(getDayOfWeek());
    }

    // EFFECTS: returns true if the date is of dayOfWeek, false otherwise
    @Override
    public boolean isOnDate(LocalDate date) {
        return date.getDayOfWeek() == getDayOfWeek();
    }

}
