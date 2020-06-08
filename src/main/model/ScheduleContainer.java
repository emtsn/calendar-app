package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import utilities.EventUtility;
import utilities.TimeUtility;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class ScheduleContainer {
    private List<DateEvent> dateEvents;
    private List<RepeatEvent> repeatEvents;
    private Map<RepeatKey, List<RepeatEvent>> repeatEventsMap;

    public ScheduleContainer() {
        dateEvents = new ArrayList<>();
        repeatEvents = new ArrayList<>();
        repeatEventsMap = new HashMap<>();
    }

    // EFFECTS: returns true if event with eventName is in eventList
    public static <T extends ScheduleEvent> boolean eventsContains(List<T> eventList, String eventName) {
        return eventsGet(eventList, eventName) != null;
    }

    // EFFECTS: returns the first event with eventName in eventList
    public static <T extends ScheduleEvent> T eventsGet(List<T> eventList, String eventName) {
        for (T t : eventList) {
            if (eventName.equals(t.getName())) {
                return t;
            }
        }
        return null;
    }

    // MODIFIES: this
    // EFFECTS: clears all events
    @JsonIgnore
    public void clearScheduleEvents() {
        dateEvents.clear();
        repeatEvents.clear();
        repeatEventsMap.clear();
    }

    // EFFECTS: returns all events as a list of schedule events
    @JsonIgnore
    public List<ScheduleEvent> getScheduleEvents() {
        List<ScheduleEvent> scheduleEvents = new ArrayList<>();
        scheduleEvents.addAll(repeatEvents);
        scheduleEvents.addAll(dateEvents);
        return scheduleEvents;
    }

    // EFFECTS: returns dateEvents
    public List<DateEvent> getDateEvents() {
        return dateEvents;
    }

    // EFFECTS: returns repeatEvents
    public List<RepeatEvent> getRepeatEvents() {
        return repeatEvents;
    }

    // MODIFIES: this
    // EFFECTS: copies all events from dateEvents
    public void setDateEvents(List<DateEvent> dateEvents) {
        this.dateEvents.clear();
        addDateEvents(dateEvents);
    }

    // MODIFIES; this
    // EFFECTS; copies all events from repeatEvents
    public void setRepeatEvents(List<RepeatEvent> repeatEvents) {
        this.repeatEvents.clear();
        this.repeatEventsMap.clear();
        addRepeatEvents(repeatEvents);
    }

    // REQUIRES: index >= 0 and index < dateEvents.size()
    // EFFECTS: returns event at index of dateEvents
    @JsonIgnore
    public DateEvent getDateEvent(int index) {
        return dateEvents.get(index);
    }

    // REQUIRES: index >= 0 and index < repeatEvents.size()
    // EFFECTS: returns event at index of repeatEvents
    @JsonIgnore
    public RepeatEvent getRepeatEvent(int index) {
        return repeatEvents.get(index);
    }

    // MODIFIES: this
    // EFFECTS: adds d to dateEvents
    @JsonIgnore
    public void addEvent(DateEvent d) {
        dateEvents.add(d);
        dateEvents.sort(null);
    }

    // MODIFIES: this
    // EFFECTS: adds r to repeatEvents
    @JsonIgnore
    public void addEvent(RepeatEvent r) {
        repeatEvents.add(r);
        repeatEvents.sort(null);
        List<RepeatEvent> eventsList = repeatEventsMap.computeIfAbsent(
                r.getRepeatKey(), k -> new ArrayList<>());
        eventsList.add(r);
    }

    // MODIFIES: this
    // EFFECTS: adds all event to dateEvents
    public void addDateEvents(List<DateEvent> events) {
        for (DateEvent event : events) {
            addEvent(event);
        }
    }

    // MODIFIES: this
    // EFFECTS: adds all events to repeatEvents
    public void addRepeatEvents(List<RepeatEvent> events) {
        for (RepeatEvent event : events) {
            addEvent(event);
        }
    }

    // MODIFIES: this
    // EFFECTS: removes the first appearance of dateEvent from dateEvents
    public boolean removeEvent(DateEvent dateEvent) {
        int index = Collections.binarySearch(dateEvents, dateEvent);
        if (index >= 0) {
            dateEvents.remove(index);
            return true;
        } else {
            return false;
        }
    }

    // MODIFIES: this
    // EFFECTS: removes the first appearance of repeatEvent from repeatEvents
    public boolean removeEvent(RepeatEvent repeatEvent) {
        List<RepeatEvent> eventsList = repeatEventsMap.get(repeatEvent.getRepeatKey());
        if (eventsList != null) {
            if (eventsList.remove(repeatEvent)) {
                if (eventsList.size() < 1) {
                    repeatEventsMap.remove(repeatEvent.getRepeatKey());
                }
                int index = Collections.binarySearch(repeatEvents, repeatEvent);
                assert index >= 0;
                repeatEvents.remove(index);
                return true;
            }
        }
        return false;
    }

    // EFFECTS: returns size of all events together
    @JsonIgnore
    public int getSize() {
        return dateEvents.size() + repeatEvents.size();
    }

    // EFFECTS: returns size of dateEvents
    @JsonIgnore
    public int getDateEventsSize() {
        return dateEvents.size();
    }

    // EFFECTS: returns size of repeatEvents
    @JsonIgnore
    public int getRepeatEventsSize() {
        return repeatEvents.size();
    }

    // EFFECTS: returns true if event with eventName exists in dateEvents, false otherwise
    @JsonIgnore
    public boolean dateEventsContains(String eventName) {
        return eventsContains(dateEvents, eventName);
    }

    // EFFECTS: returns true if event with eventName exists in repeatEvents, false otherwise
    @JsonIgnore
    public boolean repeatEventsContains(String eventName) {
        return eventsContains(repeatEvents, eventName);
    }

    // MODIFIES: this
    // EFFECTS: sorts all events by time, ascending order
    @JsonIgnore
    public void sort() {
        dateEvents.sort(null);
        repeatEvents.sort(null);
    }

    // EFFECTS: returns a list of all dateEvents in schedule that are in yearMonth
    @JsonIgnore
    public List<DateEvent> getDateEventsForYearMonth(YearMonth yearMonth) {
        return EventUtility.eventsBetweenDates(yearMonth.atDay(1), yearMonth.atEndOfMonth(), dateEvents);
    }

    // EFFECTS: returns an array of booleans for a month, where values correspond to whether there is a event on the day
    @JsonIgnore
    public boolean[] hasEvents(YearMonth yearMonth, boolean checkDateEvents, boolean checkRepeatEvents) {
        boolean[] retVal = new boolean[yearMonth.lengthOfMonth()];
        if (checkDateEvents) {
            setHasEventsForDate(yearMonth, retVal);
        }
        if (checkRepeatEvents) {
            setHasEventsForRepeat(yearMonth, retVal);
        }
        return retVal;
    }

    // MODIFIES: hasEvents
    // EFFECTS: for the month, sets the value of hasEvents at date to true if there is a date event on that date
    @JsonIgnore
    private void setHasEventsForDate(YearMonth yearMonth, boolean[] hasEvents) {
        List<DateEvent> dateEventsForMonth = getDateEventsForYearMonth(yearMonth);
        for (DateEvent dateEvent : dateEventsForMonth) {
            hasEvents[dateEvent.getDate().getDayOfMonth() - 1] = true;
        }
    }

    // MODIFIES: hasEvents
    // EFFECTS: for the month, sets the value of hasEvents at date to true if there is a repeat event on that date
    @JsonIgnore
    private void setHasEventsForRepeat(YearMonth yearMonth, boolean[] hasEvents) {
        for (RepeatKey key : repeatEventsMap.keySet()) {
            if (key.getTimeScale().equals(ChronoUnit.MONTHS)) {
                if (yearMonth.lengthOfMonth() >= key.getDayOf()) {
                    hasEvents[key.getDayOf() - 1] = true;
                }
            } else if (key.getTimeScale().equals(ChronoUnit.WEEKS)) {
                int diff = TimeUtility.daysBetweenDow(yearMonth.atDay(1).getDayOfWeek(),
                        DayOfWeek.of(key.getDayOf()));
                for (int i = 0; i < 6; i++) {
                    int num = (7 * i) + diff;
                    if (num < yearMonth.lengthOfMonth()) {
                        hasEvents[num] = true;
                    }
                }
            }
        }
    }

    // EFFECTS: returns true if any date event exist at the same time as another date event
    @JsonIgnore
    public boolean hasDateEventConflict() {
        List<DateEvent> tempDateEvents = new ArrayList<>(dateEvents);
        tempDateEvents.sort(null);
        for (int i = 0; i < tempDateEvents.size(); i++) {
            DateEvent de = tempDateEvents.get(i);
            if (i < tempDateEvents.size() - 1) {
                DateEvent deAfter = tempDateEvents.get(i + 1);
                if (!deAfter.getStartDateTime().isAfter(de.getEndDateTime())) {
                    return true;
                }
            }
        }
        return false;
    }


    // REQUIRES: startDate <= endDate
    // EFFECTS: returns a list of dateEvents for date
    @JsonIgnore
    public List<DateEvent> getDateEventsBetweenDates(LocalDate startDate, LocalDate endDate) {
        return EventUtility.eventsBetweenDates(startDate, endDate, dateEvents);
    }

    // EFFECTS: returns a list of repeatEvents for date
    @JsonIgnore
    public List<RepeatEvent> getRepeatEventsForDate(LocalDate date) {
        List<RepeatEvent> retVal = new ArrayList<>();
        for (Map.Entry<RepeatKey, List<RepeatEvent>> entry : repeatEventsMap.entrySet()) {
            if (entry.getKey().getTimeScale().equals(ChronoUnit.MONTHS)) {
                if (date.getDayOfMonth() == entry.getKey().getDayOf()) {
                    retVal.addAll(entry.getValue());
                }
            } else if (entry.getKey().getTimeScale().equals(ChronoUnit.WEEKS)) {
                if (date.getDayOfWeek().getValue() == entry.getKey().getDayOf()) {
                    retVal.addAll(entry.getValue());
                }
            }
        }
        return retVal;
    }

    // REQUIRES: startDate <= endDate
    // EFFECTS: returns a list of repeatEvents between startDate and endDate
    @JsonIgnore
    public List<RepeatEvent> getRepeatEventsBetweenDates(LocalDate startDate, LocalDate endDate) {
        LocalDate afterEnd;
        if (endDate.equals(LocalDate.MAX)) {
            afterEnd = endDate;
            if (startDate.equals(LocalDate.MIN)) {
                return repeatEvents;
            }
        } else {
            afterEnd = endDate.plusDays(1);
        }
        boolean includeAllMonth = ChronoUnit.MONTHS.between(startDate, afterEnd) >= 1;
        boolean sameMonth = startDate.getMonthValue() == endDate.getMonthValue();
        boolean includeAllWeek = ChronoUnit.WEEKS.between(startDate, afterEnd) >= 1;
        boolean sameWeek = startDate.getDayOfWeek().getValue() <= endDate.getDayOfWeek().getValue();
        return getRepeatEventsBetweenDates(startDate, endDate, includeAllMonth, includeAllWeek, sameMonth, sameWeek);
    }

    // REQUIRES: startDate <= endDate, sameMonth and sameWeek are accurate to the dates
    // EFFECTS: returns a list of repeatEvents between startDate and endDate
    @JsonIgnore
    public List<RepeatEvent> getRepeatEventsBetweenDates(LocalDate startDate, LocalDate endDate,
                                                         boolean includeAllMonth, boolean includeAllWeek,
                                                         boolean sameMonth, boolean sameWeek) {
        List<RepeatEvent> retVal = new ArrayList<>();
        for (Map.Entry<RepeatKey, List<RepeatEvent>> entry : repeatEventsMap.entrySet()) {
            if (entry.getKey().getTimeScale().equals(ChronoUnit.MONTHS)) {
                if (includeAllMonth || TimeUtility.isBetweenLoop(entry.getKey().getDayOf(),
                        startDate.getDayOfMonth(), endDate.getDayOfMonth(), sameMonth)) {
                    retVal.addAll(entry.getValue());
                }
            } else if (entry.getKey().getTimeScale().equals(ChronoUnit.WEEKS)) {
                if (includeAllWeek || TimeUtility.isBetweenLoop(entry.getKey().getDayOf(),
                        startDate.getDayOfWeek().getValue(), endDate.getDayOfWeek().getValue(), sameWeek)) {
                    retVal.addAll(entry.getValue());
                }
            }
        }
        return retVal;
    }
}
