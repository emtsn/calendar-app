package ui.tools;

import model.DateEvent;
import model.FormatterPattern;
import model.WeeklyEvent;
import ui.ScheduleEditor;
import ui.exceptions.InvalidCommandException;
import ui.exceptions.InvalidInputException;

import java.util.List;

import static ui.tools.EditorUtility.*;

public class EventTool extends EditorTool implements FormatterPattern {

    public EventTool(ScheduleEditor editor) {
        super(editor);
    }

    // MODIFIES: this
    // EFFECTS: asks for input on which type of event to edit
    @Override
    public void display() throws InvalidInputException {
        displayTitle("Event Types");
        try {
            String fileCommand = askString("Enter 'date' or 'repeat'.");
            switch (fileCommand) {
                case "date":
                    displayDateChoice();
                    break;
                case "repeat":
                    displayRepeatChoice();
                    break;
                default:
                    throw new InvalidCommandException(fileCommand);
            }
        } catch (InvalidInputException ie) {
            throw ie;
        } finally {
            displayClose();
        }
    }

    // MODIFIES: this
    // EFFECTS: asks for input what to do with dateEvents
    private void displayDateChoice() throws InvalidInputException {
        displayTitle("Dates");
        String fileCommand = askString("Enter 'add' or 'remove'");
        switch (fileCommand) {
            case "add":
                displayAddDateEvent();
                break;
            case "remove":
                displayRemoveEvent(editor.getSchedule().getDateEvents());
                break;
            default:
                throw new InvalidCommandException(fileCommand);
        }
    }

    // MODIFIES: this
    // EFFECTS: asks for input what to do with repeatEvents
    private void displayRepeatChoice() throws InvalidInputException {
        displayTitle("Repeat");
        String fileCommand = askString("Enter 'add' or 'remove'");
        switch (fileCommand) {
            case "add":
                displayAddRepeatEvent();
                break;
            case "remove":
                displayRemoveEvent(editor.getSchedule().getRepeatEvents());
                break;
            default:
                throw new InvalidCommandException(fileCommand);
        }
    }

    // MODIFIES: this
    // EFFECTS: asks for input on specification of an event and adds it to schedule
    private void displayAddDateEvent() throws InvalidInputException {
        try {
            String setName = askString("Enter name of event: ");
            String setDate = askString("Enter day of event (" + DATE_PATTERN.toUpperCase() + "): ");
            String setStartTime = askString("Enter start time of event (" + TIME_PATTERN.toUpperCase() + "): ");
            String setEndTime = askString("Enter end time of event (" + TIME_PATTERN.toUpperCase() + "): ");
            DateEvent newEvent = new DateEvent(setName, setDate, setStartTime, setEndTime);
            editor.addEvent(newEvent);
            System.out.println(newEvent.getName() + " is now in the schedule for " + newEvent.getTimeString());
        } catch (Exception e) {
            throw new InvalidInputException("Entered invalid time.");
        }
    }

    // MODIFIES: this
    // EFFECTS: asks for input on specification of an repeat event and adds it to schedule
    private void displayAddRepeatEvent() throws InvalidInputException {
        try {
            String setName = askString("Enter name of event: ");
            String setDayOfWeek = askString("Enter day of week of event (e.g. Monday): ");
            String setStartTime = askString("Enter start time of event (" + TIME_PATTERN.toUpperCase() + "): ");
            String setEndTime = askString("Enter end time of event (" + TIME_PATTERN.toUpperCase() + "): ");
            WeeklyEvent newEvent = new WeeklyEvent(setName, setDayOfWeek, setStartTime, setEndTime);
            editor.addEvent(newEvent);
            System.out.println(newEvent.getName() + " is now in the schedule for " + newEvent.getTimeString());
        } catch (Exception e) {
            throw new InvalidInputException("Entered invalid time.");
        }
    }

    // MODIFIES: this
    // EFFECTS: asks for input to remove an event at index in events
    private void displayRemoveEvent(List<?> events) {
        int index = askInt("Enter index of event: ");
        if (events.size() > index) {
            events.remove(index);
        } else {
            System.out.println("ERROR: Entered invalid index.");
        }
    }
}
