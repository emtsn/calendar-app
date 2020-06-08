package ui.components.input;

import javax.swing.*;

import java.time.LocalDate;

import static utilities.TimeUtility.intArray;

public class DayOfMonthInputPanel extends InputPanel {
    private JComboBox<Integer> dayOfMonthBox;

    public DayOfMonthInputPanel(LocalDate initialDate) {
        super("Day of Month:");
        initializeInitialDayOfMonth(initialDate);
    }

    // MODIFIES: this
    // EFFECTS: sets the dayOfMonthBox to DayOfMonth of date
    public void initializeInitialDayOfMonth(LocalDate date) {
        dayOfMonthBox.setSelectedItem(date.getDayOfMonth());
    }

    // MODIFIES: this
    // EFFECTS: initializes the components of the panel
    @Override
    public void initializeComponents() {
        dayOfMonthBox = new JComboBox<>(intArray(1, 31));
        dayOfMonthBox.setMaximumSize(dayOfMonthBox.getPreferredSize());
    }

    // MODIFIES: this
    // EFFECTS: adds dayOfMonthBox to this
    @Override
    public void addComponents() {
        addAllComponents(dayOfMonthBox);
    }

    // EFFECTS: returns the dayOfMonth inputted in the panel
    public int getDayOfMonth() {
        return (int) dayOfMonthBox.getSelectedItem();
    }

    // MODIFIES: this
    // EFFECTS: sets the selected item of dayOfMonthBox to dayOfMonth
    public void setDayOfMonth(int dayOfMonth) {
        dayOfMonthBox.setSelectedItem(dayOfMonth);
    }
}
