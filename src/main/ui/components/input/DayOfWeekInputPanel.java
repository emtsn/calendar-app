package ui.components.input;

import javax.swing.*;
import java.time.DayOfWeek;
import java.time.LocalDate;

public class DayOfWeekInputPanel extends InputPanel {
    private JComboBox<DayOfWeek> dayOfWeekBox;

    public DayOfWeekInputPanel(LocalDate initialDate) {
        super("Day of Week:");
        initializeInitialDayOfWeek(initialDate);
    }

    // MODIFIES: this
    // EFFECTS: sets the initial DayOfWeek as the DayOfWeek of date
    public void initializeInitialDayOfWeek(LocalDate date) {
        dayOfWeekBox.setSelectedItem(date.getDayOfWeek());
    }

    // MODIFIES: this
    // EFFECTS: initializes the components of the panel
    @Override
    public void initializeComponents() {
        dayOfWeekBox = new JComboBox<>(DayOfWeek.values());
        dayOfWeekBox.setMaximumSize(dayOfWeekBox.getPreferredSize());
    }

    // MODIFIES: this
    // EFFECTS: adds dayOfWeekBox to this
    @Override
    public void addComponents() {
        addAllComponents(dayOfWeekBox);
    }

    // EFFECTS: returns the dayOfWeek inputted in the panel
    public DayOfWeek getDayOfWeek() {
        return (DayOfWeek) dayOfWeekBox.getSelectedItem();
    }

    // MODIFIES: this
    // EFFECTS: sets the selected item of dayOfWeekBox to dayOfWeek
    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        dayOfWeekBox.setSelectedItem(dayOfWeek);
    }
}
