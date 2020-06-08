package ui.components.input;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;

import static utilities.TimeUtility.intArray;

public class DateInputPanel extends InputPanel {
    private JComboBox<Integer> yearBox;
    private JComboBox<Month> monthBox;
    private JComboBox<Integer> dayBox;

    public DateInputPanel(String labelText, LocalDate initialDate) {
        super(labelText);
        initializeInitialDate(initialDate);
        initializeDateChangeListener();
    }

    // MODIFIES: this
    // EFFECTS: initializes the initial date with date
    public void initializeInitialDate(LocalDate date) {
        yearBox.setSelectedItem(date.getYear());
        monthBox.setSelectedItem(date.getMonth());
        dayBox.setSelectedItem(date.getDayOfMonth());
    }

    // MODIFIES: this
    // EFFECTS: initializes the components of the panel
    @Override
    public void initializeComponents() {
        yearBox = new JComboBox<>(intArray(2000, 2050));
        monthBox = new JComboBox<>(Month.values());
        dayBox = new JComboBox<>(intArray(1, 31));
        yearBox.setMaximumSize(yearBox.getPreferredSize());
        monthBox.setMaximumSize(monthBox.getPreferredSize());
        dayBox.setMaximumSize(dayBox.getPreferredSize());
    }

    // MODIFIES: this
    // EFFECTS: adds the date ComboBoxes to this
    @Override
    public void addComponents() {
        addAllComponents(yearBox, monthBox, dayBox);
    }

    // MODIFIES: this
    // EFFECTS: creates an ItemListener for yearBox and monthBox, to change the days in dayBox
    private void initializeDateChangeListener() {
        ItemListener dateChangeListener = e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                YearMonth yearMonth = YearMonth.of((int) yearBox.getSelectedItem(), (Month) monthBox.getSelectedItem());
                setDayComboBox(dayBox, yearMonth.lengthOfMonth());
            }
        };
        yearBox.addItemListener(dateChangeListener);
        monthBox.addItemListener(dateChangeListener);
    }

    // EFFECTS: returns the date inputted in the panel
    public LocalDate getDate() throws DateTimeException {
        return LocalDate.of((int) yearBox.getSelectedItem(),
                (Month) monthBox.getSelectedItem(), (int) dayBox.getSelectedItem());
    }

    // MODIFIES: this
    // EFFECTS: sets the selected date of the boxes to date
    public void setDate(LocalDate date) {
        yearBox.setSelectedItem(date.getYear());
        monthBox.setSelectedItem(date.getMonth());
        dayBox.setSelectedItem(date.getDayOfMonth());
    }

    // MODIFIES: dayComboBox
    // EFFECTS: change comboBox showing days to switch to a different max day
    private static void setDayComboBox(JComboBox<Integer> dayComboBox, int maxDay) {
        int prevSelected = (int) dayComboBox.getSelectedItem();
        if (prevSelected > maxDay) {
            prevSelected = maxDay;
        }
        DefaultComboBoxModel<Integer> model = (DefaultComboBoxModel<Integer>) dayComboBox.getModel();
        model.removeAllElements();
        for (int i = 1; i <= maxDay; i++) {
            model.addElement(i);
        }
        model.setSelectedItem(prevSelected);
        dayComboBox.setModel(model);
    }
}
