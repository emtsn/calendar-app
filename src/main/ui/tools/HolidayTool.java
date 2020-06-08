package ui.tools;

import model.FormatterPattern;
import network.PageReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ui.ScheduleEditor;
import ui.exceptions.InvalidCommandException;
import ui.exceptions.InvalidInputException;

import java.io.IOException;
import java.time.LocalDate;

import static ui.tools.EditorUtility.*;

public class HolidayTool extends EditorTool implements FormatterPattern {
    private static final int HOLIDAY_COUNT = 3;
    private JSONArray holidays;

    public HolidayTool(ScheduleEditor editor) {
        super(editor);
    }

    // MODIFIES: this
    // EFFECTS: displays the holidays
    @Override
    public void display() throws InvalidCommandException, InvalidInputException {
        displayTitle("Next Holidays");
        displayHolidays();
        displayClose();
    }

    // MODIFIES: this
    // EFFECTS: shows the next HOLIDAY_COUNT holidays
    public void displayHolidays() {
        try {
            String webpage = PageReader.readWebPage("https://date.nager.at/api/v2/NextPublicHolidays/CA");
            holidays = new JSONArray(webpage);
            for (int i = 0; i < HOLIDAY_COUNT; i++) {
                printNextHoliday(i);
            }
        } catch (IOException ie) {
            System.out.println("Error: failed to get next holidays");
        }
    }

    // EFFECTS: prints the nth holiday in holidays
    public void printNextHoliday(int count) {
        try {
            if (holidays.length() > count) {
                JSONObject nextHoliday = holidays.getJSONObject(count);
                String formattedHoliday = LocalDate.parse(nextHoliday.get("date").toString(),
                        DATE_DASH_FORMATTER).format(DATE_FORMATTER);
                System.out.println("[" + formattedHoliday + "] " + nextHoliday.get("name"));
            }
        } catch (JSONException js) {
            System.out.println("Error: failed to print next holiday");
        }
    }
}
