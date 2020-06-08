package utilities;

import model.DateEvent;
import model.MultiEvent;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public final class EventUtility {
    // REQUIRES: leftIndex >= 0, rightIndex <= list.size() - 1
    // EFFECTS: returns a copy of the list with elements between leftIndex and rightIndex
    public static <T> List<T> trimList(int leftIndex, int rightIndex, List<T> original) {
        List<T> retVal = new ArrayList<>();
        for (int i = leftIndex; i <= rightIndex; i++) {
            retVal.add(original.get(i));
        }
        return retVal;
    }

    // REQUIRES: leftEdge date is before or equal to rightEdge date, eventList is sorted
    // EFFECTS: returns a list of events from eventList that are between leftEdge and rightEdge
    public static <T extends DateEvent> List<T> eventsBetweenDates(LocalDate leftEdge,
                                                                   LocalDate rightEdge, List<T> eventList) {
        List<T> retVal = new ArrayList<>();
        if (eventList.size() == 0 || eventList.get(0).getDate().isAfter(rightEdge)
                || eventList.get(eventList.size() - 1).getDate().isBefore(leftEdge)) {
            return retVal;
        }
        int leftIndex = findLeftIndex(leftEdge, eventList);
        int rightIndex = findRightIndex(leftIndex, rightEdge, eventList);
        if (leftIndex > rightIndex) {
            return retVal;
        }
        return trimList(leftIndex, rightIndex, eventList);
    }


    // REQUIRES: eventList.size() > 0, leftEdge <= highest day in eventList
    // EFFECTS: returns the index of the earliest event in eventList after or equal to leftEdge
    public static <T extends DateEvent> int findLeftIndex(LocalDate leftEdge, List<T> eventList) {
        int start = 0; // start of the current search range
        int end = eventList.size() - 1; // end of the current search range
        while (start < end) {
            int picker = (start + end) / 2;
            LocalDate date = eventList.get(picker).getDate();
            if (date.isBefore(leftEdge)) {
                start = picker + 1;
            } else {
                end = picker;
            }
        }
        return start;
    }

    // REQUIRES: eventList.size() > 0, rightEdge >= lowest day in eventList
    // EFFECTS: returns the index of the latest event in eventList before or equal to rightEdge (or -1 if not found)
    public static <T extends DateEvent> int findRightIndex(int leftIndex, LocalDate rightEdge, List<T> eventList) {
        int start = leftIndex;
        int end = eventList.size() - 1;
        while (start < end) {
            int picker = (start + end) / 2 + 1;
            LocalDate date = eventList.get(picker).getDate();
            if (date.isAfter(rightEdge)) {
                end = picker - 1;
            } else {
                start = picker;
            }
        }
        if (start == end && eventList.get(end).getDate().isAfter(rightEdge)) {
            return -1;
        }
        return end;
    }
}
