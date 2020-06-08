package ui.tools;

import model.SaveLoadSystem;
import model.ScheduleContainer;
import ui.ScheduleEditor;
import ui.exceptions.InvalidCommandException;

import java.io.IOException;

import static ui.tools.EditorUtility.*;

public class FileTool extends EditorTool implements SaveLoadSystem {

    public FileTool(ScheduleEditor editor) {
        super(editor);
    }

    // EFFECTS: saves the current schedule of the editor with the set save file, returns true if successful
    @Override
    public boolean save() {
        boolean success = true;
        try {
            SaveLoadSystem.saveWithJackson(editor.getSchedule(), editor.getSaveFile());
            System.out.println("Saving complete.");
        } catch (IOException e) {
            System.out.println("ERROR: save failure");
            success = false;
        }
        return success;
    }

    // MODIFIES: editor
    // EFFECTS: loads the schedule from the save file, returns true if successful
    @Override
    public boolean load() {
        boolean success = true;
        try {
            ScheduleContainer schedule = SaveLoadSystem.loadWithJackson(editor.getSaveFile(), ScheduleContainer.class);
            schedule.sort();
            editor.setSchedule(schedule);
            System.out.println("Loading complete.");
        } catch (IOException e) {
            System.out.println("ERROR: load failure");
            success = false;
        }
        return success;
    }

    // MODIFIES: this
    // EFFECTS: asks for input for saving and loading
    @Override
    public void display() throws InvalidCommandException {
        displayTitle("File");
        String fileCommand = askString("Enter 'save' or 'load'.");
        try {
            if (fileCommand.equals("save")) {
                save();
            } else if (fileCommand.equals("load")) {
                load();
            } else {
                throw new InvalidCommandException(fileCommand);
            }
        } finally {
            displayClose();
        }
    }
}
