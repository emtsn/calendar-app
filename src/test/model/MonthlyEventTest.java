package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MonthlyEventTest implements FormatterPattern {
    private MonthlyEvent m1;
    private MonthlyEvent m2;
    private MonthlyEvent m3;

    @BeforeEach
    void runBefore() {
        m1 = new MonthlyEvent("Renew Stuff", 25, LocalTime.of(12,0), LocalTime.of(12,30));
        m2 = new MonthlyEvent("Start of Month", 1, LocalTime.of(0,0).format(TIME_FORMATTER),
                LocalTime.of(1,0).format(TIME_FORMATTER));
        m3 = new MonthlyEvent("Twelve", 12, LocalTime.of(16,0), LocalTime.of(21,0));
    }

    @Test
    void testGetDayOfMonth() {
        assertEquals(m1.getDayOf(), 25);
    }

    @Test
    void testIsOnDate() {
        assertTrue(m1.isOnDate(LocalDate.of(2012,4,25)));
        assertTrue(m1.isOnDate(LocalDate.of(2020,12,25)));
        assertFalse(m1.isOnDate(LocalDate.of(2012,4,12)));
    }

    @Test
    void testGetTimeString() {
        assertEquals(m1.getTimeString(), "25th 12:00~12:30");
        assertEquals(m2.getTimeString(), "1st 00:00~01:00");
        assertEquals(m3.getTimeString(), "12th 16:00~21:00");
    }
}
