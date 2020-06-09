package model;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class HolidaysContainerTest {
    private static final String TEST_SAVE_LOCATION = "data/holidays.json";
    private static final boolean TEST_WEB = false;

    private HolidaysContainer h1;
    private HolidaysContainer h2;

    private static List<MultiEvent> createSameDayHolidays(LocalDate date, int count) {
        List<MultiEvent> holidays = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            holidays.add(new MultiEvent(date + "-" + i, date, LocalTime.NOON, LocalTime.NOON));
        }
        return holidays;
    }

    private static Map<Integer, List<MultiEvent>> createHolidaysMap() {
        Map<Integer, List<MultiEvent>> yearHolidays = new HashMap<>();
        List<MultiEvent> holidays1970 = new ArrayList<>();
        holidays1970.addAll(createSameDayHolidays(LocalDate.of(1970, 4, 17), 2));
        holidays1970.addAll(createSameDayHolidays(LocalDate.of(1970, 7, 17), 2));
        holidays1970.addAll(createSameDayHolidays(LocalDate.of(1970, 8, 2), 6));
        holidays1970.addAll(createSameDayHolidays(LocalDate.of(1970, 12, 20), 8));
        List<MultiEvent> holidays2003 = new ArrayList<>();
        holidays2003.addAll(createSameDayHolidays(LocalDate.of(2003, 2, 14), 14));
        holidays2003.addAll(createSameDayHolidays(LocalDate.of(2003, 3, 9), 1));
        holidays2003.addAll(createSameDayHolidays(LocalDate.of(2003, 3, 13), 8));
        holidays2003.addAll(createSameDayHolidays(LocalDate.of(2003, 11, 2), 4));
        List<MultiEvent> holidays2011 = new ArrayList<>();
        holidays2011.addAll(createSameDayHolidays(LocalDate.of(2011, 5, 7), 3));
        holidays2011.addAll(createSameDayHolidays(LocalDate.of(2011, 6, 2), 3));
        holidays2011.addAll(createSameDayHolidays(LocalDate.of(2011, 9, 2), 1));
        holidays2011.addAll(createSameDayHolidays(LocalDate.of(2011, 10, 22), 7));
        yearHolidays.put(1970, holidays1970);
        yearHolidays.put(2003, holidays2003);
        yearHolidays.put(2011, holidays2011);
        return yearHolidays;
    }

    @BeforeEach
    void runBefore() {
        h1 = new HolidaysContainer();
        h2 = new HolidaysContainer();
        h1.setSaveFile(TEST_SAVE_LOCATION);
        h2.setSaveFile(TEST_SAVE_LOCATION);
        h1.setSettings(false, false);
        h1.setSettings(false, false);
    }

    @Test
    void testJson() {
        MultiEvent holiday = HolidaysContainer.getHolidayFrom(
                new JSONObject("{\"name\" : \"New Year's Day\",\"date\" : \"2019-01-01\"}"));
        assertEquals("New Year's Day", holiday.getName());
        assertEquals(LocalDate.of(2019, 1, 1), holiday.getDate());
        List<MultiEvent> holidays = HolidaysContainer.getHolidaysFrom(
                new JSONArray("[{\"name\" : \"New Year's Day\",\"date\" : \"2019-01-01\"}," +
                        "{\"name\" : \"New Year's Eve\",\"date\" : \"2019-12-31\"}]"));
        assertEquals("New Year's Day", holidays.get(0).getName());
        assertEquals(LocalDate.of(2019, 1, 1), holidays.get(0).getDate());
        assertEquals("New Year's Eve", holidays.get(1).getName());
        assertEquals(LocalDate.of(2019, 12, 31), holidays.get(1).getDate());
    }

    @Test
    void testWeb() {
        List<MultiEvent> holidays;
        if (TEST_WEB) {
            h1.setSettings(false, true);
            holidays = h1.getHolidays(2019);
            assertEquals(29, holidays.size());
        }
        holidays = h1.getHolidays(0);
        assertEquals(0, holidays.size());
    }

    @Test
    void testBrokenSave() {
        h1.setSaveFile("data/holidays/json/stuff/data.json");
        assertFalse(h1.save());
        assertFalse(h1.load());
    }

    @Test
    void testSave() {
        Map<Integer, List<MultiEvent>> yearHolidays = new HashMap<>();
        List<MultiEvent> holidays = new ArrayList<>();
        holidays.addAll(
                createSameDayHolidays(LocalDate.of(2014, 3, 20), 8));
        holidays.addAll(
                createSameDayHolidays(LocalDate.of(2014, 6, 2), 4));
        holidays.addAll(
                createSameDayHolidays(LocalDate.of(2014, 11, 13), 6));
        yearHolidays.put(2014, holidays);
        MultiEvent.mergeEvents(holidays);
        h1.setHolidaysMap(yearHolidays);
        h1.save();
        h2.load();
        assertEquals(h1.getHolidaysMap().keySet().size(), h2.getHolidaysMap().keySet().size());
        assertEquals(h1.getHolidays(2014).size(),
                h2.getHolidays(2014).size());
    }

    @Test
    void testFormattedText() {
        h1.setHolidaysMap(createHolidaysMap());
        // holidays before but no dim
        String formatText = h1.getFormattedHolidaysText(2003,
                LocalDate.of(2004, 7, 9), false);
        assertTrue(formatText.startsWith("<html>"));
        assertFalse(formatText.startsWith("<html>" + HolidaysContainer.HTML_DIM_FONT_TAG));
        assertTrue(formatText.endsWith("</html>"));
        assertFalse(formatText.endsWith("</font></html>"));
        // Holidays are before
        formatText = h1.getFormattedHolidaysText(2003,
                LocalDate.of(2004, 7, 9), true);
        assertTrue(formatText.length() > 27);
        assertTrue(formatText.startsWith("<html>" + HolidaysContainer.HTML_DIM_FONT_TAG));
        assertTrue(formatText.endsWith("</font></html>"));
        // holidays are after
        formatText = h1.getFormattedHolidaysText(2003,
                LocalDate.of(2002, 7, 9), true);
        assertTrue(formatText.startsWith("<html>"));
        assertFalse(formatText.startsWith("<html>" + HolidaysContainer.HTML_DIM_FONT_TAG));
        assertTrue(formatText.endsWith("</html>"));
        assertFalse(formatText.endsWith("</font></html>"));
        // holidays some are before some are after
        formatText = h1.getFormattedHolidaysText(2003,
                LocalDate.of(2003, 7, 9), true);
        assertTrue(formatText.startsWith("<html>" + HolidaysContainer.HTML_DIM_FONT_TAG));
        assertTrue(formatText.endsWith("</html>"));
        assertFalse(formatText.endsWith("</font></html>"));
    }

    @Test
    void testHasEvents() {
        h1.setHolidaysMap(createHolidaysMap());
        YearMonth yearMonth = YearMonth.of(2003, 3);
        h1.setSettings(true, false);
        boolean[] hasEvents = h1.createHasHolidaysForYearMonth(yearMonth);
        assertEquals(yearMonth.lengthOfMonth(), hasEvents.length);
        for (int i = 0; i < yearMonth.lengthOfMonth(); i++) {
            if (i + 1 == 9 || i + 1 == 13) {
                assertTrue(hasEvents[i]);
            } else {
                assertFalse(hasEvents[i]);
            }
        }
    }

    @Test
    void testGetForDate() {
        h1.setHolidaysMap(createHolidaysMap());
        List<MultiEvent> forDate1 = h1.getHolidayForDate(LocalDate.of(2011, 6, 2));
        List<MultiEvent> forDate2 = h1.getHolidayForDate(LocalDate.of(2011, 6, 3));
        assertEquals(3, forDate1.size());
        assertEquals(0, forDate2.size());
    }
}
