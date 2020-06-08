package ui.components.input;

import utilities.ColorUtility;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ColorInputPanel extends InputPanel {
    private JLabel colorLabel;
    private JComboBox<String> colorBox;

    public ColorInputPanel(String labelText, Color initColor) {
        super(labelText);
        setSelectedItem(initColor);
    }

    @Override
    public void initializeComponents() {
        colorLabel = new JLabel("   ");
        colorLabel.setOpaque(true);
        colorLabel.setPreferredSize(new Dimension(20,20));
        colorBox = new JComboBox<>(ColorUtility.DEFAULT_COLOR_STRINGS);
        colorBox.addActionListener(e -> colorLabel.setBackground(getColor()));
    }

    @Override
    public void addComponents() {
        addAllComponents(colorLabel, colorBox);
    }

    // MODIFIES: this
    // EFFECTS: sets the selected item of the colorBox
    public void setSelectedItem(Color color) {
        colorLabel.setBackground(color);
        colorBox.setSelectedItem(ColorUtility.colorToString(color));
    }

    // MODIFIES: this
    // EFFECTS: adds an action listener to the colorBox
    public void addActionListener(ActionListener l) {
        colorBox.addActionListener(l);
    }

    // EFFECTS: returns the chosen color
    public Color getColor() {
        return ColorUtility.stringToColor((String) colorBox.getSelectedItem(), Color.GREEN);
    }
}
