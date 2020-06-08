package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WeeklyEventTest implements FormatterPattern{

    private WeeklyEvent w1;
    private WeeklyEvent w2;
    private WeeklyEvent w3;
    private WeeklyEvent w4;

    @BeforeEach
    void runBefore() {
        w1 = new WeeklyEvent("CS Class", DayOfWeek.MONDAY,
                LocalTime.of(11, 0), LocalTime.of(11,50));
        w2 = new WeeklyEvent("CS Class", "WEDNESDAY",LocalTime.of(11, 0).format(TIME_FORMATTER),
                LocalTime.of(11,50).format(TIME_FORMATTER));
        w3 = new WeeklyEvent("Walk the dog", DayOfWeek.SUNDAY, LocalTime.of(10,0));
        w4 = new WeeklyEvent("Work at McDonald's", DayOfWeek.TUESDAY,
                LocalTime.of(10,0));
    }

    @Test
    void testGetDayOfWeek() {
        assertEquals(DayOfWeek.MONDAY, w1.getDayOfWeek());
        assertEquals(DayOfWeek.SUNDAY, w3.getDayOfWeek());
    }

    @Test
    void testGetTimeString() {
        assertEquals("Monday 11:00~11:50", w1.getTimeString());
        assertEquals("Wednesday 11:00~11:50", w2.getTimeString());
    }

    @Test
    void testIsOnDate() {
        assertTrue(w1.isOnDate(LocalDate.of(2019,9,30)));
        assertFalse(w2.isOnDate(LocalDate.of(2019,9,30)));
    }
}
