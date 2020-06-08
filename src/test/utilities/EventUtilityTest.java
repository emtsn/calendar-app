package utilities;

import model.DateEvent;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EventUtilityTest {

    // EFFECTS: runs tests for whether list elements in expected match actual
    private static <T> void listEquals(List<T> expected, List<T> actual) {
        if (expected.size() != actual.size()) {
            fail("Size difference");
        }
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), actual.get(i));
        }
    }

    // EFFECTS: runs tests for whether array elements in expected match actual
    private static <T> void listEqualsArray(T[] expected, List<T> actual) {
        if (expected.length != actual.size()) {
            fail("Size difference");
        }
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual.get(i));
        }
    }

    private void addNumbersToList(List<Integer> list) {
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
    }

    private DateEvent createDateEvent(String name, LocalDate date) {
        return new DateEvent(name, date, LocalTime.NOON, LocalTime.NOON);
    }

    private void addLotsDateEvents(List<DateEvent> dateEvents) {
        dateEvents.add(createDateEvent("0", LocalDate.of(1950, 1,2)));
        dateEvents.add(createDateEvent("1", LocalDate.of(1961, 4,3)));
        dateEvents.add(createDateEvent("2", LocalDate.of(1967, 12,20)));
        dateEvents.add(createDateEvent("3", LocalDate.of(1967, 12,20)));
        dateEvents.add(createDateEvent("4", LocalDate.of(1978, 4,14)));
        dateEvents.add(createDateEvent("5", LocalDate.of(1979, 7,13)));
        dateEvents.add(createDateEvent("6", LocalDate.of(1979, 10,6)));
        dateEvents.add(createDateEvent("7", LocalDate.of(1979, 12,18)));
        dateEvents.add(createDateEvent("8", LocalDate.of(1982, 5,31)));
        dateEvents.add(createDateEvent("9", LocalDate.of(1990, 3,1)));
        dateEvents.add(createDateEvent("10", LocalDate.of(1990, 12,31)));
        dateEvents.add(createDateEvent("11", LocalDate.of(1991, 1,1)));
        dateEvents.add(createDateEvent("12", LocalDate.of(2014, 6,5)));
        dateEvents.add(createDateEvent("13", LocalDate.of(2014, 6,5)));
        dateEvents.add(createDateEvent("14", LocalDate.of(2014, 6,5)));
        dateEvents.add(createDateEvent("15", LocalDate.of(2060, 9,17)));
    }

    @Test
    void testTrim() {
        List<Integer> listA = new ArrayList<>();
        listA.add(1);
        listEquals(listA, EventUtility.trimList(0, 0, listA));
        listA.clear();

        addNumbersToList(listA);
        listEqualsArray(new Integer[]{1, 2}, EventUtility.trimList(0, 1, listA));
        listEqualsArray(new Integer[]{1, 2, 3, 4, 5}, listA);
        listA.clear();

        addNumbersToList(listA);
        listEqualsArray(new Integer[]{3, 4, 5}, EventUtility.trimList(2, 4, listA));
        listEqualsArray(new Integer[]{1, 2, 3, 4, 5}, listA);
        listA.clear();

        addNumbersToList(listA);
        listEqualsArray(new Integer[]{2, 3, 4}, EventUtility.trimList(1, 3, listA));
        listEqualsArray(new Integer[]{1, 2, 3, 4, 5}, listA);
        listA.clear();
    }

    @Test
    void testEventsBetweenDatesNothing() {
        // for empty date events
        List<DateEvent> dateEvents = new ArrayList<>();
        List<DateEvent> returnValue = EventUtility.eventsBetweenDates(
                LocalDate.of(2000, 1, 1),
                LocalDate.of(2000, 1, 2), dateEvents);
        assertEquals(0, dateEvents.size());
        assertEquals(0, returnValue.size());

        // for date values before every date event
        dateEvents.clear();
        addLotsDateEvents(dateEvents);
        returnValue = EventUtility.eventsBetweenDates(
                LocalDate.of(1870, 6, 2),
                LocalDate.of(1950, 1,1), dateEvents);
        assertEquals(0, returnValue.size());
        assertEquals(16, dateEvents.size());

        // for date values after every date event
        returnValue = EventUtility.eventsBetweenDates(
                LocalDate.of(2060, 9, 18),
                LocalDate.of(2070, 4,5), dateEvents);
        assertEquals(0, returnValue.size());
        assertEquals(16, dateEvents.size());

        // for date values between when there are no events
        returnValue = EventUtility.eventsBetweenDates(
                LocalDate.of(1980, 1, 1),
                LocalDate.of(1981, 1,1), dateEvents);
        assertEquals(0, returnValue.size());

        // edge case, for date values between when there are no events
        returnValue = EventUtility.eventsBetweenDates(
                LocalDate.of(1991, 1, 2),
                LocalDate.of(2014, 6,4), dateEvents);
        assertEquals(0, returnValue.size());
    }

    @Test
    void testEventsBetweenDates() {
        testEventsBetweenFor(false);
        testEventsBetweenFor(true);
    }

    private void testEventsBetweenFor(boolean odd) {
        List<DateEvent> dateEvents = new ArrayList<>();
        addLotsDateEvents(dateEvents);
        if (odd) {
            dateEvents.add(createDateEvent("16", LocalDate.of(2070, 1, 1)));
        }
        List<DateEvent> returnValue;

        // all the dates
        returnValue = EventUtility.eventsBetweenDates(
                dateEvents.get(0).getDate(),
                dateEvents.get(dateEvents.size() - 1).getDate(), dateEvents);
        listEquals(dateEvents, returnValue);

        // beyond all the dates
        returnValue = EventUtility.eventsBetweenDates(
                dateEvents.get(0).getDate().minusDays(15),
                dateEvents.get(dateEvents.size() - 1).getDate().plusMonths(1), dateEvents);
        listEquals(dateEvents, returnValue);

        // edge case, left edge and below
        returnValue = EventUtility.eventsBetweenDates(
                dateEvents.get(0).getDate().minusDays(1),
                dateEvents.get(0).getDate().plusDays(1), dateEvents);
        listEqualsArray(new DateEvent[]{dateEvents.get(0)}, returnValue);

        // edge case, right edge and above
        returnValue = EventUtility.eventsBetweenDates(
                dateEvents.get(dateEvents.size() - 1).getDate().minusDays(1),
                dateEvents.get(dateEvents.size() - 1).getDate().plusWeeks(10), dateEvents);
        listEquals(EventUtility.trimList(dateEvents.size() - 1, dateEvents.size() - 1, dateEvents),
                returnValue);

        // one date but many
        returnValue = EventUtility.eventsBetweenDates(
                dateEvents.get(12).getDate().minusDays(0),
                dateEvents.get(12).getDate().plusDays(1), dateEvents);
        listEquals(EventUtility.trimList(12, 14, dateEvents), returnValue);

        // lots of different dates
        returnValue = EventUtility.eventsBetweenDates(
                LocalDate.of(1970, 1,1),
                LocalDate.of(2000,1,1), dateEvents);
        listEquals(EventUtility.trimList(4, 11, dateEvents), returnValue);
    }
}
