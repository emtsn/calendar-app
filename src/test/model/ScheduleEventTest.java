package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static utilities.TimeUtility.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ScheduleEventTest implements FormatterPattern {
    LocalTime baseLengthTime = ScheduleEvent.BASE_LENGTH_TIME;
    private LocalDateTime d1;
    private LocalDateTime d2;
    private LocalDateTime d3;
    private LocalDateTime d4;
    private ScheduleEvent s1;
    private ScheduleEvent s2;
    private ScheduleEvent s3;
    private ScheduleEvent s4;

    @BeforeEach
    void runBefore() {
        d1 = LocalDateTime.of(1987, 4,2,4,30);
        d2 = LocalDateTime.of(1211, 4,17,7,14);
        d3 = LocalDateTime.of(2011,5,30,2,30);
        d4 = LocalDateTime.of(2014,10,3,23,59);
        s1 = new DateEvent("Walk the dog", d1);
        s2 = new DateEvent("Walk the dog", d2.toLocalDate(), d2.toLocalTime(), d3.toLocalTime());
        s3 = new DateEvent("Walk at the park", d3);
        s4 = new DateEvent("", d4);
    }

    @Test
    void testName() {
        assertEquals("Walk the dog", s1.getName());
        assertEquals("Walk at the park", s3.getName());
        s1.setName("Borrow");
        s2.setName("Have");
        assertEquals("Borrow", s1.getName());
        assertEquals("Have", s2.getName());
    }

    @Test
    void testGetStartTime() {
        assertEquals(d1.toLocalTime(), s1.getStartTime());
        assertEquals(d3.toLocalTime(), s3.getStartTime());
    }

    @Test
    void testGetLengthTime() {
        assertEquals(baseLengthTime, s1.getLengthTime());
    }

    @Test
    void testGetEndTime() {
        assertEquals(addLocalTimesLimit(d1.toLocalTime(), baseLengthTime), s1.getEndTime());
        assertEquals(addLocalTimesLimit(d4.toLocalTime(), baseLengthTime), s4.getEndTime());
    }

    @Test
    void testIsOnTime() {
        assertFalse(s1.isOnTime(LocalTime.of(3,30)));
        assertTrue(s1.isOnTime(s1.getStartTime()));
        assertTrue(s1.isOnTime(LocalTime.of(5,0)));
        assertTrue(s1.isOnTime(s1.getEndTime()));
        assertFalse(s1.isOnTime(LocalTime.of(20,40)));
    }

    @Test
    void testIsOnDateTime() {
        assertFalse(s1.isOnDateTime(d1.minusDays(5)));
        assertFalse(s1.isOnDateTime(d1.minusHours(3)));
        assertTrue(s1.isOnDateTime(d1));
        assertTrue(s1.isOnDateTime(d1.plusMinutes(30)));
        assertTrue(s1.isOnDateTime(LocalDateTime.of(d1.toLocalDate(), s1.getEndTime())));
        assertFalse(s1.isOnDateTime(d1.plusHours(3)));
        assertFalse(s1.isOnDateTime(d1.plusDays(10)));
    }

    @Test
    void testSameName() {
        assertTrue(s1.checkSameName(s2));
        assertFalse(s2.checkSameName(s3));
        assertTrue(s3.checkSameName(s3));
        assertTrue(s4.checkSameName(s4));
    }
}
