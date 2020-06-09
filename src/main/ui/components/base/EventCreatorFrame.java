package ui.components.base;

import model.*;
import ui.VisualEditor;
import ui.components.input.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class EventCreatorFrame extends JFrame {
    private enum EventType {
        Date,
        Weekly,
        Monthly;

        public static String[] names() {
            ArrayList<String> names = new ArrayList<>();
            for (EventType eventType : EventType.values()) {
                names.add(eventType.name());
            }
            return names.toArray(new String[EventType.values().length]);
        }

        public String toString() {
            return this.name();
        }
    }

    private static final int HEIGHT = 250;
    private static final int WIDTH = 400;

    private VisualEditor visualEditor;

    private JPanel warningPanel;
    private JLabel warningLabel;

    private JComboBox<String> typeBox;
    private JButton bottomButton;

    private NameInputPanel setNameInputPanel;
    private DateInputPanel setDateInputPanel;
    private DayOfWeekInputPanel setDayOfWeekInputPanel;
    private DayOfMonthInputPanel setDayOfMonthInputPanel;
    private TimeInputPanel setStartTimeInputPanel;
    private TimeInputPanel setEndTimeInputPanel;


    public EventCreatorFrame(VisualEditor visualEditor) {
        this.visualEditor = visualEditor;
        setTitle("Create New Event");
        getContentPane().setBackground(VisualEditor.BG_PANEL_COLOR);
        initializeComponents();
        initializeDisplay();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    // MODIFIES: this
    // EFFECTS: initializes the display of the panel
    private void initializeDisplay() {
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setResizable(false);
        initializeEditor();
    }

    // MODIFIES: this
    // EFFECTS: initializes the components of the panel
    private void initializeComponents() {
        warningLabel = new JLabel();
        warningLabel.setMinimumSize(new Dimension(20, warningLabel.getHeight()));
        warningPanel = new JPanel();
        warningPanel.add(warningLabel);
        warningPanel.setOpaque(false);
        initializeEditingPanels();
    }

    // MODIFIES: this
    // EFFECTS: initializes the editing panels of the panel
    private void initializeEditingPanels() {
        setNameInputPanel = new NameInputPanel();
        setDateInputPanel = new DateInputPanel("Day:", visualEditor.pickingDate());
        setDayOfWeekInputPanel = new DayOfWeekInputPanel(visualEditor.pickingDate());
        setDayOfMonthInputPanel = new DayOfMonthInputPanel(visualEditor.pickingDate());
        setStartTimeInputPanel = new TimeInputPanel("Start Time:");
        setEndTimeInputPanel = new TimeInputPanel("End Time:");
    }

    // MODIFIES: this
    // EFFECTS: initializes the editor (add behaviour) of the panel
    private void initializeEditor() {
        bottomButton = new JButton("Add");
        bottomButton.addActionListener(e -> createEventFromEditor());
        getContentPane().add(bottomButton, BorderLayout.PAGE_END);
        getContentPane().add(createEventPanel(), BorderLayout.CENTER);
    }

    // MODIFIES: this
    // EFFECTS: sets the editing values to the values of the scheduleEvent
    public void setValues(ScheduleEvent scheduleEvent) {
        if (scheduleEvent instanceof MonthlyEvent) {
            typeBox.setSelectedItem(EventType.Monthly.name());
            setDayOfMonthInputPanel.setDayOfMonth(((MonthlyEvent) scheduleEvent).getDayOf());
        } else if (scheduleEvent instanceof WeeklyEvent) {
            typeBox.setSelectedItem(EventType.Weekly.name());
            setDayOfWeekInputPanel.setDayOfWeek(((WeeklyEvent) scheduleEvent).getDayOfWeek());
        } else {
            typeBox.setSelectedItem(EventType.Date.name());
            setDateInputPanel.setDate(((DateEvent) scheduleEvent).getDate());
        }
        setNameInputPanel.setName(scheduleEvent.getName());
        setStartTimeInputPanel.setTime(scheduleEvent.getStartTime());
        setEndTimeInputPanel.setTime(scheduleEvent.getEndTime());
    }

    // EFFECTS: returns a panel for creating a new event
    private JPanel createEventPanel() {
        JPanel eventPanel = new JPanel();
        typeBox = new JComboBox<>(EventType.names());
        JPanel typePanel = createTypePanel(typeBox);
        typeBox.setMaximumSize(typeBox.getPreferredSize());
        eventPanel.setLayout(new BoxLayout(eventPanel, BoxLayout.Y_AXIS));
        eventPanel.add(typeBox, BorderLayout.PAGE_START);
        eventPanel.add(setNameInputPanel);
        eventPanel.add(typePanel);
        eventPanel.add(setStartTimeInputPanel);
        eventPanel.add(setEndTimeInputPanel);
        eventPanel.add(warningPanel);
        eventPanel.setOpaque(false);
        return eventPanel;
    }

    // EFFECTS: returns a panel that switches between a property between types of events
    //          (Date for DateEvent, DayOfWeek for WeeklyEvent, etc.)
    private JPanel createTypePanel(JComboBox<String> typePicker) {
        JPanel typePanel = new JPanel();
        CardLayout layout = new CardLayout();
        typePanel.setLayout(layout);
        typePicker.addItemListener(e -> layout.show(typePanel, (String) e.getItem()));
        typePanel.add(setDateInputPanel, EventType.Date.name());
        typePanel.add(setDayOfWeekInputPanel, EventType.Weekly.name());
        typePanel.add(setDayOfMonthInputPanel, EventType.Monthly.name());
        typePanel.setMaximumSize(typePanel.getPreferredSize());
        typePanel.setOpaque(false);
        return typePanel;
    }

    // MODIFIES: this
    // EFFECTS: changes the warning text on the panel
    private void changeWarning(String setText) {
        warningLabel.setText(setText);
        warningLabel.setMaximumSize(warningLabel.getPreferredSize());
    }

    // EFFECTS: returns a DateEvent from currently set editing panels
    private DateEvent createDateEvent() {
        return new DateEvent(setNameInputPanel.getName(), setDateInputPanel.getDate(),
                setStartTimeInputPanel.getTime(), setEndTimeInputPanel.getTime());
    }

    // EFFECTS: returns a WeeklyEvent from currently set editing panels
    private WeeklyEvent createWeeklyEvent() {
        return new WeeklyEvent(setNameInputPanel.getName(), setDayOfWeekInputPanel.getDayOfWeek(),
                setStartTimeInputPanel.getTime(), setEndTimeInputPanel.getTime());
    }

    // EFFECTS: returns a MonthlyEvent from currently set editing panels
    private MonthlyEvent createMonthlyEvent() {
        return new MonthlyEvent(setNameInputPanel.getName(), setDayOfMonthInputPanel.getDayOfMonth(),
                setStartTimeInputPanel.getTime(), setEndTimeInputPanel.getTime());
    }

    // EFFECTS: creates an event of type from currently set editing panels
    private void createEventFromEditor() {
        if (setNameInputPanel.getName().length() < 1) {
            changeWarning("*Add a name for the event");
            return;
        }
        changeWarning("");
        switch ((String) typeBox.getSelectedItem()) {
            case "Date":
                visualEditor.addEvent(createDateEvent());
                break;
            case "Weekly":
                visualEditor.addEvent(createWeeklyEvent());
                break;
            default:
                visualEditor.addEvent(createMonthlyEvent());
                break;
        }
        giveBackFrame();
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    // MODIFIES: this
    // EFFECTS: returns control to the visualEditor and adds changes to it when needed
    public void giveBackFrame() {
        if (visualEditor != null) {
            visualEditor.refresh();
        }
    }

    // MODIFIES: this
    // EFFECTS: change the text of the bottom button
    public void setBottomButtonText(String text) {
        bottomButton.setText(text);
    }
}
