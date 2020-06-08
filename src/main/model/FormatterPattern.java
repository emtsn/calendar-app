package model;

import java.time.format.DateTimeFormatter;

public interface FormatterPattern {
    String DATE_PATTERN = "yyyy/MM/dd";
    String TIME_PATTERN = "HH:mm";
    DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN + " " + TIME_PATTERN);
    DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
    DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_PATTERN);

    DateTimeFormatter DATE_DASH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // EFFECTS: returns the int as ordinal numbers (1st, 2nd, 3rd...)
    static String ordinal(int i) {
        int num = i % 10;
        if (i == 11 || i == 12 || i == 13) {
            return i + "th";
        } else if (num == 1) {
            return i + "st";
        } else if (num == 2) {
            return i + "nd";
        } else if (num == 3) {
            return i + "rd";
        } else {
            return i + "th";
        }
    }
}
