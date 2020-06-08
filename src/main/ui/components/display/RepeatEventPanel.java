package ui.components.display;

import model.RepeatEvent;
import model.ScheduleEvent;
import ui.VisualEditor;

import javax.swing.*;
import java.awt.*;

public class RepeatEventPanel extends EventPanel {
    private RepeatEvent repeatEvent;

    public RepeatEventPanel(VisualEditor editor, JPanel parent, RepeatEvent repeatEvent,
                            boolean showTimeScale) {
        super(editor, parent);
        this.repeatEvent = repeatEvent;
        initializeComponents(Color.BLACK, showTimeScale);
    }

    @Override
    public void showEventsForCorrespondingTime() {
        //editor.showAllEvents();
    }

    @Override
    public ScheduleEvent getScheduleEvent() {
        return repeatEvent;
    }

    @Override
    public void remove() {
        parent.remove(this);
        editor.removeEvent(repeatEvent);
        editor.revalidate();
        editor.repaint();
    }
}
