package utilities;

import model.exceptions.TimeOverflowException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.Locale;

public final class TimeUtility {

    // EFFECTS: returns sum of two LocalTime
    public static LocalTime addLocalTimes(LocalTime time1, LocalTime time2) throws TimeOverflowException {
        if (diffLocalTimes(time1, LocalTime.of(23,59)).isBefore(time2)) {
            throw new TimeOverflowException();
        }
        return time1.plusHours(time2.getHour()).plusMinutes(time2.getMinute());
    }

    // EFFECTS: returns sum of two LocalTime for max of 23:59
    public static LocalTime addLocalTimesLimit(LocalTime time1, LocalTime time2) {
        if (diffLocalTimes(time1, LocalTime.of(23,59)).isAfter(time2)) {
            return time1.plusHours(time2.getHour()).plusMinutes(time2.getMinute());
        } else {
            return LocalTime.of(23,59);
        }
    }

    // EFFECTS: returns difference between two LocalTime as LocalTime
    public static LocalTime diffLocalTimes(LocalTime time1, LocalTime time2) {
        if (!time1.isAfter(time2)) {
            return time2.minusHours(time1.getHour()).minusMinutes(time1.getMinute());
        } else {
            return time1.minusHours(time2.getHour()).minusMinutes(time2.getMinute());
        }
    }

    // EFFECTS: returns true if dateTime is contained within startDateTime and endDateTime inclusive, false otherwise
    public static boolean isBetweenDateTime(LocalDateTime dateTime, LocalDateTime startDateTime,
                                            LocalDateTime endDateTime) {
        return dateTime.isEqual(startDateTime) || dateTime.isEqual(endDateTime)
                || (dateTime.isAfter(startDateTime) && dateTime.isBefore(endDateTime));
    }

    // EFFECTS: returns true if date is contained within startDate and endDate inclusive, false otherwise
    public static boolean isBetweenDate(LocalDate date, LocalDate startDate, LocalDate endDate) {
        return date.isEqual(startDate) || date.isEqual(endDate)
                || (date.isAfter(startDate) && date.isBefore(endDate));
    }

    // EFFECTS: returns true if time is contained within startTime and endTime inclusive, false otherwise
    public static boolean isBetweenTime(LocalTime time, LocalTime startTime, LocalTime endTime) {
        return (!time.isBefore(startTime) && !time.isAfter(endTime));
    }

    // EFFECTS: returns dayOfWeek as formatted string
    public static String dowFullFormat(DayOfWeek dayOfWeek) {
        return dayOfWeek.getDisplayName(TextStyle.FULL, Locale.CANADA);
    }

    // EFFECTS: returns dayOfWeek as formatted string, shortened
    public static String dowShortFormat(DayOfWeek dayOfWeek) {
        return dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.CANADA);
    }

    // EFFECTS: return an array of all DayOfWeek starting at dayOfWeek
    public static DayOfWeek[] dowValues(DayOfWeek dayOfWeek) {
        int startValue = dayOfWeek.getValue();
        DayOfWeek[] dows = new DayOfWeek[7];
        for (int i = 0; i < 7; i++) {
            int dowSetValue = startValue + i;
            if (dowSetValue > 7) {
                dowSetValue -= 7;
            }
            dows[i] = DayOfWeek.of(dowSetValue);
        }
        return dows;
    }

    // EFFECTS: returns true, for a unit that loops around, if num is between left and right
    public static boolean isBetweenLoop(int num, int left, int right, boolean sameLoop) {
        return ((sameLoop && left <= num && num <= right)
                || (!sameLoop && (left <= num || num <= right)));
    }

    // EFFECTS: get the corresponding start of week (SUNDAY) for date
    public static LocalDate atStartOfWeek(LocalDate date) {
        return date.minusDays(daysBetweenDow(DayOfWeek.SUNDAY, date.getDayOfWeek()));
    }

    // EFFECTS: get the corresponding end of week (SATURDAY) for date
    public static LocalDate atEndOfWeek(LocalDate date) {
        return date.plusDays(daysBetweenDow(date.getDayOfWeek(), DayOfWeek.SATURDAY));
    }

    // EFFECTS: returns the number of days it takes to get from first DayOfWeek to second DayOfWeek
    public static int daysBetweenDow(DayOfWeek first, DayOfWeek second) {
        int firstVal = first.getValue();
        int secondVal = second.getValue();
        if (firstVal > secondVal) {
            return 7 - (firstVal - secondVal);
        } else {
            return secondVal - firstVal;
        }
    }

    // REQUIRES: from <= to
    // EFFECTS: returns an array of int from "from" to "to"
    public static Integer[] intArray(int from, int to) {
        int count = to - from + 1;
        Integer[] intArray = new Integer[count];
        for (int i = 0; i < count; i++) {
            intArray[i] = i + from;
        }
        return intArray;
    }
}
