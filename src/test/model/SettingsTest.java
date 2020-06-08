package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SettingsTest {
    public static final String TEST_SAVE_FILE = "data/testData.json";
    public static final String TEST_SETTINGS_FILE = "data/settings.json";

    private Settings s1;
    private Settings s2;
    private Settings s3;

    @BeforeEach
    void runBefore() {
        s1 = new Settings();
        s2 = new Settings();
        s3 = new Settings();
        s1.setSettingsFile(TEST_SETTINGS_FILE);
        s2.setSettingsFile(TEST_SETTINGS_FILE);
        s3.setSettingsFile(TEST_SETTINGS_FILE);
    }

    private void checkDefault(Settings s) {
        assertEquals(Settings.DEFAULT_SAVE_FILE, s.getSaveFile());
        assertEquals(Settings.DEFAULT_SHOW_DATE, s.isShowDate());
        assertEquals(Settings.DEFAULT_SHOW_REPEAT, s.isShowRepeat());
        assertEquals(Settings.DEFAULT_DIM_PAST_EVENTS, s.isDimPastEvents());
        assertEquals(Settings.DEFAULT_CONFIRM_TO_DELETE, s.isConfirmToDelete());
        assertEquals(Settings.DEFAULT_SHOW_HOLIDAYS_ON_CALENDAR, s.isShowHolidaysOnCalendar());
        assertEquals(Settings.DEFAULT_SHOW_HOLIDAYS_ON_EVENTS, s.isShowHolidaysOnEvents());
        assertEquals(Settings.DEFAULT_MERGE_HOLIDAY, s.isMergeHoliday());
        assertEquals(Settings.DEFAULT_LOAD_ON_START, s.isLoadOnStart());
        assertEquals(Settings.DEFAULT_BW_MODE, s.isBwMode());
        assertEquals(Settings.DEFAULT_FLIP_TEXT_COLOR, s.isFlipTextColor());
        assertEquals(Settings.DEFAULT_HIGHLIGHT_COLOR, s.getHighlightColor());
        boolean prevBw = s.isBwMode();
        s.setBwMode(false);
        assertEquals(Settings.DEFAULT_HIGHLIGHT_COLOR, s.getUsingColor());
        s.setBwMode(true);
        assertEquals(Settings.DEFAULT_HIGHLIGHT_COLORLESS, s.getUsingColor());
        s.setBwMode(prevBw);
    }

    private void checkOpposite(Settings s) {
        assertEquals(TEST_SAVE_FILE, s.getSaveFile());
        assertEquals(!Settings.DEFAULT_SHOW_DATE, s.isShowDate());
        assertEquals(!Settings.DEFAULT_SHOW_REPEAT, s.isShowDate());
        assertEquals(!Settings.DEFAULT_DIM_PAST_EVENTS, s.isDimPastEvents());
        assertEquals(!Settings.DEFAULT_CONFIRM_TO_DELETE, s.isConfirmToDelete());
        assertEquals(!Settings.DEFAULT_SHOW_HOLIDAYS_ON_CALENDAR, s.isShowHolidaysOnCalendar());
        assertEquals(!Settings.DEFAULT_SHOW_HOLIDAYS_ON_EVENTS, s.isShowHolidaysOnEvents());
        assertEquals(!Settings.DEFAULT_MERGE_HOLIDAY, s.isMergeHoliday());
        assertEquals(!Settings.DEFAULT_LOAD_ON_START, s.isLoadOnStart());
        assertEquals(!Settings.DEFAULT_BW_MODE, s.isBwMode());
        assertEquals(!Settings.DEFAULT_FLIP_TEXT_COLOR, s.isFlipTextColor());
        assertEquals(Color.WHITE, s.getHighlightColor());
        boolean prevBw = s.isBwMode();
        s.setBwMode(false);
        assertEquals(Color.WHITE, s.getUsingColor());
        s.setBwMode(true);
        assertEquals(Settings.DEFAULT_HIGHLIGHT_COLORLESS, s.getUsingColor());
        s.setBwMode(prevBw);
    }

    private void setOpposite(Settings s) {
        s.setSaveFile(TEST_SAVE_FILE);
        s.setShowDate(!Settings.DEFAULT_SHOW_DATE);
        s.setShowRepeat(!Settings.DEFAULT_SHOW_REPEAT);
        s.setDimPastEvents(!Settings.DEFAULT_DIM_PAST_EVENTS);
        s.setConfirmToDelete(!Settings.DEFAULT_CONFIRM_TO_DELETE);
        s.setShowHolidaysOnCalendar(!Settings.DEFAULT_SHOW_HOLIDAYS_ON_CALENDAR);
        s.setShowHolidaysOnEvents(!Settings.DEFAULT_SHOW_HOLIDAYS_ON_EVENTS);
        s.setMergeHoliday(!Settings.DEFAULT_MERGE_HOLIDAY);
        s.setLoadOnStart(!Settings.DEFAULT_LOAD_ON_START);
        s.setBwMode(!Settings.DEFAULT_BW_MODE);
        s.setFlipTextColor(!Settings.DEFAULT_FLIP_TEXT_COLOR);
        s.setHighlightColor(Color.WHITE);
    }

    @Test
    void testGettersSetters() {
        s1 = new Settings();
        checkDefault(s1);
        s1.setSettingsFile(TEST_SETTINGS_FILE);
        assertEquals(TEST_SETTINGS_FILE, s1.getSettingsFile());
        setOpposite(s1);
        checkOpposite(s1);
    }

    @Test
    void testCopy() {
        setOpposite(s1);
        s2 = new Settings(s1);
        s3 = new Settings();
        checkOpposite(s2);
        checkDefault(s3);
        s3.copy(s2);
        checkOpposite(s3);
    }

    @Test
    void testSave() {
        setOpposite(s1);
        s1.save();
        assertTrue(s2.load());
        checkOpposite(s2);
        s1.setSettingsFile("data/testSettings.json");
        assertFalse(s1.load());
        s1.setSettingsFile("data/test/settings.json");
        assertFalse(s1.save());
    }
}
