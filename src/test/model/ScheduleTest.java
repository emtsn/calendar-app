package model;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public abstract class ScheduleTest {

    public ScheduleContainer s1;
    public List<DateEvent> listManyDate;
    public List<RepeatEvent> listManyRepeat;
    public List<WeeklyEvent> listManyWeekly;
    public List<MonthlyEvent> listManyMonthly;
    public DateEvent d1;
    public DateEvent d2;
    public DateEvent d3;
    public DateEvent d4;
    public WeeklyEvent w1;
    public WeeklyEvent w2;
    public WeeklyEvent w3;
    public MonthlyEvent m1;
    public MonthlyEvent m2;
    public MonthlyEvent m3;

    public void Initialize() {
        s1 = new ScheduleContainer();
        d1 = new DateEvent("Talk to John", LocalDateTime.of(1914, 4,5,13,32));
        d2 = new DateEvent("Walk the dog", LocalDateTime.of(1967, 9,30,6,38));
        d3 = new DateEvent("Go to museum", LocalDateTime.of(2010, 11,15,19,23));
        d4 = new DateEvent("", LocalDateTime.of(2013, 5,23,22,47));
        w1 = new WeeklyEvent("Take out the trash", DayOfWeek.MONDAY, LocalTime.of(10,30));
        w2 = new WeeklyEvent("Call company", DayOfWeek.FRIDAY, LocalTime.of(20,15));
        w3 = new WeeklyEvent("Pay John", DayOfWeek.SATURDAY, LocalTime.of(10,30));
        m1 = new MonthlyEvent("Flip calendar", 1, LocalTime.of(0,0), LocalTime.of(0,30));
        m2 = new MonthlyEvent("Renew Stuff", 14, LocalTime.of(11,0), LocalTime.of(11,30));
        m3 = new MonthlyEvent("Fourteen", 25, LocalTime.of(20,0), LocalTime.of(22,30));
        listManyDate = new ArrayList<>();
        listManyRepeat = new ArrayList<>();
        listManyWeekly = new ArrayList<>();
        listManyMonthly = new ArrayList<>();
    }

    public void addManyToSchedule() {
        addManyDateEvents();
        addManyWeeklyEvents();
        addManyMonthlyEvents();
    }

    public void addManyDateEvents() {
        s1.addEvent(d3);
        s1.addEvent(d2);
        s1.addEvent(d3);
        s1.addEvent(d1);
        s1.addEvent(d4);
        listManyDate.add(d1);
        listManyDate.add(d2);
        listManyDate.add(d3);
        listManyDate.add(d3);
        listManyDate.add(d4);
    }

    public void addManyWeeklyEvents() {
        s1.addEvent(w3);
        s1.addEvent(w1);
        s1.addEvent(w3);
        s1.addEvent(w1);
        s1.addEvent(w2);
        listManyRepeat.add(w1);
        listManyRepeat.add(w1);
        listManyRepeat.add(w2);
        listManyRepeat.add(w3);
        listManyRepeat.add(w3);
        listManyWeekly.add(w1);
        listManyWeekly.add(w1);
        listManyWeekly.add(w2);
        listManyWeekly.add(w3);
        listManyWeekly.add(w3);
    }

    public void addManyMonthlyEvents() {
        s1.addEvent(m2);
        s1.addEvent(m3);
        s1.addEvent(m2);
        s1.addEvent(m3);
        s1.addEvent(m1);
        listManyRepeat.add(m1);
        listManyRepeat.add(m2);
        listManyRepeat.add(m2);
        listManyRepeat.add(m3);
        listManyRepeat.add(m3);
        listManyMonthly.add(m1);
        listManyMonthly.add(m2);
        listManyMonthly.add(m2);
        listManyMonthly.add(m3);
        listManyMonthly.add(m3);
    }

}
