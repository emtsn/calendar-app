package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

public class RepeatEventTest {

    private RepeatEvent r1;

    @BeforeEach
    void runBefore() {
        r1 = new WeeklyEvent("CS Class", DayOfWeek.MONDAY, LocalTime.of(11, 0), LocalTime.of(11,50));
    }

    @Test
    void testRepeatKey() {
        RepeatKey key1 = new RepeatKey(ChronoUnit.WEEKS, 1);
        RepeatKey key2 = new RepeatKey(ChronoUnit.WEEKS, 1);
        RepeatKey key3 = new RepeatKey(ChronoUnit.MONTHS, 1);
        RepeatKey key4 = new RepeatKey(ChronoUnit.WEEKS, 3);
        assertEquals(ChronoUnit.WEEKS, key1.getTimeScale());
        assertEquals(3, key4.getDayOf());
        assertNotEquals(key1, r1);
        assertEquals(key1, key1);
        assertEquals(key1, key2);
        assertNotEquals(key2, key3);
        assertNotEquals(key1, key4);
    }

    @Test
    void testGetTimeScale() {
        assertEquals(ChronoUnit.WEEKS, r1.getTimeScale());
    }
}
