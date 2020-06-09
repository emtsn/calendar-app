package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import utilities.ColorUtility;

import java.awt.*;
import java.io.IOException;

public class Settings implements SaveLoadSystem {
    public static final String DEFAULT_DIRECTORY = "calendar_data";
    public static final String DEFAULT_SAVE_FILE = DEFAULT_DIRECTORY + "/saveFile.json";
    public static final String DEFAULT_SETTINGS_FILE = DEFAULT_DIRECTORY + "/settings.json";
    public static final String DEFAULT_HOLIDAYS_FILE = DEFAULT_DIRECTORY + "/holidays.json";
    public static final boolean DEFAULT_SHOW_DATE = true;
    public static final boolean DEFAULT_SHOW_REPEAT = true;
    public static final boolean DEFAULT_DIM_PAST_EVENTS = true;
    public static final boolean DEFAULT_CONFIRM_TO_DELETE = true;
    public static final boolean DEFAULT_SHOW_HOLIDAYS_ON_CALENDAR = true;
    public static final boolean DEFAULT_SHOW_HOLIDAYS_ON_EVENTS = true;
    public static final boolean DEFAULT_MERGE_HOLIDAY = true;
    public static final boolean DEFAULT_LOAD_HOLIDAYS_FROM_WEB = false;
    public static final boolean DEFAULT_LOAD_ON_START = false;
    public static final boolean DEFAULT_BW_MODE = false;
    public static final boolean DEFAULT_FLIP_TEXT_COLOR = true;
    public static final Color DEFAULT_HIGHLIGHT_COLOR = ColorUtility.COLOR_3;
    public static final Color DEFAULT_HIGHLIGHT_COLORLESS = Color.GRAY;

    private String settingsFile;
    private String saveFile;
    private boolean showDate;
    private boolean showRepeat;
    private boolean dimPastEvents;
    private boolean confirmToDelete;
    private boolean showHolidaysOnCalendar;
    private boolean showHolidaysOnEvents;
    private boolean mergeHoliday;
    private boolean loadHolidaysFromWeb;
    private boolean loadOnStart;
    private boolean bwMode;
    private boolean flipTextColor;
    private Color highlightColor;

    public Settings() {
        setToDefault();
    }

    public Settings(Settings settings) {
        copy(settings);
    }

    // MODIFIES: this
    // EFFECTS: sets all settings to default
    @JsonIgnore
    public void setToDefault() {
        settingsFile = DEFAULT_SETTINGS_FILE;
        saveFile = DEFAULT_SAVE_FILE;
        showDate = DEFAULT_SHOW_DATE;
        showRepeat = DEFAULT_SHOW_REPEAT;
        dimPastEvents = DEFAULT_DIM_PAST_EVENTS;
        confirmToDelete = DEFAULT_CONFIRM_TO_DELETE;
        showHolidaysOnCalendar = DEFAULT_SHOW_HOLIDAYS_ON_CALENDAR;
        showHolidaysOnEvents = DEFAULT_SHOW_HOLIDAYS_ON_EVENTS;
        mergeHoliday = DEFAULT_MERGE_HOLIDAY;
        loadHolidaysFromWeb = DEFAULT_LOAD_HOLIDAYS_FROM_WEB;
        loadOnStart = DEFAULT_LOAD_ON_START;
        bwMode = DEFAULT_BW_MODE;
        flipTextColor = DEFAULT_FLIP_TEXT_COLOR;
        highlightColor = DEFAULT_HIGHLIGHT_COLOR;
    }

    // MODIFIES: this
    // EFFECTS: copies the settings of another settings
    public void copy(Settings settings) {
        settingsFile = settings.settingsFile;
        saveFile = settings.saveFile;
        showDate = settings.showDate;
        showRepeat = settings.showRepeat;
        dimPastEvents = settings.dimPastEvents;
        confirmToDelete = settings.confirmToDelete;
        showHolidaysOnCalendar = settings.showHolidaysOnCalendar;
        showHolidaysOnEvents = settings.showHolidaysOnEvents;
        mergeHoliday = settings.mergeHoliday;
        loadHolidaysFromWeb = settings.loadHolidaysFromWeb;
        loadOnStart = settings.loadOnStart;
        bwMode = settings.bwMode;
        flipTextColor = settings.flipTextColor;
        highlightColor = settings.highlightColor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Settings)) {
            return false;
        }
        Settings settings = (Settings) o;
        return ((showDate == settings.showDate) && (showRepeat == settings.showRepeat)
                && (dimPastEvents == settings.dimPastEvents) && (confirmToDelete == settings.confirmToDelete)
                && (showHolidaysOnCalendar == settings.showHolidaysOnCalendar)
                && (showHolidaysOnEvents == settings.showHolidaysOnEvents)
                && (mergeHoliday == settings.mergeHoliday) && (loadHolidaysFromWeb == settings.loadHolidaysFromWeb)
                && (loadOnStart == settings.loadOnStart) && (bwMode == settings.bwMode)
                && (flipTextColor == settings.flipTextColor) && (saveFile.equals(settings.saveFile))
                && highlightColor.equals(settings.highlightColor));
    }

    @Override
    public int hashCode() {
        int result = saveFile.hashCode();
        result = 31 * result + (showDate ? 1 : 0);
        result = 31 * result + (showRepeat ? 1 : 0);
        result = 31 * result + (dimPastEvents ? 1 : 0);
        result = 31 * result + (confirmToDelete ? 1 : 0);
        result = 31 * result + (showHolidaysOnCalendar ? 1 : 0);
        result = 31 * result + (showHolidaysOnEvents ? 1 : 0);
        result = 31 * result + (mergeHoliday ? 1 : 0);
        result = 31 * result + (loadHolidaysFromWeb ? 1 : 0);
        result = 31 * result + (loadOnStart ? 1 : 0);
        result = 31 * result + (bwMode ? 1 : 0);
        result = 31 * result + (flipTextColor ? 1 : 0);
        result = 31 * result + highlightColor.hashCode();
        return result;
    }

    // EFFECTS: saves this settings to settingsFile, returns true if successful
    @Override
    public boolean save() {
        boolean success = true;
        try {
            SaveLoadSystem.saveWithJackson(this, settingsFile);
        } catch (IOException e) {
            System.out.println("Failed to save settings.");
            success = false;
        }
        return success;
    }

    // MODIFIES: this
    // EFFECTS: loads settings from settingsFile, returns true if successful
    @Override
    public boolean load() {
        boolean success = true;
        Settings prevSettings = new Settings(this);
        try {
            copy(SaveLoadSystem.loadWithJackson(settingsFile, Settings.class));
        } catch (IOException e) {
            System.out.println("Failed to load settings.");
            copy(prevSettings);
            success = false;
        }
        return success;
    }

    @JsonIgnore
    public Color getUsingColor() {
        if (bwMode) {
            return DEFAULT_HIGHLIGHT_COLORLESS;
        }
        return highlightColor;
    }

    public String getSettingsFile() {
        return settingsFile;
    }

    public String getSaveFile() {
        return saveFile;
    }

    public boolean isShowDate() {
        return showDate;
    }

    public boolean isShowRepeat() {
        return showRepeat;
    }

    public boolean isDimPastEvents() {
        return dimPastEvents;
    }

    public boolean isConfirmToDelete() {
        return confirmToDelete;
    }

    public boolean isShowHolidaysOnCalendar() {
        return showHolidaysOnCalendar;
    }

    public boolean isShowHolidaysOnEvents() {
        return showHolidaysOnEvents;
    }

    public boolean isMergeHoliday() {
        return mergeHoliday;
    }

    public boolean isLoadHolidaysFromWeb() {
        return loadHolidaysFromWeb;
    }

    public boolean isLoadOnStart() {
        return loadOnStart;
    }

    public boolean isBwMode() {
        return bwMode;
    }

    public Color getHighlightColor() {
        return highlightColor;
    }

    public boolean isFlipTextColor() {
        return flipTextColor;
    }

    public void setSettingsFile(String settingsFile) {
        this.settingsFile = settingsFile;
    }

    public void setSaveFile(String saveFile) {
        this.saveFile = saveFile;
    }

    public void setShowDate(boolean showDate) {
        this.showDate = showDate;
    }

    public void setShowRepeat(boolean showRepeat) {
        this.showRepeat = showRepeat;
    }

    public void setDimPastEvents(boolean dimPastEvents) {
        this.dimPastEvents = dimPastEvents;
    }

    public void setConfirmToDelete(boolean confirmToDelete) {
        this.confirmToDelete = confirmToDelete;
    }

    public void setShowHolidaysOnCalendar(boolean showHolidaysOnCalendar) {
        this.showHolidaysOnCalendar = showHolidaysOnCalendar;
    }

    public void setShowHolidaysOnEvents(boolean showHolidaysOnEvents) {
        this.showHolidaysOnEvents = showHolidaysOnEvents;
    }

    public void setMergeHoliday(boolean mergeHoliday) {
        this.mergeHoliday = mergeHoliday;
    }

    public void setLoadHolidaysFromWeb(boolean loadHolidaysFromWeb) {
        this.loadHolidaysFromWeb = loadHolidaysFromWeb;
    }

    public void setLoadOnStart(boolean loadOnStart) {
        this.loadOnStart = loadOnStart;
    }

    public void setBwMode(boolean bwMode) {
        this.bwMode = bwMode;
    }

    public void setFlipTextColor(boolean flipTextColor) {
        this.flipTextColor = flipTextColor;
    }

    public void setHighlightColor(Color highlightColor) {
        this.highlightColor = highlightColor;
    }
}
