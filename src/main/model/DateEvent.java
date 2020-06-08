package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import model.deserializers.DateEventDeserializer;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static utilities.TimeUtility.*;

@JsonDeserialize(using = DateEventDeserializer.class)
public class DateEvent extends ScheduleEvent {
    private LocalDate date;

    public DateEvent(String name, LocalDate date, LocalTime startTime, LocalTime endTime) {
        super(name, startTime, endTime);
        this.date = date;
    }

    public DateEvent(String name, LocalDateTime startDateTime) {
        this(name, startDateTime.toLocalDate(), startDateTime.toLocalTime(),
                addLocalTimesLimit(startDateTime.toLocalTime(), BASE_LENGTH_TIME));
    }

    public DateEvent(String name, String date, String startTime, String endTime) {
        super(name, startTime, endTime);
        this.date = LocalDate.parse(date, DATE_FORMATTER);
    }

    // EFFECTS: returns date
    public LocalDate getDate() {
        return date;
    }

    // EFFECTS: return day of week of date
    @JsonIgnore
    public DayOfWeek getDayOfWeek() {
        return date.getDayOfWeek();
    }

    // EFFECTS: returns the formatted string for the time scale of the event
    @Override
    public String getTimeScaleString() {
        return getStartDateTime().format(DATE_FORMATTER);
    }

    // EFFECTS: returns true if date is on this.date
    @Override
    public boolean isOnDate(LocalDate date) {
        return date.isEqual(this.date);
    }

    // EFFECTS: returns start date time of event
    @JsonIgnore
    public LocalDateTime getStartDateTime() {
        return LocalDateTime.of(date, startTime);
    }

    // EFFECTS: returns end date time of event
    @JsonIgnore
    public LocalDateTime getEndDateTime() {
        return LocalDateTime.of(date, endTime);
    }

    @Override
    public int compareTo(ScheduleEvent o) {
        DateEvent other = (DateEvent) o;
        return getStartDateTime().compareTo(other.getStartDateTime());
    }
}
