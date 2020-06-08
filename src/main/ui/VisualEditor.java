package ui;

import model.*;
import ui.components.base.FindFrame;
import ui.components.display.DateEventPanel;
import ui.components.display.EventPanel;
import ui.components.display.RepeatEventPanel;
import ui.components.display.TitleLabel;
import ui.components.base.EventCreatorFrame;
import ui.components.base.EventEditorFrame;
import ui.components.input.ColorInputPanel;
import utilities.TimeUtility;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.List;

import static utilities.TimeUtility.dowValues;

public class VisualEditor extends JFrame implements SaveLoadSystem, FormatterPattern {
    private enum Display {
        MonthCalendar,
        AllEvents,
        EventsForDate,
        Holidays,
        Settings,
        Help,
    }

    public static final int HEIGHT = 500;
    public static final int WIDTH = 500;

    private static final String HELP_TEXT
            = "<html>- Use <i>File/New</i> to create a new schedule<br>"
            + "- Use <i>File/Open</i> to open an existing schedule<br>"
            + "- Use <i>File/Save</i> to save the current schedule<br>"
            + "- Use <i>File/Save As</i> to save the current schedule at a directory<br>"
            + "- Use <i>File/Settings</i> to view settings<br>"
            + "&nbsp;&nbsp;- Use <i>Select</i> to find a new location for your calendar<br>"
            + "&nbsp;&nbsp;- The settings file will always be in \"" + Settings.DEFAULT_SETTINGS_FILE + "\"<br>"
            + "- Use <i>Edit/New Event</i> to create a new event<br>"
            + "- Use <i>View/Today</i> to view the events for today<br>"
            + "- Use <i>View/Events</i> to view all events<br>"
            + "- Use <i>View/Calendar</i> to view the calendar<br>"
            + "&nbsp;&nbsp;- Use left/right button below to switch months<br>"
            + "&nbsp;&nbsp;- Click on a date to view the date's events<br>"
            + "- Use <i>View/Holidays</i> to view the holidays for this year";

    private static final int DATE_BOX_GAP = 0;
    private static final boolean DATE_BOX_DRAW_FULL_BORDER = true;

    public static final Color DEFAULT_PANEL_COLOR = new Color(238, 238, 238, 255);

    public static final Color TITLE_LABEL_COLOR = Color.WHITE;
    public static final Color TITLE_LABEL_BORDER_COLOR = Color.GRAY;
    public static final Color TITLE_LABEL_TEXT_COLOR = Color.BLACK;
    public static final Color SUBTITLE_LABEL_COLOR = Color.WHITE;
    public static final Color BG_PANEL_COLOR = DEFAULT_PANEL_COLOR;
    public static final Color TOOL_PANEL_COLOR = TITLE_LABEL_COLOR;
    public static final Color TOOL_PANEL_BORDER_COLOR = TITLE_LABEL_BORDER_COLOR;
    public static final Color ELEMENT_COLOR = Color.WHITE;
    public static final Color TODAY_HL_COLOR = Color.LIGHT_GRAY;

    private Display currentDisplay;
    private LocalDate selectedDate;
    private LocalDate selectedEndDate;

    private ScheduleContainer currentSchedule;
    private Component centreComponent;

    private HolidaysContainer currentHolidays;
    private int selectedHolidaysYear;

    private Settings currentSettings;

    public VisualEditor(String name) {
        super(name);
        File file = new File(Settings.DEFAULT_DIRECTORY);
        file.mkdirs();
        initializeFields();
        initializeBaseDisplay();
        initializeSchedule();
        initializeMenu();
        showDisplay(currentDisplay);
        setVisible(true);
    }

    // MODIFIES: this
    // EFFECTS: initializes the fields
    private void initializeFields() {
        selectedDate = getCurrentDate();
        selectedEndDate = selectedDate;
        selectedHolidaysYear = getCurrentDate().getYear();
        currentSchedule = new ScheduleContainer();
        currentDisplay = Display.MonthCalendar;
        currentHolidays = new HolidaysContainer();
        currentSettings = new Settings();
        currentSettings.load();
        currentHolidays.load();
    }

    // MODIFIES: this
    // EFFECTS: initializes the schedule
    private void initializeSchedule() {
        if (currentSettings.isLoadOnStart()) {
            if (!currentSettings.getSaveFile().isEmpty()) {
                load();
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: initializes the base display of the frame
    private void initializeBaseDisplay() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        centreComponent = new JPanel();
        setResizable(false);
    }

    // MODIFIES: this
    // EFFECTS: initializes the top menu bar
    private void initializeMenu() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        menuBar.add(createEditMenu());
        menuBar.add(createViewMenu());
        menuBar.add(createHelpMenu());
        getContentPane().add(menuBar, BorderLayout.PAGE_START);
    }

    // EFFECTS: returns a LocalDate set to the current date
    public LocalDate getCurrentDate() {
        return LocalDate.now();
    }

    // EFFECTS: returns a LocalDate to set initial date editing for
    public LocalDate pickingDate() {
        if (currentDisplay.equals(Display.EventsForDate)) {
            return selectedDate;
        } else if (currentDisplay.equals(Display.MonthCalendar)) {
            return selectedYearMonth().atDay(1);
        } else {
            return getCurrentDate();
        }
    }

    // EFFECTS: return a year month corresponding to the selectedDate
    public YearMonth selectedYearMonth() {
        return YearMonth.of(selectedDate.getYear(), selectedDate.getMonth());
    }

    // MODIFIES: this
    // EFFECTS:: adds dateEvent to currentSchedule
    public void addEvent(DateEvent dateEvent) {
        currentSchedule.addEvent(dateEvent);
    }

    // MODIFIES: this
    // EFFECTS:: adds repeatEvent to currentSchedule
    public void addEvent(RepeatEvent repeatEvent) {
        currentSchedule.addEvent(repeatEvent);
    }

    // MODIFIES: this
    // EFFECTS: removes dateEvent from currentSchedule, show error if failed
    public void removeEvent(DateEvent dateEvent) {
        if (!currentSchedule.removeEvent(dateEvent)) {
            showError("Failed to remove event.");
        }
    }

    // MODIFIES: this
    // EFFECTS: removes repeatEvent from currentSchedule, show error if failed
    public void removeEvent(RepeatEvent repeatEvent) {
        if (!currentSchedule.removeEvent(repeatEvent)) {
            showError("Failed to remove event.");
        }
    }

    // EFFECTS: saves schedule onto saveFile, returns true if successful
    @Override
    public boolean save() {
        boolean success = true;
        try {
            SaveLoadSystem.saveWithJackson(currentSchedule, currentSettings.getSaveFile());
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to save schedule. Please check the save file location in settings.");
            success = false;
        }
        return success;
    }

    // MODIFIES: this
    // EFFECTS: loads schedule from saveFile, returns true if successful
    @Override
    public boolean load() {
        boolean success = true;
        try {
            currentSchedule = SaveLoadSystem.loadWithJackson(currentSettings.getSaveFile(), ScheduleContainer.class);
            currentSchedule.sort();
            refresh();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load schedule. Please check the save file location in settings.");
            success = false;
        }
        return success;
    }

    // EFFECTS: shows an error dialogue
    private void showError(String text) {
        JOptionPane.showMessageDialog(this, text, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // EFFECTS: shows an confirm dialogue
    private boolean showConfirm(String text) {
        int retVal = JOptionPane.showConfirmDialog(this, text, "Confirm...", JOptionPane.YES_NO_OPTION);
        return retVal == 0;
    }

    // EFFECTS: brings up prompt to choose a file location
    public String openFilePrompt(String initialPath, boolean isSave) {
        JFileChooser fileChooser = new JFileChooser(new File(initialPath).exists()
                ? Settings.DEFAULT_DIRECTORY : initialPath);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(new FileNameExtensionFilter("JSON Files", "json"));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int option = isSave ? fileChooser.showSaveDialog(this) : fileChooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            return filePromptProcessFile(fileChooser.getSelectedFile(), isSave);
        }
        return "";
    }

    // EFFECTS: returns the file path after checking for errors
    private String filePromptProcessFile(File file, boolean isSave) {
        if (file.getAbsolutePath().endsWith(Settings.DEFAULT_SETTINGS_FILE)
                || file.getAbsolutePath().endsWith(Settings.DEFAULT_HOLIDAYS_FILE)) {
            showError("Please do not pick the settings or holidays file.");
            return "";
        }
        if (isSave && file.exists()
                && !showConfirm("You are attempting to overwrite an existing file. Please confirm.")) {
            return "";
        }
        return file.getAbsolutePath() + (file.getAbsolutePath().endsWith(".json") ? "" : ".json");
    }

    // EFFECTS: creates and returns the file section of the menu bar
    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("File");
        JMenuItem newFileItem = new JMenuItem("New");
        JMenuItem openMenuItem = new JMenuItem("Open...");
        JMenuItem saveMenuItem = new JMenuItem("Save");
        JMenuItem saveAsMenuItem = new JMenuItem("Save As...");
        JMenuItem settingsMenuItem = new JMenuItem("Settings");
        newFileItem.addActionListener(e -> promptNewSchedule());
        openMenuItem.addActionListener(e -> promptOpenSchedule());
        saveMenuItem.addActionListener(e -> promptSaveSchedule(false));
        saveAsMenuItem.addActionListener(e -> promptSaveSchedule(true));
        settingsMenuItem.addActionListener(e -> showDisplay(Display.Settings));
        fileMenu.add(newFileItem);
        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(saveAsMenuItem);
        fileMenu.add(settingsMenuItem);
        return fileMenu;
    }

    // MODIFIES: this
    // EFFECTS: shows a prompt to create new schedule, clears if picked yes
    private void promptNewSchedule() {
        if (showConfirm("Create new schedule? This will delete you current schedule.")) {
            currentSchedule.clearScheduleEvents();
            currentSettings.setSaveFile("");
            currentSettings.save();
            refresh();
        }
    }

    // EFFECTS: shows a file prompt to create new schedule, clears if picked yes
    private void promptOpenSchedule() {
        String retVal = openFilePrompt(currentSettings.getSaveFile(), false);
        if (retVal.equals("")) {
            return;
        }
        currentSettings.setSaveFile(retVal);
        currentSettings.save();
        load();
    }

    // EFFECTS: shows a file prompt to save schedule to location, then saves at location if valid
    private void promptSaveSchedule(boolean forcePickLocation) {
        if (forcePickLocation || currentSettings.getSaveFile().equals("")) {
            String retVal = openFilePrompt(currentSettings.getSaveFile(), true);
            if (retVal.equals("")) {
                return;
            }
            currentSettings.setSaveFile(retVal);
            currentSettings.save();
            refresh();
        }
        save();
    }

    // EFFECTS: creates and returns the edit section of the menu bar
    private JMenu createEditMenu() {
        JMenu editMenu = new JMenu("Edit");
        JMenuItem newMenuItem = new JMenuItem("New Event");
        newMenuItem.addActionListener(e -> showEventCreator());
        editMenu.add(newMenuItem);
        return editMenu;
    }

    // EFFECTS: creates and returns the view section of the menu bar
    private JMenu createViewMenu() {
        JMenu viewMenu = new JMenu("View");
        JMenuItem showEventsMenuItem = new JMenuItem("All Events");
        JMenuItem showMonthMenuItem = new JMenuItem("Calendar");
        JMenuItem showHolidayMenuItem = new JMenuItem("Holidays");
        showEventsMenuItem.addActionListener(e -> showDisplay(Display.AllEvents));
        showMonthMenuItem.addActionListener(e -> showDisplay(Display.MonthCalendar));
        showHolidayMenuItem.addActionListener(e -> showDisplay(Display.Holidays));
        viewMenu.add(createDateMenu());
        viewMenu.add(showEventsMenuItem);
        viewMenu.add(showMonthMenuItem);
        viewMenu.add(showHolidayMenuItem);
        return viewMenu;
    }

    // EFFECTS: creates and returns the date section of the view menu
    private JMenu createDateMenu() {
        JMenu dayMenu = new JMenu("Date");
        JMenuItem todayMenuItem = new JMenuItem("Today");
        JMenuItem weekMenuItem = new JMenuItem("This Week");
        JMenuItem findMenuItem = new JMenuItem("Find");
        todayMenuItem.addActionListener(e -> showEventsForDates(getCurrentDate(), getCurrentDate()));
        weekMenuItem.addActionListener(e -> showEventsForDates(
                TimeUtility.atStartOfWeek(getCurrentDate()), TimeUtility.atEndOfWeek(getCurrentDate())));
        findMenuItem.addActionListener(e -> openFindWindow());
        dayMenu.add(todayMenuItem);
        dayMenu.add(weekMenuItem);
        dayMenu.add(findMenuItem);
        return dayMenu;
    }

    // EFFECTS: creates and returns the help section of the menu bar
    private JMenu createHelpMenu() {
        JMenu helpMenu = new JMenu("Help");
        JMenuItem showHelpMenuItem = new JMenuItem("Show Help");
        showHelpMenuItem.addActionListener(e -> showDisplay(Display.Help));
        helpMenu.add(showHelpMenuItem);
        return helpMenu;
    }

    // MODIFIES: this
    // EFFECTS: refreshes the display of the editor
    public void refresh() {
        showDisplay(currentDisplay);
    }

    // MODIFIES: this
    // EFFECTS: change the centre component in the editor
    private void changeCentreComponentTo(Component component, Display setDisplay) {
        getContentPane().remove(centreComponent);
        centreComponent = component;
        currentDisplay = setDisplay;
        getContentPane().add(component, BorderLayout.CENTER);
        validate();
        repaint();
    }

    // MODIFIES: this
    // EFFECTS: shows the corresponding visual display from display
    public void showDisplay(Display display) {
        switch (display) {
            case Settings:
                showSettings();
                break;
            case Help:
                showHelp();
                break;
            default:
                showViewDisplay(display);
                break;
        }
    }

    // MODIFIES: this
    // EFFECTS: shows the corresponding visual display of view displays
    private void showViewDisplay(Display display) {
        switch (display) {
            case MonthCalendar:
                showMonthCalendar();
                break;
            case EventsForDate:
                showEventsForDates(selectedDate, selectedEndDate);
                break;
            case Holidays:
                showHolidays();
                break;
            default:
                showAllEvents();
                break;
        }
    }

    // MODIFIES: this
    // EFFECTS: shows the calendar display at the centre of the frame
    public void showMonthCalendar() {
        changeCentreComponentTo(createCalendarDisplay(), Display.MonthCalendar);
        currentDisplay = Display.MonthCalendar;
    }

    // EFFECTS: creates a calendar display
    private JPanel createCalendarDisplay() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(new TitleLabel(selectedYearMonth().format(DateTimeFormatter.ofPattern("yyyy MMMM"))),
                BorderLayout.PAGE_START);
        mainPanel.add(createCalendar(), BorderLayout.CENTER);
        mainPanel.add(createToolPanel(e -> moveMonths(-1), e -> moveMonths(1)), BorderLayout.PAGE_END);
        return mainPanel;
    }

    // EFFECTS: creates and returns a bottom tool panel
    private JPanel createToolPanel(ActionListener leftAction, ActionListener rightAction) {
        JPanel toolPanel = new JPanel();
        toolPanel.setBackground(TOOL_PANEL_COLOR);
        toolPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, TOOL_PANEL_BORDER_COLOR));
        if (leftAction != null) {
            JButton leftButton = new JButton("<");
            leftButton.addActionListener(leftAction);
            toolPanel.add(leftButton);
        }
        if (rightAction != null) {
            JButton rightButton = new JButton(">");
            rightButton.addActionListener(rightAction);
            toolPanel.add(rightButton);
        }
        return toolPanel;
    }

    // EFFECTS: creates and returns the main calendar panel
    private JPanel createCalendar() {
        JPanel calendarPanel = new JPanel(new BorderLayout());
        JPanel dowLabels = createDowLabels();
        JPanel monthPanel = createMonthPanel(selectedYearMonth());
        calendarPanel.add(dowLabels, BorderLayout.PAGE_START);
        calendarPanel.add(monthPanel, BorderLayout.CENTER);
        return calendarPanel;
    }

    // EFFECTS: creates and returns a row of DayOfWeek labels
    private JPanel createDowLabels() {
        JPanel dowLabels = new JPanel();
        dowLabels.setBackground(BG_PANEL_COLOR);
        dowLabels.setLayout(new GridLayout(1,7));
        for (DayOfWeek dayOfWeek : dowValues(DayOfWeek.SUNDAY)) {
            JLabel dowLabel = new JLabel(dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    SwingConstants.CENTER);
            dowLabels.add(dowLabel);
        }
        return dowLabels;
    }

    // EFFECTS: creates and returns the boxes of dates for one month
    private JPanel createMonthPanel(YearMonth yearMonth) {
        JPanel monthPanel = new JPanel();
        monthPanel.setBackground(BG_PANEL_COLOR);
        monthPanel.setLayout(new GridLayout(6, 7, DATE_BOX_GAP, DATE_BOX_GAP));
        int dayOffSet = yearMonth.atDay(1).getDayOfWeek().getValue();
        int lastDay = yearMonth.lengthOfMonth();
        boolean[] hasEventsForDates = currentSchedule.hasEvents(yearMonth,
                currentSettings.isShowDate(), currentSettings.isShowRepeat());
        boolean[] hasHolidaysForDates = currentSettings.isShowHolidaysOnCalendar()
                ? currentHolidays.createHasHolidaysForYearMonth(yearMonth, currentSettings.isMergeHoliday())
                : new boolean[lastDay];
        int today = (getCurrentDate().getYear() == yearMonth.getYear()
                && getCurrentDate().getMonthValue() == yearMonth.getMonthValue())
                ? getCurrentDate().getDayOfMonth() : -1;
        addDateBoxes(monthPanel, yearMonth, dayOffSet, lastDay, today,
                hasEventsForDates, hasHolidaysForDates);
        return monthPanel;
    }

    // MODIFIES: monthPanel
    // EFFECTS: adds date boxes to monthPanel
    private void addDateBoxes(JPanel monthPanel, YearMonth yearMonth, int dayOffSet, int lastDay, int today,
                              boolean[] hasEventsForDates, boolean[] hasHolidaysForDates) {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                int currentIndex = (7 * i + j);
                int day = currentIndex - dayOffSet + 1;
                if (day >= 1 && day <= lastDay) {
                    monthPanel.add(createDateBox(yearMonth, day, hasEventsForDates[day - 1],
                            hasHolidaysForDates[day - 1], day == today,
                            j == 0, i == 5));
                } else {
                    JPanel emptyPanel = createEmptyDateBox(day == 0 && j < 6,
                            day > lastDay && day - 7 <= lastDay);
                    monthPanel.add(emptyPanel);
                }
            }
        }
    }

    // EFFECTS: creates and returns a box for a date
    private JPanel createDateBox(YearMonth yearMonth, int day, boolean hasEvent, boolean hasHoliday,
                                 boolean isToday, boolean drawLeft, boolean drawBtm) {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(ELEMENT_COLOR);
        JButton dateBox = new JButton();
        dateBox.setText("<html>&nbsp;" + day + (hasHoliday ? "*" : "") + "</html>");
        dateBox.setHorizontalAlignment(SwingConstants.LEFT);
        dateBox.setVerticalAlignment(SwingConstants.TOP);
        int border = DATE_BOX_DRAW_FULL_BORDER ? 1 : 0;
        dateBox.setBorder(BorderFactory.createMatteBorder(1, drawLeft ? border : 0, drawBtm ? border : 0, border,
                Color.BLACK));
        dateBox.addActionListener(e -> showEventsForDate(yearMonth.atDay(day)));
        dateBox.setOpaque(true);
        dateBox.setForeground((hasEvent || isToday) && currentSettings.isFlipTextColor() ? Color.WHITE : Color.BLACK);
        dateBox.setBackground(isToday ? TODAY_HL_COLOR
                : (hasEvent ? currentSettings.getUsingColor() : ELEMENT_COLOR));
        outerPanel.add(dateBox, BorderLayout.CENTER);
        return outerPanel;
    }

    // EFFECTS: creates and returns a filler box for date
    private JPanel createEmptyDateBox(boolean drawRight, boolean drawTop) {
        JPanel emptyPanel = new JPanel();
        if (drawRight) {
            emptyPanel.setBorder(BorderFactory.createMatteBorder(0,0,0,1, Color.BLACK));
        } else if (drawTop) {
            emptyPanel.setBorder(BorderFactory.createMatteBorder(1,0,0,0, Color.BLACK));
        }
        emptyPanel.setOpaque(false);
        return emptyPanel;
    }

    // MODIFIES: this
    // EFFECTS: moves between the selected month to be shown (on the month calendar)
    private void moveMonths(int count) {
        selectedDate = selectedYearMonth().plusMonths(count).atDay(1);
        showMonthCalendar();
    }

    // MODIFIES: this
    // EFFECTS: shows a list of events in the current schedule
    public void showAllEvents() {
        changeCentreComponentTo(createEventsDisplay(LocalDate.MIN, LocalDate.MAX),
                Display.AllEvents);
    }

    // MODIFIES: this
    // EFFECTS: shows a list of events in the current schedule for the date
    private void showEventsForDate(LocalDate date) {
        selectedDate = date;
        selectedEndDate = date;
        changeCentreComponentTo(createEventsDisplay(date, date), Display.EventsForDate);
    }

    // MODIFIES: this
    // EFFECTS: shows a list of events in the current schedule for the date
    public void showEventsForDates(LocalDate startDate, LocalDate endDate) {
        selectedDate = startDate;
        selectedEndDate = endDate;
        changeCentreComponentTo(createEventsDisplay(startDate, endDate), Display.EventsForDate);
    }

    // MODIFIES: this
    // EFFECTS: opens a search window for a date range
    private void openFindWindow() {
        FindFrame findFrame = new FindFrame(this);
    }

    // MODIFIES: this
    // EFFECTS: shows a confirmation box to choose whether to jump to date
    public void promptJumpToDate(LocalDate date) {
        if (!(selectedDate.equals(date) && selectedEndDate.equals(date))) {
            if (showConfirm("Jump to date of event?")) {
                showEventsForDate(date);
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: creates a panel with a list of events in the schedule
    private JPanel createEventsDisplay(LocalDate startDate, LocalDate endDate) {
        boolean isDate = startDate.equals(endDate);
        String titleText = "";
        if (!isDate && startDate.equals(LocalDate.MIN) && endDate.equals(LocalDate.MAX)) {
            titleText = "All Events";
        } else {
            titleText = "Events ";
            if (isDate) {
                titleText = titleText.concat("for " + startDate.format(DATE_FORMATTER)
                        + (startDate.equals(getCurrentDate()) ? " (Today)" : ""));
            } else {
                titleText = titleText.concat("between " + startDate.format(DATE_FORMATTER)
                        + "~" + endDate.format(DATE_FORMATTER));
            }
        }
        List<DateEvent> dateEvents = currentSchedule.getDateEventsBetweenDates(startDate, endDate);
        List<RepeatEvent> repeatEvents = currentSchedule.getRepeatEventsBetweenDates(startDate, endDate);
        return createEventsDisplay(titleText, dateEvents, repeatEvents, isDate);
    }

    // MODIFIES: this
    // EFFECTS: create a panel with a list of events in the schedule
    private JPanel createEventsDisplay(String titleText, List<DateEvent> dateEvents, List<RepeatEvent> repeatEvents,
                                       boolean isDate) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_PANEL_COLOR);
        mainPanel.add(new TitleLabel(titleText), BorderLayout.PAGE_START);
        List<MultiEvent> holidays = new ArrayList<>();
        if (isDate) {
            mainPanel.add(createToolPanel(e -> showEventsForDate(selectedDate.minusDays(1)),
                    e -> showEventsForDate(selectedDate.plusDays(1))), BorderLayout.PAGE_END);
            if (currentSettings.isShowHolidaysOnEvents()) {
                holidays = currentHolidays.getHolidayForDate(selectedDate, currentSettings.isMergeHoliday());
            }
        }
        if (dateEvents.size() + repeatEvents.size() < 1 && holidays.size() < 1) {
            mainPanel.add(new JLabel("No Events...", SwingConstants.CENTER), BorderLayout.CENTER);
        } else {
            mainPanel.add(createScrollableEventList(dateEvents, repeatEvents, holidays, isDate), BorderLayout.CENTER);
        }
        return mainPanel;
    }

    // EFFECTS: create a scrollable list of events
    private JScrollPane createScrollableEventList(List<DateEvent> dateEvents, List<RepeatEvent> repeatEvents,
                                                  List<MultiEvent> holidays, boolean isDate) {
        JPanel listPanel = new JPanel();
        JScrollPane scrollPane = new JScrollPane(listPanel);
        // For some reason, the scroll pane creates a horizontal scroll bar for an event name label
        // with the size of its text without truncating, creating empty space. Better to just disable.
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.PAGE_AXIS));
        addHolidayLabels(holidays, listPanel);
        addEventPanels(dateEvents, repeatEvents, listPanel, holidays.size() >= 1, isDate);
        return scrollPane;
    }

    // MODIFIES: listPanel
    // EFFECTS: adds EventPanels of events in the schedule to listPanel
    private void addEventPanels(List<DateEvent> dateEvents, List<RepeatEvent> repeatEvents,
                                JPanel listPanel, boolean addTop, boolean isDate) {
        if (repeatEvents.size() >= 1) {
            listPanel.add(new TitleLabel("Repeat Events", false, addTop, true));
        }
        for (int i = 0; i < repeatEvents.size() && i < 100; i++) {
            EventPanel eventPanel = new RepeatEventPanel(this, listPanel, repeatEvents.get(i), true);
            eventPanel.setMaximumSize(new Dimension(WIDTH, eventPanel.getMaximumSize().height));
            listPanel.add(eventPanel);
        }
        if (dateEvents.size() >= 1) {
            listPanel.add(new TitleLabel("Date Events", false, repeatEvents.size() >= 1, true));
        }
        for (int i = 0; i < dateEvents.size() && i < 100; i++) {
            EventPanel eventPanel = new DateEventPanel(this, listPanel, dateEvents.get(i),
                    getCurrentDate(), currentSettings.isDimPastEvents(), !isDate);
            eventPanel.setMaximumSize(new Dimension(WIDTH, eventPanel.getMaximumSize().height));
            listPanel.add(eventPanel);
        }
    }

    // MODIFIES: listPanel
    // EFFECTS: adds labels for holidays to the list panel
    private void addHolidayLabels(List<MultiEvent> holidays, JPanel listPanel) {
        if (holidays.size() >= 1) {
            listPanel.add(new TitleLabel("Holidays", false));
        }
        for (MultiEvent holiday : holidays) {
            JPanel panel = new JPanel(new BorderLayout());
            JLabel label = new JLabel(holiday.getMergedName(), SwingConstants.CENTER);
            panel.add(label, BorderLayout.CENTER);
            label.setPreferredSize(new Dimension(WIDTH, label.getPreferredSize().height));
            panel.setBorder(new EmptyBorder(2, 2, 2, 2));
            panel.setMaximumSize(panel.getPreferredSize());
            panel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.setOpaque(false);
            listPanel.add(panel);
        }
    }

    // MODIFIES: this
    // EFFECTS: prompts whether to delete the event in event panel or not, and does the action
    public void promptDelete(EventPanel eventPanel) {
        if (!currentSettings.isConfirmToDelete() || showConfirm("Delete event?")) {
            eventPanel.remove();
        }
    }

    // EFFECTS: shows a window to create a new event
    private void showEventCreator() {
        EventCreatorFrame newEventFrame = new EventCreatorFrame(this);
    }

    // EFFECTS: shows a window to edit an event
    public void showEventEditor(EventPanel eventPanel) {
        EventEditorFrame editEventFrame = new EventEditorFrame(this, eventPanel);
    }

    // MODIFIES: this
    // EFFECTS: shows the holiday display
    private void showHolidays() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_PANEL_COLOR);
        String holidayText = currentHolidays.getFormattedHolidaysText(selectedHolidaysYear,
                getCurrentDate(), currentSettings.isMergeHoliday(), currentSettings.isDimPastEvents());
        JLabel holidayListLabel = new JLabel(holidayText);
        holidayListLabel.setVerticalAlignment(SwingConstants.TOP);
        holidayListLabel.setHorizontalAlignment(SwingConstants.LEFT);
        JScrollPane scrollPane = new JScrollPane(holidayListLabel);
        mainPanel.add(new TitleLabel("Holidays for " + selectedHolidaysYear), BorderLayout.PAGE_START);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(createToolPanel(e -> moveHolidaysYear(-1), e -> moveHolidaysYear(1)),
                BorderLayout.PAGE_END);
        changeCentreComponentTo(mainPanel, Display.Holidays);
        scrollPane.setBorder(null);
        holidayListLabel.setBackground(BG_PANEL_COLOR);
        holidayListLabel.setOpaque(true);
    }

    // MODIFIES: this
    // EFFECTS: moves the holidays year
    private void moveHolidaysYear(int count) {
        int diff = getCurrentDate().getYear() - (selectedHolidaysYear + count);
        if (diff <= 5 && diff >= -5) {
            selectedHolidaysYear += count;
        }
        showHolidays();
    }

    // MODIFIES: this
    // EFFECTS: shows the setting display
    private void showSettings() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel innerPanel = new JPanel(new BorderLayout());
        JButton applyButton = new JButton("Apply");
        Settings newSettings = new Settings(currentSettings);
        applyButton.addActionListener(e -> {
            currentSettings = newSettings;
            if (!currentSettings.save()) {
                showError("You settings could not be saved. Make sure you have a "
                        + Settings.DEFAULT_DIRECTORY + " directory next to your application.");
            }
        });
        addSettingsPanels(innerPanel, newSettings);
        mainPanel.add(new TitleLabel("Settings"), BorderLayout.PAGE_START);
        mainPanel.add(innerPanel, BorderLayout.CENTER);
        mainPanel.add(applyButton, BorderLayout.PAGE_END);
        innerPanel.setOpaque(false);
        mainPanel.setBackground(BG_PANEL_COLOR);
        changeCentreComponentTo(mainPanel, Display.Settings);
    }

    // MODIFIES: innerPanel, settings
    // EFFECTS: adds all the settings to innerPanel
    private void addSettingsPanels(JPanel innerPanel, Settings settings) {
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.PAGE_AXIS));
        addLoadSettings(listPanel, settings);
        addEventSettings(listPanel, settings);
        addHolidaysSettings(listPanel, settings);
        addColorSettings(listPanel, settings);
        listPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        listPanel.setMaximumSize(listPanel.getMinimumSize());
        listPanel.setOpaque(false);
        innerPanel.add(listPanel, BorderLayout.PAGE_START);
    }

    // MODIFIES: listPanel, settings
    // EFFECTS: adds load options to panel
    private void addLoadSettings(JPanel listPanel, Settings settings) {
        JCheckBox colorChooser = new JCheckBox("Load on start");
        colorChooser.setSelected(settings.isLoadOnStart());
        colorChooser.addItemListener(e ->
                settings.setLoadOnStart(e.getStateChange() == ItemEvent.SELECTED));
        colorChooser.setAlignmentX(Component.LEFT_ALIGNMENT);
        addFilePicker(listPanel, settings);
        listPanel.add(colorChooser);
    }

    // MODIFIES: listPanel, settings
    // EFFECTS: create the panel for selecting file
    private void addFilePicker(JPanel listPanel, Settings settings) {
        JLabel label = new JLabel("  File Location");
        JTextField fileField = new JTextField(settings.getSaveFile());
        fileField.setEnabled(false);
        fileField.setAlignmentX(Component.LEFT_ALIGNMENT);
        listPanel.add(label);
        listPanel.add(fileField);
        fileField.setPreferredSize(new Dimension(WIDTH, fileField.getPreferredSize().height));
        fileField.setMaximumSize(new Dimension(WIDTH, fileField.getPreferredSize().height));
    }

    // MODIFIES: listPanel, settings
    // EFFECTS: creates the panel for color options
    private void addColorSettings(JPanel listPanel, Settings settings) {
        JCheckBox bwBox = new JCheckBox("B&W mode");
        JCheckBox flipTextBox = new JCheckBox("Flip text color on highlight");
        bwBox.setSelected(settings.isBwMode());
        flipTextBox.setSelected(settings.isFlipTextColor());
        bwBox.addItemListener(e -> settings.setBwMode(e.getStateChange() == ItemEvent.SELECTED));
        flipTextBox.addItemListener(e -> settings.setFlipTextColor(e.getStateChange() == ItemEvent.SELECTED));
        ColorInputPanel colorChooser = new ColorInputPanel("Highlight color:", settings.getHighlightColor());
        colorChooser.addActionListener(e -> settings.setHighlightColor(colorChooser.getColor()));
        listPanel.add(bwBox);
        listPanel.add(colorChooser);
        listPanel.add(flipTextBox);
        colorChooser.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    // MODIFIES: listPanel, settings
    // EFFECTS: creates the panel for event options
    private void addEventSettings(JPanel listPanel, Settings settings) {
        JCheckBox showDateBox = new JCheckBox("Show date events in Calendar");
        JCheckBox showRepeatBox = new JCheckBox("Show repeat events in Calendar");
        JCheckBox dimBox = new JCheckBox("Dim past events");
        JCheckBox confirmBox = new JCheckBox("Confirm on delete");
        showDateBox.setSelected(settings.isShowDate());
        showRepeatBox.setSelected(settings.isShowRepeat());
        dimBox.setSelected(settings.isDimPastEvents());
        confirmBox.setSelected(settings.isConfirmToDelete());
        showDateBox.addItemListener(e -> settings.setShowDate(e.getStateChange() == ItemEvent.SELECTED));
        showRepeatBox.addItemListener(e -> settings.setShowRepeat(e.getStateChange() == ItemEvent.SELECTED));
        dimBox.addItemListener(e -> settings.setDimPastEvents(e.getStateChange() == ItemEvent.SELECTED));
        confirmBox.addItemListener(e -> settings.setConfirmToDelete(e.getStateChange() == ItemEvent.SELECTED));
        listPanel.add(showDateBox);
        listPanel.add(showRepeatBox);
        listPanel.add(dimBox);
        listPanel.add(confirmBox);
    }

    // MODIFIES: listPanel, settings
    // EFFECTS: creates the panel for holiday options
    private void addHolidaysSettings(JPanel listPanel, Settings settings) {
        JCheckBox inCalendarBox = new JCheckBox("Show holidays in Calendar");
        JCheckBox inEventsBox = new JCheckBox("Show holidays in Events View");
        JCheckBox mergeHolidaysBox = new JCheckBox("Merge holidays on the same day");
        inCalendarBox.setSelected(settings.isShowHolidaysOnCalendar());
        inEventsBox.setSelected(settings.isShowHolidaysOnEvents());
        mergeHolidaysBox.setSelected(settings.isMergeHoliday());
        inCalendarBox.addItemListener(e -> settings.setShowHolidaysOnCalendar(
                e.getStateChange() == ItemEvent.SELECTED));
        inEventsBox.addItemListener(e -> settings.setShowHolidaysOnEvents(
                e.getStateChange() == ItemEvent.SELECTED));
        mergeHolidaysBox.addItemListener(e -> settings.setMergeHoliday(e.getStateChange() == ItemEvent.SELECTED));
        listPanel.add(inCalendarBox);
        listPanel.add(inEventsBox);
        listPanel.add(mergeHolidaysBox);
    }

    // MODIFIES: this
    // EFFECTS: show help display
    private void showHelp() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        JLabel textLabel = new JLabel(HELP_TEXT);
        textLabel.setVerticalAlignment(SwingConstants.TOP);
        textLabel.setHorizontalAlignment(SwingConstants.LEFT);
        mainPanel.add(new TitleLabel("Help"), BorderLayout.PAGE_START);
        mainPanel.add(textLabel, BorderLayout.CENTER);
        mainPanel.setBackground(BG_PANEL_COLOR);
        changeCentreComponentTo(mainPanel, Display.Help);
    }

    public static void main(String[] args) {
        try {
            new VisualEditor("Calendar");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
