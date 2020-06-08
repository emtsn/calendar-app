package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ScheduleContainerTest extends ScheduleTest {

    private ScheduleContainer s2;
    private DateEvent d5;
    private DateEvent d6;
    private DateEvent d7;
    private DateEvent d8;
    private ExampleEvent example;

    @BeforeEach
    void runBefore() {
        Initialize();
        s2 = new ScheduleContainer();
        d5 = new DateEvent("Conflicts with d6", LocalDateTime.of(2000, 5,5,12,0));
        d6 = new DateEvent("Conflicts with d5 and d7", LocalDateTime.of(2000, 5,5,12,50));
        d7 = new DateEvent("Conflicts with d6", LocalDateTime.of(2000, 5,5,13,20));
        d8 = new DateEvent("Conflicts with d7", LocalDateTime.of(2000, 5,5,14,20));
        example = new ExampleEvent("name", LocalTime.of(10,30), LocalTime.of(11,30));
    }

    private class ExampleEvent extends RepeatEvent {
        public ExampleEvent(String name, LocalTime startTime, LocalTime endTime) {
            super(name, startTime, endTime, ChronoUnit.HOURS, 1);
        }
        @Override
        public boolean isOnDate(LocalDate date) {
            return false;
        }

        @Override
        public String getTimeScaleString() {
            return "";
        }

        @Override
        public int getDayOf() {
            return 1;
        }
    }

    @Test
    void testSetGet() {
        addManyDateEvents();
        addManyWeeklyEvents();
        addManyMonthlyEvents();
        assertEquals(listManyDate.size(), s1.getDateEventsSize());
        assertEquals(listManyRepeat.size(), s1.getRepeatEventsSize());
        s1.clearScheduleEvents();
        assertEquals(0, s1.getDateEventsSize());
        assertEquals(0, s1.getRepeatEventsSize());
        s1.setDateEvents(listManyDate);
        assertEquals(listManyDate.size(), s1.getDateEventsSize());
        assertEquals(0, s1.getRepeatEventsSize());
        s1.setRepeatEvents(listManyRepeat);
        assertEquals(listManyDate.size(), s1.getDateEventsSize());
        assertEquals(listManyRepeat.size(), s1.getRepeatEventsSize());
        assertEquals(listManyDate.size() + listManyRepeat.size(), s1.getSize());
    }

    @Test
    void testAddOneDateEvent() {
        assertEquals(0, s1.getDateEventsSize());
        assertFalse(s1.dateEventsContains("Talk to John"));
        assertFalse(s1.dateEventsContains("Walk the dog"));
        s1.addEvent(d1);
        assertEquals(1, s1.getDateEventsSize());
        assertEquals(d1, s1.getDateEvent(0));
        assertTrue(s1.dateEventsContains("Talk to John"));
        assertFalse(s1.dateEventsContains("Walk the dog"));
    }

    @Test
    void testAddOneWeeklyEvent() {
        assertEquals(0, s1.getRepeatEventsSize());
        assertFalse(s1.repeatEventsContains("Take out the trash"));
        assertFalse(s1.repeatEventsContains("Call company"));
        s1.addEvent(w1);
        assertEquals(1, s1.getRepeatEventsSize());
        assertEquals(w1, s1.getRepeatEvent(0));
        assertTrue(s1.repeatEventsContains("Take out the trash"));
        assertFalse(s1.repeatEventsContains("Call company"));
    }

    @Test
    void testAddOneMonthlyEvent() {
        assertEquals(0, s1.getRepeatEventsSize());
        assertFalse(s1.repeatEventsContains("Renew Stuff"));
        assertFalse(s1.repeatEventsContains("Call the dog"));
        s1.addEvent(m2);
        assertEquals(1, s1.getRepeatEventsSize());
        assertEquals(m2, s1.getRepeatEvent(0));
        assertTrue(s1.repeatEventsContains("Renew Stuff"));
        assertFalse(s1.repeatEventsContains("Call the dog"));
    }

    @Test
    void testAddLotsDateEvent() {
        assertEquals(0, s1.getDateEventsSize());
        assertFalse(s1.dateEventsContains("Talk to John"));
        assertFalse(s1.dateEventsContains("Go to museum"));
        assertFalse(s1.dateEventsContains(""));
        for (int i = 0; i < 299; i++) {
            s1.addEvent(d2);
        }
        s1.addEvent(d3);
        assertEquals(300, s1.getDateEventsSize());
        assertEquals(d2, s1.getDateEvent(10));
        assertEquals(d3, s1.getDateEvent(299));
        assertTrue(s1.dateEventsContains("Walk the dog"));
        assertTrue(s1.dateEventsContains("Go to museum"));
        assertFalse(s1.dateEventsContains(""));
    }

    @Test
    void testAddLotsWeeklyEvent() {
        assertEquals(0, s1.getRepeatEventsSize());
        assertFalse(s1.repeatEventsContains("Take out the trash"));
        assertFalse(s1.repeatEventsContains("Call company"));
        assertFalse(s1.repeatEventsContains("Pay John"));
        for (int i = 0; i < 299; i++) {
            s1.addEvent(w1);
        }
        s1.addEvent(w2);
        assertEquals(300, s1.getRepeatEventsSize());
        assertEquals(w1, s1.getRepeatEvent(10));
        assertEquals(w2, s1.getRepeatEvent(299));
        assertTrue(s1.repeatEventsContains("Take out the trash"));
        assertTrue(s1.repeatEventsContains("Call company"));
        assertFalse(s1.repeatEventsContains("Pay John"));
    }

    @Test
    void testGetManyDateEvent() {
        addManyDateEvents();
        assertEquals(d1, s1.getDateEvent(0));
        assertEquals(d2, s1.getDateEvent(1));
        assertEquals(d3, s1.getDateEvent(2));
        assertEquals(d3, s1.getDateEvent(3));
        assertEquals(d4, s1.getDateEvent(4));
        assertEquals(listManyDate, s1.getDateEvents());
        assertEquals(new ArrayList<ScheduleEvent>(listManyDate), s1.getScheduleEvents());
    }

    @Test
    void testGetManyWeeklyEvent() {
        addManyWeeklyEvents();
        assertEquals(w1, s1.getRepeatEvent(0));
        assertEquals(w1, s1.getRepeatEvent(1));
        assertEquals(w2, s1.getRepeatEvent(2));
        assertEquals(w3, s1.getRepeatEvent(3));
        assertEquals(w3, s1.getRepeatEvent(4));
        assertEquals(listManyWeekly, s1.getRepeatEvents());
        assertEquals(new ArrayList<ScheduleEvent>(listManyWeekly), s1.getScheduleEvents());
    }

    @Test
    void testGetManyMonthlyEvent() {
        addManyMonthlyEvents();
        assertEquals(m1, s1.getRepeatEvent(0));
        assertEquals(m2, s1.getRepeatEvent(1));
        assertEquals(m2, s1.getRepeatEvent(2));
        assertEquals(m3, s1.getRepeatEvent(3));
        assertEquals(m3, s1.getRepeatEvent(4));
        assertEquals(listManyMonthly, s1.getRepeatEvents());
        assertEquals(new ArrayList<ScheduleEvent>(listManyMonthly), s1.getScheduleEvents());
    }

    @Test
    void testGetManyScheduleEvent() {
        addManyDateEvents();
        addManyWeeklyEvents();
        assertEquals(listManyDate, s1.getDateEvents());
        assertEquals(listManyWeekly, s1.getRepeatEvents());
        List<ScheduleEvent> listSchedule = new ArrayList<>();
        listSchedule.addAll(listManyWeekly);
        listSchedule.addAll(listManyDate);
        assertEquals(listSchedule, s1.getScheduleEvents());
    }

    @Test
    void testRemoveEvent() {
        addManyToSchedule();
        assertTrue(s1.removeEvent(d1));
        assertTrue(s1.removeEvent(w1));
        assertTrue(s1.removeEvent(m1));
        assertFalse(s1.removeEvent(example));
    }

    @Test
    void testRemoveOneDateEvent() {
        s1.addEvent(d1);
        assertEquals(1, s1.getDateEventsSize());
        assertTrue(s1.dateEventsContains("Talk to John"));
        s1.removeEvent(d1);
        assertEquals(0, s1.getDateEventsSize());
        assertFalse(s1.dateEventsContains("Talk to John"));
        assertFalse(s1.removeEvent(d1));
    }

    @Test
    void testRemoveOneRepeatEvent() {
        s1.addEvent(w1);
        assertEquals(1, s1.getRepeatEventsSize());
        assertTrue(s1.repeatEventsContains("Take out the trash"));
        s1.removeEvent(w1);
        assertEquals(0, s1.getRepeatEventsSize());
        assertFalse(s1.repeatEventsContains("Take out the trash"));
        assertFalse(s1.removeEvent(w1));
    }

    @Test
    void testRemoveManyDateEvent() {
        addManyDateEvents();
        s1.removeEvent(d1);
        s1.removeEvent(d2);
        assertEquals(3, s1.getDateEventsSize());
        assertFalse(s1.dateEventsContains(d1.getName()));
        assertFalse(s1.dateEventsContains(d2.getName()));
        assertTrue(s1.dateEventsContains(d3.getName()));
        assertTrue(s1.dateEventsContains(d4.getName()));
    }

    @Test
    void testRemoveManyRepeatEvent() {
        addManyWeeklyEvents();
        s1.removeEvent(w1);
        s1.removeEvent(w1);
        assertEquals(3, s1.getRepeatEventsSize());
        assertFalse(s1.repeatEventsContains(w1.getName()));
        assertTrue(s1.repeatEventsContains(w2.getName()));
        assertTrue(s1.repeatEventsContains(w3.getName()));
    }

    @Test
    void testSortManyWeekCorrect() {
        addManyWeeklyEvents();
        s1.sort();
        assertEquals(w1, s1.getRepeatEvent(0));
        assertEquals(w1, s1.getRepeatEvent(1));
        assertEquals(w2, s1.getRepeatEvent(2));
        assertEquals(w3, s1.getRepeatEvent(3));
        assertEquals(w3, s1.getRepeatEvent(4));
    }

    @Test
    void testSortManyWeekMixed() {
        s1.addEvent(w2);
        s1.addEvent(w3);
        s1.addEvent(w1);
        s1.sort();
        assertEquals(w1, s1.getRepeatEvent(0));
        assertEquals(w2, s1.getRepeatEvent(1));
        assertEquals(w3, s1.getRepeatEvent(2));
    }

    @Test
    void testSortManyWeekTime() {
        WeeklyEvent monday1 = new WeeklyEvent("1", DayOfWeek.MONDAY, LocalTime.of(0, 16), LocalTime.of(23,30));
        WeeklyEvent monday2 = new WeeklyEvent("2", DayOfWeek.MONDAY, LocalTime.of(4, 40), LocalTime.of(7,30));
        WeeklyEvent monday3 = new WeeklyEvent("3", DayOfWeek.MONDAY, LocalTime.of(10, 50), LocalTime.of(11,50));
        s1.addEvent(monday3);
        s1.addEvent(monday1);
        s1.addEvent(monday2);
        s1.sort();
        assertEquals(monday1, s1.getRepeatEvent(0));
        assertEquals(monday2, s1.getRepeatEvent(1));
        assertEquals(monday3, s1.getRepeatEvent(2));
    }

    @Test
    void testGetScheduleForDate() {
        LocalDate date = LocalDate.of(2019, 11, 28);
        assertEquals(DayOfWeek.THURSDAY, date.getDayOfWeek());
        DateEvent date1 = new DateEvent("date1", LocalDateTime.of(2019, 11, 28, 10, 40));
        DateEvent date2 = new DateEvent("date2", LocalDateTime.of(2019, 11, 28, 0, 30));
        RepeatEvent monthly1 = new MonthlyEvent("monthly1", 28, LocalTime.of(20,0), LocalTime.of(21,0));
        RepeatEvent monthly2 = new MonthlyEvent("monthly2", 28, LocalTime.of(20,0), LocalTime.of(21,0));
        RepeatEvent weekly1 = new WeeklyEvent("weekly1", DayOfWeek.THURSDAY, LocalTime.of(12,0));
        RepeatEvent weekly2 = new WeeklyEvent("weekly2", DayOfWeek.THURSDAY, LocalTime.of(4,0));
        s1.addEvent(date1);
        s1.addEvent(weekly1);
        s1.addEvent(monthly1);
        addManyToSchedule();
        s1.addEvent(date2);
        s1.addEvent(weekly2);
        s1.addEvent(monthly2);
        List<DateEvent> dateEventsForDate = s1.getDateEventsBetweenDates(date, date);
        List<RepeatEvent> repeatEventsForDate = s1.getRepeatEventsForDate(date);
        assertEquals(2, dateEventsForDate.size());
        assertEquals(4, repeatEventsForDate.size());
    }

    @Test
    void testGetScheduleBetweenDate() {
        LocalDate date = LocalDate.of(2019, 11, 27);
        DateEvent date1 = new DateEvent("date1", date.atTime(10, 40));
        DateEvent date2 = new DateEvent("date2", date.plusDays(1).atTime(0, 30));
        DateEvent date3 = new DateEvent("date3", date.plusDays(2).atTime(0, 30));
        DateEvent date4 = new DateEvent("date4", date.plusDays(3).atTime(0, 30));
        RepeatEvent monthly1 = new MonthlyEvent("monthly1", date.getDayOfMonth(), LocalTime.of(20,0), LocalTime.of(21,0));
        RepeatEvent monthly2 = new MonthlyEvent("monthly2", date.getDayOfMonth() + 1, LocalTime.of(20,0), LocalTime.of(21,0));
        RepeatEvent monthly3 = new MonthlyEvent("monthly3", date.getDayOfMonth() + 2, LocalTime.of(20,0), LocalTime.of(21,0));
        RepeatEvent monthly4 = new MonthlyEvent("monthly4", date.getDayOfMonth() + 3, LocalTime.of(20,0), LocalTime.of(21,0));
        RepeatEvent weekly1 = new WeeklyEvent("weekly1", date.getDayOfWeek(), LocalTime.of(12,0));
        RepeatEvent weekly2 = new WeeklyEvent("weekly2", date.plusDays(1).getDayOfWeek(), LocalTime.of(4,0));
        RepeatEvent weekly3 = new WeeklyEvent("weekly3", date.plusDays(2).getDayOfWeek(), LocalTime.of(4,0));
        RepeatEvent weekly4 = new WeeklyEvent("weekly4", date.plusDays(3).getDayOfWeek(), LocalTime.of(4,0));
        s1.addEvent(date1);
        s1.addEvent(date2);
        s1.addEvent(date3);
        s1.addEvent(date4);
        s1.addEvent(monthly1);
        s1.addEvent(monthly2);
        s1.addEvent(monthly3);
        s1.addEvent(monthly4);
        s1.addEvent(weekly1);
        s1.addEvent(weekly2);
        s1.addEvent(weekly3);
        s1.addEvent(weekly4);
        assertEquals(2, s1.getDateEventsBetweenDates(date.plusDays(1), date.plusDays(2)).size());
        assertEquals(4, s1.getRepeatEventsBetweenDates(date.plusDays(1), date.plusDays(2)).size());
        assertEquals(4, s1.getDateEventsBetweenDates(LocalDate.MIN, LocalDate.MAX).size());
        assertEquals(2, s1.getDateEventsBetweenDates(LocalDate.MIN, date.plusDays(1)).size());
        assertEquals(2, s1.getDateEventsBetweenDates(date.plusDays(2), LocalDate.MAX).size());
        assertEquals(8, s1.getRepeatEventsBetweenDates(LocalDate.MIN, LocalDate.MAX).size());
        assertEquals(8, s1.getRepeatEventsBetweenDates(LocalDate.MIN, date.plusDays(1)).size());
        assertEquals(8, s1.getRepeatEventsBetweenDates(date.plusDays(2), LocalDate.MAX).size());

    }

    @Test
    void testClearEvents() {
        s1.addEvent(d1);
        s1.addEvent(d2);
        s1.addEvent(d3);
        s1.addEvent(d4);
        s1.clearScheduleEvents();
        assertEquals(0, s1.getDateEventsSize());
        assertEquals(0, s1.getRepeatEventsSize());
    }

    @Test
    void testNoConflict() {
        s2.addEvent(d2);
        s2.addEvent(d3);
        s2.addEvent(d5);
        s2.addEvent(d7);
        assertFalse(s2.hasDateEventConflict());
    }

    @Test
    void testConflict() {
        s2.addEvent(d6);
        assertFalse(s2.hasDateEventConflict());
        s2.addEvent(d5);
        assertTrue(s2.hasDateEventConflict());
        s2.addEvent(d7);
        assertTrue(s2.hasDateEventConflict());
    }

    @Test
    void testConflictEdge() {
        s2.addEvent(d7);
        s2.addEvent(d8);
        assertTrue(s2.hasDateEventConflict());
    }

    @Test
    void testSetHasEventsForYearMonth() {
        addManyDateEvents();
        addManyMonthlyEvents();
        addManyWeeklyEvents();
        YearMonth yearMonth = YearMonth.of(d3.getDate().getYear(), d3.getDate().getMonth());
        s1.addEvent(new DateEvent("one", yearMonth.atDay(12), LocalTime.NOON, LocalTime.NOON));
        s1.addEvent(new DateEvent("two", yearMonth.atDay(20), LocalTime.NOON, LocalTime.NOON));
        s1.addEvent(new DateEvent("two", yearMonth.atDay(3), LocalTime.NOON, LocalTime.NOON));
        boolean[] allHas = s1.hasEvents(yearMonth, true, true);
        int startDow = yearMonth.atDay(1).getDayOfWeek().getValue();
        for (int i = 0; i < yearMonth.lengthOfMonth(); i++) {
            boolean anyTrue = false;
            if (i + 1 == 3 || i + 1 == 12 || i + 1 == 20 || i + 1 == d3.getDate().getDayOfMonth()) {
                assertTrue(allHas[i]);
                anyTrue = true;
            }
            if ((i + 1) % 7 == startDow + w1.getDayOfWeek().getValue() - 1
                    || (i + 1) % 7 == startDow + w2.getDayOfWeek().getValue() - 1
                    || (i + 1) % 7 == startDow + w3.getDayOfWeek().getValue() - 1) {
                assertTrue(allHas[i]);
                anyTrue = true;
            }
            if (i + 1 == m1.getDayOf() || i + 1 == m2.getDayOf() || i + 1 == m3.getDayOf()) {
                assertTrue(allHas[i]);
                anyTrue = true;
            }
            if (!anyTrue) {
                assertFalse(allHas[i]);
            }
        }
        boolean[] dateHas = s1.hasEvents(yearMonth, true, false);
        for (int i = 0; i < yearMonth.lengthOfMonth(); i++) {
            if (i + 1 == 3 || i + 1 == 12 || i + 1 == 20 || i + 1 == d3.getDate().getDayOfMonth()) {
                assertTrue(dateHas[i]);
            } else {
                assertFalse(dateHas[i]);
            }
        }
        boolean[] repeatHas = s1.hasEvents(yearMonth, false, true);
        for (int i = 0; i < yearMonth.lengthOfMonth(); i++) {
            boolean anyTrue = false;
            if ((i + 1) % 7 == startDow + w1.getDayOfWeek().getValue() - 1
                    || (i + 1) % 7 == startDow + w2.getDayOfWeek().getValue() - 1
                    || (i + 1) % 7 == startDow + w3.getDayOfWeek().getValue() - 1) {
                assertTrue(repeatHas[i]);
                anyTrue = true;
            }
            if (i + 1 == m1.getDayOf() || i + 1 == m2.getDayOf() || i + 1 == m3.getDayOf()) {
                assertTrue(repeatHas[i]);
                anyTrue = true;
            }
            if (!anyTrue) {
                assertFalse(repeatHas[i]);
            }
        }
    }
}
