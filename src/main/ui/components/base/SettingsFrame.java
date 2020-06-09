package ui.components.base;

import model.Settings;
import ui.VisualEditor;
import ui.components.input.ColorInputPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SettingsFrame extends JFrame {
    private static final int HEIGHT = 400;
    private static final int WIDTH = 400;
    private static final String SAVE_ERROR_MESSAGE = "<html>You settings could not be saved to your settings file.<br>"
            + "Make sure you have a " + Settings.DEFAULT_DIRECTORY + " directory next to your application.</html>";
    private static final String WEB_API_WARNING_MESSAGE = "<html>Enabling Web API may cause slow loading of "
            + "unvisited years.<br>Do you still want to enable?</html>";
    private static final String UNSAVED_SETTINGS_WARNING_MESSAGE = "<html>You have unsaved settings.<br>"
            + "Do you still want to exit?</html>";

    private VisualEditor editor;
    private JPanel mainPanel;
    private Settings prevSettings;
    private Settings newSettings;

    public SettingsFrame(VisualEditor editor, Settings currentSettings) {
        this.editor = editor;
        setTitle("Settings");
        prevSettings = new Settings(currentSettings);
        newSettings = new Settings(currentSettings);
        showSettings();
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setResizable(false);
        getContentPane().setBackground(VisualEditor.BG_PANEL_COLOR);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setVisible(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                quitBack(false);
            }
        });
    }

    // MODIFIES: this
    // EFFECTS: quits back to editor
    public boolean quitBack(boolean force) {
        if (force || prevSettings.equals(newSettings) || showConfirm(UNSAVED_SETTINGS_WARNING_MESSAGE)) {
            editor.removeSettingsFrame();
            dispose();
            return true;
        }
        return false;
    }

    // MODIFIES: this
    // EFFECTS: refreshes the settings panel
    private void refresh() {
        getContentPane().remove(mainPanel);
        mainPanel = createMainPanel();
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        repaint();
        revalidate();
    }


    // EFFECTS: shows a confirm dialog for the settings, and return user choice
    private boolean showConfirm(String text) {
        int value = JOptionPane.showConfirmDialog(this, text,
                "Confirm...", JOptionPane.YES_NO_OPTION);
        return value == 0;
    }

    // MODIFIES: this
    // EFFECTS: shows the setting display
    private void showSettings() {
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.LEADING));
        bottomBar.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        JButton okButton = new JButton("OK");
        JButton applyButton = new JButton("Apply");
        JButton defaultButton = new JButton("Default");
        JButton cancelButton = new JButton("Cancel");
        okButton.addActionListener(e -> applySettings(true));
        applyButton.addActionListener(e -> applySettings(false));
        defaultButton.addActionListener(e -> setDefault());
        cancelButton.addActionListener(e -> quitBack(true));
        bottomBar.add(okButton);
        bottomBar.add(applyButton);
        bottomBar.add(defaultButton);
        bottomBar.add(cancelButton);
        getContentPane().add(createMainPanel(), BorderLayout.CENTER);
        getContentPane().add(bottomBar, BorderLayout.PAGE_END);
    }

    // EFFECTS: returns the main panel of the settings with all of the options
    private JPanel createMainPanel() {
        mainPanel = new JPanel(new BorderLayout());
        addSettingsPanels(mainPanel, newSettings);
        return mainPanel;
    }

    // MODIFIES: this
    // EFFECTS: applies the settings on editor
    private void applySettings(boolean quitOnApply) {
        if (!prevSettings.isLoadHolidaysFromWeb() && newSettings.isLoadHolidaysFromWeb()) {
            if (!showConfirm(WEB_API_WARNING_MESSAGE)) {
                newSettings.setLoadHolidaysFromWeb(false);
                refresh();
                return;
            }
        }
        prevSettings.copy(newSettings);
        if (editor.applySettings(newSettings)) {
            editor.refresh();
            if (quitOnApply) {
                quitBack(true);
            }
        } else {
            JOptionPane.showMessageDialog(this, SAVE_ERROR_MESSAGE,
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // MODIFIES: this
    // EFFECTS: sets the options to the default options
    public void setDefault() {
        newSettings.setToDefault();
        refresh();
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
        JCheckBox mergeBox = new JCheckBox("Merge holidays on the same day");
        JCheckBox webBox = new JCheckBox("Use Web API for holidays");
        inCalendarBox.setSelected(settings.isShowHolidaysOnCalendar());
        inEventsBox.setSelected(settings.isShowHolidaysOnEvents());
        mergeBox.setSelected(settings.isMergeHoliday());
        webBox.setSelected(settings.isLoadHolidaysFromWeb());
        inCalendarBox.addItemListener(e -> settings.setShowHolidaysOnCalendar(
                e.getStateChange() == ItemEvent.SELECTED));
        inEventsBox.addItemListener(e -> settings.setShowHolidaysOnEvents(
                e.getStateChange() == ItemEvent.SELECTED));
        mergeBox.addItemListener(e -> settings.setMergeHoliday(e.getStateChange() == ItemEvent.SELECTED));
        webBox.addItemListener(e -> settings.setLoadHolidaysFromWeb(e.getStateChange() == ItemEvent.SELECTED));
        listPanel.add(inCalendarBox);
        listPanel.add(inEventsBox);
        listPanel.add(mergeBox);
        listPanel.add(webBox);
    }
}
