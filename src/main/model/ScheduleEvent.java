package model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static utilities.TimeUtility.*;

public abstract class ScheduleEvent implements FormatterPattern, Comparable<ScheduleEvent> {
    public static final LocalTime BASE_LENGTH_TIME = LocalTime.of(1,0);

    protected String name;
    protected LocalTime startTime;
    protected LocalTime endTime;

    // EFFECTS: converts string to time and calls constructor
    public ScheduleEvent(String name, String startTime, String endTime) {
        this(name, LocalTime.parse(startTime, TIME_FORMATTER), LocalTime.parse(endTime, TIME_FORMATTER));
    }

    // EFFECTS: create ScheduleEvent with name, startTime, and endTime
    //          if startTime is later than endTime, then they will be switched
    public ScheduleEvent(String name, LocalTime startTime, LocalTime endTime) {
        this.name = name;
        if (!startTime.isAfter(endTime)) {
            this.startTime = startTime;
            this.endTime = endTime;
        } else {
            this.startTime = endTime;
            this.endTime = startTime;
        }
    }

    // MODIFIES: this
    // EFFECTS: changes name of event
    public void setName(String name) {
        this.name = name;
    }

    // EFFECTS: returns name of event
    public String getName() {
        return name;
    }

    // EFFECTS: returns start time of event
    public LocalTime getStartTime() {
        return startTime;
    }

    // EFFECTS: returns length time of event
    @JsonIgnore
    public LocalTime getLengthTime() {
        return diffLocalTimes(startTime, endTime);
    }

    // EFFECTS: returns end time of event
    public LocalTime getEndTime() {
        return endTime;
    }


    // EFFECTS: returns true if event is on dateTime, false otherwise
    public boolean isOnDateTime(LocalDateTime dateTime) {
        return isOnDate(dateTime.toLocalDate()) && isOnTime(dateTime.toLocalTime());
    }

    // EFFECTS: returns true if event is on date, false otherwise
    public abstract boolean isOnDate(LocalDate date);

    // EFFECTS: returns true if event is on time, false otherwise
    public boolean isOnTime(LocalTime time) {
        return isBetweenTime(time, startTime, getEndTime());
    }

    // EFFECTS: returns true if event takes the whole day
    @JsonIgnore
    public boolean isFullDay() {
        return startTime.equals(LocalTime.of(0,0)) && endTime.equals(LocalTime.of(23,59));
    }

    // EFFECTS: returns time scale of event in string
    @JsonIgnore
    public abstract String getTimeScaleString();

    // EFFECTS: returns only the time of the event in string
    @JsonIgnore
    public String getTimeOnlyString() {
        if (isFullDay()) {
            return "";
        }
        if (startTime.equals(endTime)) {
            return startTime.format(TIME_FORMATTER);
        }
        return startTime.format(TIME_FORMATTER) + "~" + endTime.format(TIME_FORMATTER);
    }

    // EFFECTS: returns time (hour/minutes) + defined time scale of event in string
    @JsonIgnore
    public String getTimeString() {
        return getTimeScaleString() + (isFullDay() ? "" : " ") + getTimeOnlyString();
    }

    // EFFECTS: returns true if this event and otherEvent has the same name, false otherwise
    public boolean checkSameName(ScheduleEvent otherEvent) {
        return getName().equals(otherEvent.getName());
    }

}
