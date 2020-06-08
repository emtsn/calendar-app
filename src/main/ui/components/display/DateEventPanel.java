package ui.components.display;

import model.DateEvent;
import model.ScheduleEvent;
import ui.VisualEditor;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class DateEventPanel extends EventPanel {
    private DateEvent dateEvent;

    public DateEventPanel(VisualEditor editor, JPanel parent, DateEvent dateEvent,
                          LocalDate currentDate, boolean doDim, boolean atTimeScale) {
        super(editor, parent);
        this.dateEvent = dateEvent;
        initializeComponents(doDim && dateEvent.getDate().isBefore(currentDate) ? Color.GRAY : Color.BLACK,
                atTimeScale);
    }

    @Override
    public void showEventsForCorrespondingTime() {
        editor.promptJumpToDate(dateEvent.getDate());
    }

    @Override
    public ScheduleEvent getScheduleEvent() {
        return dateEvent;
    }

    // MODIFIES: this
    // EFFECTS: remove the panel with the event from editor
    @Override
    public void remove() {
        parent.remove(this);
        editor.removeEvent(dateEvent);
        editor.revalidate();
        editor.repaint();
    }
}
