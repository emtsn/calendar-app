package ui.tools;

import ui.ScheduleEditor;
import ui.exceptions.InvalidCommandException;
import ui.exceptions.InvalidInputException;

public abstract class EditorTool {
    protected ScheduleEditor editor;

    public EditorTool(ScheduleEditor editor) {
        this.editor = editor;
    }

    // MODIFIES: editor
    // EFFECTS: displays the editor tool
    public abstract void display() throws InvalidCommandException, InvalidInputException;
}
