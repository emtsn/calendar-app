package ui.components.base;

import ui.components.display.EventPanel;
import ui.VisualEditor;

public class EventEditorFrame extends EventCreatorFrame {
    private EventPanel eventPanel;

    public EventEditorFrame(VisualEditor visualEditor, EventPanel eventPanel) {
        super(visualEditor);
        setTitle("Edit Event");
        setBottomButtonText("Apply");
        this.eventPanel = eventPanel;
        setValues(eventPanel.getScheduleEvent());
    }

    // MODIFIES: visualEditor
    // EFFECTS: gives back control to visualEditor
    //          and removes the eventPanel from it
    @Override
    public void giveBackFrame() {
        eventPanel.remove();
        super.giveBackFrame();
    }
}
