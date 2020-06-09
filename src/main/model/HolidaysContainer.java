package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import network.PageReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import utilities.EventUtility;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.*;

import static model.FormatterPattern.DATE_DASH_FORMATTER;

public class HolidaysContainer implements SaveLoadSystem {
    public static final String HTML_DIM_FONT_TAG = "<font color=#999999>";
    public static final String COUNTRY_CODE = "CA";

    private Map<Integer, List<MultiEvent>> yearHolidays;
    private String saveFile;
    private boolean merge;
    private boolean doWeb;

    public HolidaysContainer() {
        yearHolidays = new HashMap<>();
        saveFile = Settings.DEFAULT_HOLIDAYS_FILE;
    }

    @JsonIgnore
    public void setSettings(boolean merge, boolean doWeb) {
        this.merge = merge;
        this.doWeb = doWeb;
    }

    @JsonIgnore
    public void setSaveFile(String saveFile) {
        this.saveFile = saveFile;
    }

    public Map<Integer, List<MultiEvent>> getHolidaysMap() {
        return yearHolidays;
    }

    public void setHolidaysMap(Map<Integer, List<MultiEvent>> yearHolidays) {
        this.yearHolidays = yearHolidays;
    }

    @Override
    public boolean save() {
        boolean success = true;
        try {
            SaveLoadSystem.saveWithJackson(this, saveFile);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to save holidays.");
            success = false;
        }
        return success;
    }

    @Override
    public boolean load() {
        boolean success = true;
        try {
            setHolidaysMap(SaveLoadSystem.loadWithJackson(saveFile, HolidaysContainer.class).getHolidaysMap());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load holidays.");
            success = false;
        }
        return success;
    }

    // EFFECTS: returns a list of holidays from JSONArray
    public static List<MultiEvent> getHolidaysFrom(JSONArray array) {
        List<MultiEvent> retVal = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            retVal.add(getHolidayFrom(array.getJSONObject(i)));
        }
        return retVal;
    }

    // EFFECTS: returns a DateEvent for holiday from JSONObject
    public static MultiEvent getHolidayFrom(JSONObject object) {
        LocalDate holidate = LocalDate.parse(object.get("date").toString(), DATE_DASH_FORMATTER);
        return new MultiEvent(object.get("name").toString(), holidate,
                LocalTime.of(0,0), LocalTime.of(23,59));
    }

    // EFFECTS: returns a formatted text of the holiday with a lighter color for ones before currentDate
    private static String getFormattedHolidayText(MultiEvent holiday, LocalDate currentDate, boolean doDim) {
        boolean isBefore = holiday.getDate().isBefore(currentDate);
        if (doDim && isBefore) {
            return HTML_DIM_FONT_TAG + "[" + holiday.getTimeString() + "] " + holiday.getMergedName() + "</font>";
        } else {
            return "[" + holiday.getTimeString() + "] " + holiday.getMergedName();
        }
    }

    // MODIFIES: this
    // EFFECTS: updates the current holidays for a year if missing or changed and returns it
    public List<MultiEvent> getHolidays(int year) {
        List<MultiEvent> holidays = yearHolidays.get(year);
        if (doWeb && (holidays == null || holidays.size() < 1)) {
            String theURL = "https://date.nager.at/api/v2/PublicHolidays/" + year + "/" + COUNTRY_CODE;
            try {
                holidays = getHolidaysFrom(new JSONArray(PageReader.readWebPage(theURL)));
                yearHolidays.put(year, holidays);
                MultiEvent.mergeEvents(holidays);
                save();
            } catch (IOException | JSONException ie) {
                ie.printStackTrace();
            }
        }
        if (holidays == null) {
            holidays = new ArrayList<>();
            yearHolidays.put(year, holidays);
        }
        MultiEvent.mergeOrSplitEvents(holidays, merge);
        return holidays;
    }

    // MODIFIES: this
    // EFFECTS: returns a list of all dateEvents in schedule that are in yearMonth
    public List<MultiEvent> getHolidaysForYearMonth(YearMonth yearMonth) {
        List<MultiEvent> holidaysForYear = getHolidays(yearMonth.getYear());
        return EventUtility.eventsBetweenDates(yearMonth.atDay(1), yearMonth.atEndOfMonth(),
                holidaysForYear);
    }

    // MODIFIES: this
    // EFFECTS: updates and returns holidays
    public List<MultiEvent> getHolidayForDate(LocalDate date) {
        List<MultiEvent> holidaysForYear = getHolidays(date.getYear());
        return EventUtility.eventsBetweenDates(date, date, holidaysForYear);
    }

    // MODIFIES: this
    // EFFECTS: updates and returns a formatted text of all of a list of holidays
    public String getFormattedHolidaysText(int year, LocalDate currentDate, boolean doDim) {
        List<MultiEvent> holidays = getHolidays(year);
        String text = "<html>";
        for (int i = 0; i < holidays.size(); i++) {
            text = text.concat(getFormattedHolidayText(holidays.get(i), currentDate, doDim));
            if (i < holidays.size() - 1) {
                text = text.concat("<br>");
            }
        }
        text = text.concat("</html>");
        return text;
    }

    // MODIFIES: this
    // EFFECTS: updates and returns an array of booleans for a month, where values correspond to a holiday on that day
    public boolean[] createHasHolidaysForYearMonth(YearMonth yearMonth) {
        boolean[] hasHolidays = new boolean[yearMonth.lengthOfMonth()];
        List<MultiEvent> holidaysForMonth = getHolidaysForYearMonth(yearMonth);
        for (MultiEvent holiday : holidaysForMonth) {
            hasHolidays[holiday.getDate().getDayOfMonth() - 1] = true;
        }
        return hasHolidays;
    }
}
