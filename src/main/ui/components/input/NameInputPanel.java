package ui.components.input;

import javax.swing.*;

public class NameInputPanel extends InputPanel {
    private JTextField nameField;

    public NameInputPanel() {
        super("Name:");
    }

    // MODIFIES: this
    // EFFECTS: initialize components of the panel
    @Override
    public void initializeComponents() {
        nameField = new JTextField("", 20);
    }

    // MODIFIES: this
    // EFFECTS: adds nameField to this
    @Override
    public void addComponents() {
        addAllComponents(nameField);
    }

    // EFFECTS: returns the name inputted in the panel
    public String getName() {
        return nameField.getText();
    }

    // MODIFIES: this
    // EFFECTS: sets the name of the field to name
    public void setName(String name) {
        nameField.setText(name);
    }
}
