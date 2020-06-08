package model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import model.deserializers.MonthlyEventDeserializer;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;

@JsonDeserialize(using = MonthlyEventDeserializer.class)
public class MonthlyEvent extends RepeatEvent {

    public MonthlyEvent(String name, int dayOfMonth, LocalTime startTime, LocalTime endTime) {
        super(name, startTime, endTime, ChronoUnit.MONTHS, dayOfMonth);
    }

    public MonthlyEvent(String name, int dayOfMonth, String startTime, String endTime) {
        super(name, startTime, endTime, ChronoUnit.MONTHS, dayOfMonth);
    }

    // EFFECTS: returns the formatted string for the time scale of the event
    @Override
    public String getTimeScaleString() {
        return FormatterPattern.ordinal(getDayOf());
    }

    // EFFECTS: returns true if the date is on dayOfMonth, false otherwise
    @Override
    public boolean isOnDate(LocalDate date) {
        return getDayOf() == date.getDayOfMonth();
    }
}
