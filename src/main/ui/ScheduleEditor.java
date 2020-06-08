package ui;

import model.*;
import ui.exceptions.InvalidCommandException;
import ui.exceptions.InvalidInputException;
import ui.tools.*;

import java.util.*;

import static ui.tools.EditorUtility.*;

public class ScheduleEditor implements FormatterPattern {

    private HashMap<String, EditorTool> tools;
    private ScheduleContainer currentSchedule;
    private HashMap<String, ScheduleContainer> schedules;
    private String saveFile;

    private EventTool eventTool;
    private FileTool fileTool;
    private HolidayTool holidayTool;

    public ScheduleEditor() {
        createTools();
        schedules = new HashMap<>();
        currentSchedule = new ScheduleContainer();
        saveFile = "data/scheduleData.json";
    }

    // MODIFIES: this
    // EFFECTS: creates all of the tool and adds to tools
    public void createTools() {
        tools = new HashMap<>();
        eventTool = new EventTool(this);
        fileTool = new FileTool(this);
        holidayTool = new HolidayTool(this);
        tools.put("events", eventTool);
        tools.put("file", fileTool);
        tools.put("holidays", holidayTool);
    }

    // MODIFIES: this
    // EFFECTS: sets schedule to s
    public void setSchedule(ScheduleContainer s) {
        currentSchedule = s;
    }

    // MODIFIES: this
    // EFFECTS: returns schedule
    public ScheduleContainer getSchedule() {
        return currentSchedule;
    }

    // MODIFIES: this
    // EFFECTS: clears schedule
    public void clearSchedule() {
        currentSchedule = new ScheduleContainer();
    }

    // MODIFIES: this
    // EFFECTS: returns size of schedule
    public int getScheduleSize() {
        return currentSchedule.getSize();
    }

    // MODIFIES: this
    // EFFECTS: sets saveFile to fileName
    public void setSaveFile(String fileName) {
        saveFile = fileName;
    }

    // EFFECTS: gets saveFile
    public String getSaveFile() {
        return saveFile;
    }

    // MODIFIES: this
    // EFFECTS: adds DateEvent to currentSchedule
    public void addEvent(DateEvent d) {
        currentSchedule.addEvent(d);
    }

    // MODIFIES: this
    // EFFECTS: adds WeeklyEvent to currentSchedule
    public void addEvent(WeeklyEvent w) {
        currentSchedule.addEvent(w);
    }

    // MODIFIES: this
    // EFFECTS: adds MonthlyEvent to currentSchedule
    public void addEvent(MonthlyEvent m) {
        currentSchedule.addEvent(m);
    }

    // MODIFIES: this
    // EFFECTS: saves schedule to data
    public void save() {
        fileTool.save();
    }

    // MODIFIES: this
    // EFFECTS: loads schedule from data
    public void load() {
        fileTool.load();
    }

    // MODIFIES: this
    // EFFECTS: runs editor to executes input commands
    private void runEditor() {
        while (true) {
            String command = askString("Enter command (type 'help' for list):");
            if (command.equals("quit")) {
                break;
            } else if (command.equals("help")) {
                displayHelp();
            } else {
                runCommand(command);
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: runs the appropriate method for the given command
    private void runCommand(String command) {
        try {
            EditorTool editorTool = tools.get(command);
            if (editorTool != null) {
                editorTool.display();
            } else if (command.equals("schedules")) {
                displaySchedules();
            } else if (command.equals("sort")) {
                displaySort();
            } else if (command.equals("make")) {
                displayNewSchedule();
            } else {
                throw new InvalidCommandException(command);
            }
        } catch (InvalidInputException ie) {
            System.out.println("ERROR: " + ie.getMessage());
        }
    }

    // EFFECTS: prints all of the events in all schedules
    private void displaySchedules() {
        displayTitle("Schedules");
        displaySchedule("Current", currentSchedule);
        for (Map.Entry<String, ScheduleContainer> me : schedules.entrySet()) {
            displaySchedule(me.getKey(), me.getValue());
        }
    }

    // EFFECTS: prints a single schedule
    private void displaySchedule(String name, ScheduleContainer s) {
        System.out.println(">>" + name + "<<");
        if (s.getSize() == 0) {
            System.out.println("Nothing in schedule.");
            return;
        }
        for (ScheduleEvent se : s.getScheduleEvents()) {
            System.out.println("[" + se.getTimeString() + "] " + se.getName());
        }
    }

    // MODIFIES: this
    // EFFECTS: names currentSchedule and adds to schedules, starts new schedule
    public void displayNewSchedule() {
        displayTitle("Make");
        String fileCommand = askString("Name current schedule");
        schedules.put(fileCommand, currentSchedule);
        currentSchedule = new ScheduleContainer();
        System.out.println("Saved schedule as '" + fileCommand + "' of size "
                + schedules.get(fileCommand).getSize());
    }

    // MODIFIES: this
    // EFFECTS: sorts schedule's events
    public void displaySort() {
        currentSchedule.sort();
        System.out.println("Sorted all events by time.");
    }

    // EFFECTS: prints the help menu
    private void displayHelp() {
        displayTitle("All commands");
        System.out.println("- help");
        System.out.println("- events");
        System.out.println(" - add");
        System.out.println(" - remove");
        System.out.println("- sort");
        System.out.println("- schedules");
        System.out.println("- file");
        System.out.println(" - save");
        System.out.println(" - load");
        System.out.println("- make");
    }

    public static void main(String[] args) {
        ScheduleEditor scheduleEditor = new ScheduleEditor();
        scheduleEditor.runEditor();
    }
}
