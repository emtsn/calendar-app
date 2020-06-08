package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static utilities.TimeUtility.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DateEventTest implements FormatterPattern {

    private LocalDateTime dt1;
    private LocalDateTime dt2;
    private LocalDateTime dt3;
    private LocalDateTime dt4;
    private LocalTime t1;
    private DateEvent s1;
    private DateEvent s2;
    private DateEvent s3;
    private DateEvent s4;

    @BeforeEach
    void runBefore() {
        dt1 = LocalDateTime.of(1987, 4,2,4,30);
        dt2 = LocalDateTime.of(1211, 4,17,7,14);
        dt3 = LocalDateTime.of(2011,5,30,2,30);
        dt4 = LocalDateTime.of(2014,10,3,21,37);
        t1 = LocalTime.of(5, 0);
        s3 = new DateEvent("Walk at the park", dt3.toLocalDate().format(DATE_FORMATTER),
                dt3.toLocalTime().format(TIME_FORMATTER), t1.format(TIME_FORMATTER));
        s1 = new DateEvent("Walk the dog", dt1);
        s4 = new DateEvent("", dt4);
        s2 = new DateEvent("Walk the dog", dt2.toLocalDate(), dt2.toLocalTime(),
                addLocalTimesLimit(dt2.toLocalTime(), LocalTime.of(1, 45)));
    }

    @Test
    void testGetStartDateTime() {
        assertEquals(dt1, s1.getStartDateTime());
        assertEquals(dt2, s2.getStartDateTime());
    }

    @Test
    void testGetEndDateTime() {
        assertEquals(dt4.plusHours(1), s4.getEndDateTime());
        assertEquals(dt3.plusHours(2).plusMinutes(30), s3.getEndDateTime());
    }

    @Test
    void testGetDate() {
        assertEquals(dt3.toLocalDate(), s3.getDate());
        assertEquals(dt4.toLocalDate(), s4.getDate());
    }

    @Test
    void testGetDayOfWeek() {
        assertEquals(s1.getDayOfWeek(), DayOfWeek.THURSDAY);
        assertEquals(s3.getDayOfWeek(), DayOfWeek.MONDAY);
    }

    @Test
    void testIsOnDate() {
        assertTrue(s2.isOnDate(dt2.toLocalDate()));
        assertFalse(s3.isOnDate(dt4.toLocalDate()));
    }

    @Test
    void testGetDateTimeString() {
        assertEquals(dt1.format(DateEvent.DATE_TIME_FORMATTER) + "~"
                + LocalTime.of(5, 30).format(TIME_FORMATTER), s1.getTimeString());
        assertEquals(dt3.format(DateEvent.DATE_TIME_FORMATTER) + "~"
                + LocalTime.of(5, 0).format(TIME_FORMATTER), s3.getTimeString());
        DateEvent fullDay = new DateEvent("full day", dt2.toLocalDate(),
                LocalTime.of(0,0), LocalTime.of(23,59));
        assertEquals(dt2.toLocalDate().format(DateEvent.DATE_FORMATTER), fullDay.getTimeString());
        DateEvent sameTime = new DateEvent("same time", dt2.toLocalDate(), LocalTime.NOON, LocalTime.NOON);
        assertEquals(dt2.toLocalDate().format(DateEvent.DATE_FORMATTER) + " "
                + LocalTime.NOON.format(DateEvent.TIME_FORMATTER), sameTime.getTimeString());
    }
}
