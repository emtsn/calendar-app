package ui.components.input;

import javax.swing.*;
import java.time.LocalTime;

import static utilities.TimeUtility.intArray;

public class TimeInputPanel extends InputPanel {
    private JComboBox<Integer> hourBox;
    private JComboBox<Integer> minBox;

    public TimeInputPanel(String labelText) {
        super(labelText);
    }

    // MODIFIES: this
    // EFFECTS: initializes the components of the panel
    @Override
    public void initializeComponents() {
        hourBox = new JComboBox<>(intArray(0, 23));
        minBox = new JComboBox<>(intArray(0, 59));
        hourBox.setMaximumSize(hourBox.getPreferredSize());
        hourBox.setMaximumSize(minBox.getPreferredSize());
    }

    // MODIFIES: this
    // EFFECTS: adds the time ComboBoxes to this
    @Override
    public void addComponents() {
        addAllComponents(hourBox, minBox);
    }

    // EFFECTS: returns the time inputted in the panel
    public LocalTime getTime() {
        return LocalTime.of((int) hourBox.getSelectedItem(), (int) minBox.getSelectedItem());
    }

    // MODIFIES: this
    // EFFECTS: sets the time boxes to time
    public void setTime(LocalTime time) {
        hourBox.setSelectedItem(time.getHour());
        minBox.setSelectedItem(time.getMinute());
    }
}
