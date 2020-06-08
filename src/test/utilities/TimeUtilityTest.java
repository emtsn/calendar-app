package utilities;

import model.exceptions.TimeOverflowException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static utilities.TimeUtility.*;

public class TimeUtilityTest {

    private LocalTime t1;
    private LocalTime t2;
    private LocalTime t3;
    private LocalTime t4;
    private LocalDate d1;
    private LocalDate d2;
    private LocalDate d3;
    private LocalDateTime dt1;
    private LocalDateTime dt2;
    private LocalDateTime dt3;
    private DayOfWeek dw1;
    private DayOfWeek dw2;

    @BeforeEach
    void runBefore() {
        t1 = LocalTime.of(4,10);
        t2 = LocalTime.of(7,15);
        t3 = LocalTime.of(9,6);
        t4 = LocalTime.of(20,44);
        d1 = LocalDate.of(1920,2,28);
        d2 = LocalDate.of(1987,7,3);
        d3 = LocalDate.of(2020,12,16);
        dt1 = LocalDateTime.of(d1, t3);
        dt2 = LocalDateTime.of(d2, t2);
        dt3 = LocalDateTime.of(d3, t1);
        dw1 = DayOfWeek.MONDAY;
        dw2 = DayOfWeek.SUNDAY;
    }

    @Test
    void testAddLocalTimes() {
        try {
            assertEquals(LocalTime.of(11, 25), addLocalTimes(t1, t2));
            assertEquals(LocalTime.of(13, 16), addLocalTimes(t3, t1));
            assertEquals(LocalTime.of(14, 30), addLocalTimes(t2, t2));
            addLocalTimes(LocalTime.of(23, 0), LocalTime.of(0, 59));
        } catch (TimeOverflowException te) {
            fail();
        }
    }

    @Test
    void testAddLocalTimesOverflowException() {
        try {
            addLocalTimes(LocalTime.of(23,1), LocalTime.of(0,59));
            fail();
        } catch (TimeOverflowException te) {

        }
    }
    @Test
    void testAddLocalLimit() {
        assertEquals(LocalTime.of(11,25), addLocalTimesLimit(t1, t2));
        assertEquals(LocalTime.of(23,59), addLocalTimesLimit(t4, t3));
        assertEquals(LocalTime.of(23,59), addLocalTimesLimit(t1, t4));
    }

    @Test
    void testDiffLocalTimes() {
        assertEquals(LocalTime.of(3,5), diffLocalTimes(t1, t2));
        assertEquals(LocalTime.of(3,5), diffLocalTimes(t2, t1));
        assertEquals(LocalTime.of(4,56), diffLocalTimes(t3, t1));
        assertEquals(LocalTime.of(4,56), diffLocalTimes(t1, t3));
        assertEquals(LocalTime.of(1,51), diffLocalTimes(t2, t3));
        assertEquals(LocalTime.of(1,51), diffLocalTimes(t3, t2));
    }

    @Test
    void testIsBetweenTime() {
        assertTrue(isBetweenTime(t2, t1, t3));
        assertTrue(isBetweenTime(t1, t1, t3));
        assertTrue(isBetweenTime(t3, t1, t3));
        assertFalse(isBetweenTime(t1, t2, t3));
        assertFalse(isBetweenTime(t3, t1, t2));
    }

    @Test
    void testIsBetweenDate() {
        assertTrue(isBetweenDate(d2, d1, d3));
        assertTrue(isBetweenDate(d1, d1, d3));
        assertTrue(isBetweenDate(d3, d1, d3));
        assertFalse(isBetweenDate(d1, d2, d3));
        assertFalse(isBetweenDate(d3, d1, d2));
    }

    @Test
    void testIsBetweenDateTime() {
        assertTrue(isBetweenDateTime(dt2, dt1, dt3));
        assertTrue(isBetweenDateTime(dt1, dt1, dt3));
        assertTrue(isBetweenDateTime(dt3, dt1, dt3));
        assertFalse(isBetweenDateTime(dt1, dt2, dt3));
        assertFalse(isBetweenDateTime(dt3, dt1, dt2));
    }

    @Test
    void testDowFullFormat() {
        assertEquals("Monday", dowFullFormat(dw1));
        assertEquals("Sunday", dowFullFormat(dw2));
    }

    @Test
    void testDowShortFormat() {
        assertEquals("Mon", dowShortFormat(dw1));
        assertEquals("Sun", dowShortFormat(dw2));
    }

    @Test
    void testDowValues() {
        DayOfWeek[] mondayStart = new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY};
        DayOfWeek[] wednesdayStart = new DayOfWeek[]{DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY};
        DayOfWeek[] sundayStart = new DayOfWeek[]{DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY};
        assertArrayEquals(mondayStart, dowValues(DayOfWeek.MONDAY));
        assertArrayEquals(wednesdayStart, dowValues(DayOfWeek.WEDNESDAY));
        assertArrayEquals(sundayStart, dowValues(DayOfWeek.SUNDAY));
    }

    @Test
    void testIsBetweenLoop() {
        // same loop, out left
        assertFalse(isBetweenLoop(2, 3, 5,true));
        // same loop out right
        assertFalse(isBetweenLoop(6, 3, 5,true));
        // same loop in
        assertTrue(isBetweenLoop(7, 7, 20,true));
        assertTrue(isBetweenLoop(16, 7, 20,true));
        assertTrue(isBetweenLoop(20, 7, 20,true));
        // different loop, out
        assertFalse(isBetweenLoop(15, 20, 7,false));
        assertFalse(isBetweenLoop(19, 20, 7,false));
        assertFalse(isBetweenLoop(8, 20, 7,false));
    }

    @Test
    void testWeekStartEnd() {
        LocalDate sunday = LocalDate.of(2015, 3, 15);
        LocalDate monday = LocalDate.of(2015, 3, 23);
        LocalDate tuesday = LocalDate.of(2015, 3, 31);
        LocalDate wednesday = LocalDate.of(2015, 4, 8);
        LocalDate thursday = LocalDate.of(2014, 6, 12);
        LocalDate friday = LocalDate.of(2014, 8, 1);
        LocalDate saturday = LocalDate.of(2014, 4, 5);
        assertEquals(sunday, atStartOfWeek(sunday));
        assertEquals(monday.minusDays(1), atStartOfWeek(monday));
        assertEquals(tuesday.minusDays(2), atStartOfWeek(tuesday));
        assertEquals(wednesday.minusDays(3), atStartOfWeek(wednesday));
        assertEquals(thursday.minusDays(4), atStartOfWeek(thursday));
        assertEquals(friday.minusDays(5), atStartOfWeek(friday));
        assertEquals(saturday.minusDays(6), atStartOfWeek(saturday));
        assertEquals(sunday.plusDays(6), atEndOfWeek(sunday));
        assertEquals(monday.plusDays(5), atEndOfWeek(monday));
        assertEquals(tuesday.plusDays(4), atEndOfWeek(tuesday));
        assertEquals(wednesday.plusDays(3), atEndOfWeek(wednesday));
        assertEquals(thursday.plusDays(2), atEndOfWeek(thursday));
        assertEquals(friday.plusDays(1), atEndOfWeek(friday));
        assertEquals(saturday, atEndOfWeek(saturday));
    }

    @Test
    void testIntArray() {
        Integer[] minusEightToMinusFour = new Integer[]{-8, -7, -6, -5, -4};
        Integer[] minusThreeToFive = new Integer[]{-3, -2, -1, 0, 1, 2, 3, 4, 5};
        Integer[] tenToFifteen = new Integer[]{10, 11, 12, 13, 14, 15};
        assertArrayEquals(minusEightToMinusFour, intArray(-8, -4));
        assertArrayEquals(minusThreeToFive, intArray(-3, 5));
        assertArrayEquals(tenToFifteen, intArray(10, 15));
    }

    @Test
    void testDaysBetween() {
        assertEquals(1, TimeUtility.daysBetweenDow(DayOfWeek.SUNDAY, DayOfWeek.MONDAY));
        assertEquals(6, TimeUtility.daysBetweenDow(DayOfWeek.MONDAY, DayOfWeek.SUNDAY));
        assertEquals(2, TimeUtility.daysBetweenDow(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY));
        assertEquals(5, TimeUtility.daysBetweenDow(DayOfWeek.THURSDAY, DayOfWeek.TUESDAY));
        assertEquals(3, TimeUtility.daysBetweenDow(DayOfWeek.MONDAY, DayOfWeek.THURSDAY));
        assertEquals(4, TimeUtility.daysBetweenDow(DayOfWeek.THURSDAY, DayOfWeek.MONDAY));
        assertEquals(5, TimeUtility.daysBetweenDow(DayOfWeek.WEDNESDAY, DayOfWeek.MONDAY));
        assertEquals(2, TimeUtility.daysBetweenDow(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY));
    }
}
