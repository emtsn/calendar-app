package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = WeeklyEvent.class, name = "Weekly"),
        @JsonSubTypes.Type(value = MonthlyEvent.class, name = "Monthly"),
})
public abstract class RepeatEvent extends ScheduleEvent {
    private RepeatKey repeatKey;

    public RepeatEvent(String name, LocalTime startTime, LocalTime endTime,
                       ChronoUnit timeScale, int dayOf) {
        super(name, startTime, endTime);
        repeatKey = new RepeatKey(timeScale, dayOf);
    }

    public RepeatEvent(String name, String startTime, String endTime,
                       ChronoUnit timeScale, int dayOf) {
        super(name, startTime, endTime);
        repeatKey = new RepeatKey(timeScale, dayOf);
    }

    // EFFECTS: returns the repeat key
    @JsonIgnore
    public RepeatKey getRepeatKey() {
        return repeatKey;
    }

    // EFFECTS: return day of a time scale
    public int getDayOf() {
        return repeatKey.getDayOf();
    }

    // EFFECTS: returns timeScale
    @JsonIgnore
    public ChronoUnit getTimeScale() {
        return repeatKey.getTimeScale();
    }

    @Override
    public int compareTo(ScheduleEvent o) {
        RepeatEvent other = (RepeatEvent) o;
        int compareTimeScale = getTimeScale().compareTo(other.getTimeScale());
        if (compareTimeScale != 0) {
            return compareTimeScale;
        }
        int compareDayOf = Integer.compare(getDayOf(), other.getDayOf());
        if (compareDayOf != 0) {
            return compareDayOf;
        }
        return getStartTime().compareTo(other.getStartTime());
    }
}
