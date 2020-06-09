package ui;

import model.*;
import ui.components.base.FindFrame;
import ui.components.base.SettingsFrame;
import ui.components.display.DateEventPanel;
import ui.components.display.EventPanel;
import ui.components.display.RepeatEventPanel;
import ui.components.display.TitleLabel;
import ui.components.base.EventCreatorFrame;
import ui.components.base.EventEditorFrame;
import utilities.TimeUtility;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.time.*;
import java.time.format.TextStyle;
import java.util.*;
import java.util.List;

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
            = "<html>&nbsp;<b>File</b><br>"
            + "- Use <u>New</u> to create a new schedule<br>"
            + "- Use <u>Load</u> to load from the current schedule<br>"
            + "- Use <u>Open</u> to open an existing schedule from a directory<br>"
            + "- Use <u>Save</u> to save the current schedule<br>"
            + "- Use <u>Save As</u> to save the current schedule at a directory<br>"
            + "- Use <u>Settings</u> to open the settings window<br>"
            + "&nbsp;<b>Edit</b><br>"
            + "- Use <u>New Event</u> to create a new event<br>"
            + "&nbsp;<b>View</b><br>"
            + "- Use <u>Events/Today</u> to view the events for today<br>"
            + "- Use <u>Events/This Week</u> to view the events for this week<br>"
            + "- Use <u>Events/This Month</u> to view the events for this month<br>"
            + "- Use <u>Events/Find</u> to find events between a range<br>"
            + "- Use <u>All Events</u> to view all events<br>"
            + "&nbsp;&nbsp;- Click on an date event to jump to the date<br>"
            + "- Use <u>Calendar</u> to view the calendar<br>"
            + "&nbsp;&nbsp;- Click on a date to view the date's events<br>"
            + "- Use <u>Holidays</u> to view the holidays for this year";
    private static final String UNSAVED_HEADER = "<html>You have unsaved changes to your current schedule.<br>";
    private static final String UNSAVED_WARNING_MESSAGE = UNSAVED_HEADER + "Do you still want to exit?</html>";
    private static final String NEW_FILE_WARNING_MESSAGE = "<html>Create new schedule?</html>";
    private static final String UNSAVED_OPEN_WARNING_MESSAGE = UNSAVED_HEADER + "Do you still want to open?</html>";
    private static final String LOAD_WARNING_MESSAGE = "<html>Load from currently set save file?</html>";
    private static final String UNSAVED_LOAD_WARNING_MESSAGE = UNSAVED_HEADER + "Do you still want to load?</html>";

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
    private SettingsFrame settingsFrame;

    private boolean changedSinceLastSave;

    public VisualEditor(String name) {
        super(name);
        File file = new File(Settings.DEFAULT_DIRECTORY);
        if (file.mkdirs()) {
            showMessage("<html>Created the default directory at<br>" + System.getProperty("user.dir")
                    + "/" + Settings.DEFAULT_DIRECTORY + "</html>");
        }
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
        changedSinceLastSave = false;
        initializeHolidaysAndSettings();
    }

    // MODIFIES: this
    // EFFECTS: loads from the settings and holidays file, creates them if they don't exists, and initialize holidays
    private void initializeHolidaysAndSettings() {
        if (!currentSettings.load()) {
            showMessage("<html>Created the default settings file at<br>" + System.getProperty("user.dir")
                    + "/" + Settings.DEFAULT_SETTINGS_FILE + "</html>");
            currentSettings.save();
        }
        if (!currentHolidays.load()) {
            showMessage("<html>Created the default holidays file at<br>" + System.getProperty("user.dir")
                    + "/" + Settings.DEFAULT_HOLIDAYS_FILE + "</html>");
            currentHolidays.save();
        }
        currentHolidays.setSettings(currentSettings.isMergeHoliday(), currentSettings.isLoadHolidaysFromWeb());
        if (currentSettings.isLoadHolidaysFromWeb()) {
            for (int i = -2; i <= 2; i++) {
                currentHolidays.getHolidays(getCurrentDate().getYear() + i);
            }
        }
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
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (!changedSinceLastSave || showConfirm(UNSAVED_WARNING_MESSAGE)) {
                    dispose();
                    System.exit(0);
                }
            }
        });
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
        changedSinceLastSave = true;
        currentSchedule.addEvent(dateEvent);
    }

    // MODIFIES: this
    // EFFECTS:: adds repeatEvent to currentSchedule
    public void addEvent(RepeatEvent repeatEvent) {
        changedSinceLastSave = true;
        currentSchedule.addEvent(repeatEvent);
    }

    // MODIFIES: this
    // EFFECTS: removes dateEvent from currentSchedule, show error if failed
    public void removeEvent(DateEvent dateEvent) {
        changedSinceLastSave = true;
        if (!currentSchedule.removeEvent(dateEvent)) {
            showError("Failed to remove event.");
        }
    }

    // MODIFIES: this
    // EFFECTS: removes repeatEvent from currentSchedule, show error if failed
    public void removeEvent(RepeatEvent repeatEvent) {
        changedSinceLastSave = true;
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
            changedSinceLastSave = false;
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
            changedSinceLastSave = false;
            refresh();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load schedule. Please check the save file location in settings.");
            success = false;
        }
        return success;
    }

    // EFFECTS: shows an error dialogue
    private void showMessage(String text) {
        JOptionPane.showMessageDialog(this, text, "Info", JOptionPane.INFORMATION_MESSAGE);
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
                ? initialPath : Settings.DEFAULT_DIRECTORY);
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

    // EFFECTS: creates and returns a JMenuItem with actionListener
    private static JMenuItem createJMenuItem(String text, ActionListener actionListener) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.addActionListener(actionListener);
        return menuItem;
    }

    // EFFECTS: creates and returns the file section of the menu bar
    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("File");
        JMenuItem newFileItem = createJMenuItem("New", e -> promptNewSchedule());
        JMenuItem loadMenuItem = createJMenuItem("Load", e -> promptLoadSchedule());
        JMenuItem openMenuItem = createJMenuItem("Open...", e -> promptOpenSchedule());
        JMenuItem saveMenuItem = createJMenuItem("Save", e -> promptSaveSchedule(false));
        JMenuItem saveAsMenuItem = createJMenuItem("Save As...", e -> promptSaveSchedule(true));
        JMenuItem settingsMenuItem = createJMenuItem("Settings", e -> showDisplay(Display.Settings));
        fileMenu.add(newFileItem);
        fileMenu.add(loadMenuItem);
        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(saveAsMenuItem);
        fileMenu.add(settingsMenuItem);
        return fileMenu;
    }

    // MODIFIES: this
    // EFFECTS: shows a prompt to create new schedule, clears if picked yes
    private void promptNewSchedule() {
        if (showConfirm(NEW_FILE_WARNING_MESSAGE)) {
            currentSchedule.clearScheduleEvents();
            changedSinceLastSave = false;
            currentSettings.setSaveFile("");
            currentSettings.save();
            refresh();
        }
    }

    // EFFECTS: shows a file prompt to create new schedule, clears if picked yes
    private void promptOpenSchedule() {
        if (changedSinceLastSave && !showConfirm(UNSAVED_OPEN_WARNING_MESSAGE)) {
            return;
        }
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

    // EFFECTS: shows a prompt for whether to load or not
    private void promptLoadSchedule() {
        if (changedSinceLastSave) {
            if (!showConfirm(UNSAVED_LOAD_WARNING_MESSAGE)) {
                return;
            }
        } else {
            if (!showConfirm(LOAD_WARNING_MESSAGE)) {
                return;
            }
        }
        load();
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
        JMenuItem showEventsMenuItem = createJMenuItem("All Events", e -> showDisplay(Display.AllEvents));
        JMenuItem showMonthMenuItem = createJMenuItem("Calendar", e -> showDisplay(Display.MonthCalendar));
        JMenuItem showHolidayMenuItem = createJMenuItem("Holidays", e -> showDisplay(Display.Holidays));
        viewMenu.add(createViewEventsMenu());
        viewMenu.add(showEventsMenuItem);
        viewMenu.add(showMonthMenuItem);
        viewMenu.add(showHolidayMenuItem);
        return viewMenu;
    }

    // EFFECTS: creates and returns the events section of the view menu
    private JMenu createViewEventsMenu() {
        JMenu eventsMenu = new JMenu("Events");
        JMenuItem todayMenuItem = createJMenuItem("Today",
                e -> showEventsForDates(getCurrentDate(), getCurrentDate()));
        JMenuItem weekMenuItem = createJMenuItem("This Week", e -> showEventsForDates(
                TimeUtility.atStartOfWeek(getCurrentDate()), TimeUtility.atEndOfWeek(getCurrentDate())));
        JMenuItem monthMenuItem = createJMenuItem("This Month", e -> showEventsForDates(
                getCurrentDate().withDayOfMonth(1), getCurrentDate().withDayOfMonth(getCurrentDate().lengthOfMonth())));
        JMenuItem findMenuItem = createJMenuItem("Find", e -> openFindWindow());
        eventsMenu.add(todayMenuItem);
        eventsMenu.add(weekMenuItem);
        eventsMenu.add(monthMenuItem);
        eventsMenu.add(findMenuItem);
        return eventsMenu;
    }

    // EFFECTS: creates and returns the help section of the menu bar
    private JMenu createHelpMenu() {
        JMenu helpMenu = new JMenu("Help");
        JMenuItem showHelpMenuItem = createJMenuItem("Show Help", e -> showDisplay(Display.Help));
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
        mainPanel.add(new TitleLabel(selectedYearMonth().format(YEAR_MONTH_FORMATTER)),
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
        for (DayOfWeek dayOfWeek : TimeUtility.dowValues(DayOfWeek.SUNDAY)) {
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
                ? currentHolidays.createHasHolidaysForYearMonth(yearMonth)
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

    // EFFECTS: returns a formatted string for a title of events between two dates
    private String formatEventTextFor(LocalDate startDate, LocalDate endDate) {
        String titleText = "Events ";
        if (startDate.equals(endDate)) {
            titleText = titleText.concat("for " + startDate.format(DATE_FORMATTER)
                    + (startDate.equals(getCurrentDate()) ? " (Today)" : ""));
        } else if (TimeUtility.isWeek(startDate, endDate)) {
            titleText = titleText.concat("for Week of " + startDate.format(DATE_FORMATTER)
                    +  "~" + endDate.getDayOfMonth());
        } else if (TimeUtility.isMonths(startDate, endDate)) {
            titleText = titleText.concat((TimeUtility.isSameMonth(startDate, endDate)
                    ? "for " : ("between " + startDate.format(MONTH_FORMATTER) + "~"))
                    + endDate.format(YEAR_MONTH_FORMATTER));
        } else {
            titleText = titleText.concat("between " + startDate.format(DATE_FORMATTER)
                    + "~" + endDate.format(DATE_FORMATTER));
        }
        return titleText;
    }

    // MODIFIES: this
    // EFFECTS: creates a panel with a list of events in the schedule
    private JPanel createEventsDisplay(LocalDate startDate, LocalDate endDate) {
        boolean isDate = startDate.equals(endDate);
        String titleText = "";
        if (!isDate && startDate.equals(LocalDate.MIN) && endDate.equals(LocalDate.MAX)) {
            titleText = "All Events";
        } else {
            titleText = formatEventTextFor(startDate, endDate);
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
                holidays = currentHolidays.getHolidayForDate(selectedDate);
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
        String holidayText = currentHolidays.getFormattedHolidaysText(selectedHolidaysYear, getCurrentDate(),
                currentSettings.isDimPastEvents());
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
    // EFFECTS: shows the settings panel
    private void showSettings() {
        if (settingsFrame == null || settingsFrame.quitBack(false)) {
            settingsFrame = new SettingsFrame(this, currentSettings);
        }
    }

    // MODIFIES: this
    // EFFECTS: removes reference to settings frame
    public void removeSettingsFrame() {
        settingsFrame = null;
    }

    // MODIFIES: this
    // EFFECTS: applies settings, return true if it could be saved to disk
    public boolean applySettings(Settings newSettings) {
        currentSettings.copy(newSettings);
        currentHolidays.setSettings(currentSettings.isMergeHoliday(), currentSettings.isLoadHolidaysFromWeb());
        return currentSettings.save();
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
