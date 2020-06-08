package ui.components.display;

import model.ScheduleEvent;
import ui.VisualEditor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public abstract class EventPanel extends JPanel {
    protected VisualEditor editor;
    protected JPanel parent;

    public EventPanel(VisualEditor editor, JPanel parent) {
        this.editor = editor;
        this.parent = parent;
    }

    // MODIFIES: this
    // EFFECTS: initializes and adds components to the panel
    protected void initializeComponents(Color color, boolean showTimeScale) {
        setName(getScheduleEvent().getName());
        setLayout(new BorderLayout());
        JPanel textPanel = new JPanel(new BorderLayout());
        TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(color),
                (showTimeScale ? getScheduleEvent().getTimeString() : getScheduleEvent().getTimeOnlyString()));
        border.setTitleColor(color);
        textPanel.setBorder(border);
        textPanel.add(createNameLabel(color), BorderLayout.CENTER);
        add(textPanel, BorderLayout.CENTER);
        initializeButtons();
        textPanel.setOpaque(false);
        setOpaque(false);
        initializeSize();
    }

    // EFFECTS: creates and returns the name label
    private JButton createNameLabel(Color color) {
        JButton nameLabel = new JButton(getScheduleEvent().getName());
        nameLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        nameLabel.addActionListener(e -> showEventsForCorrespondingTime());
        nameLabel.setMinimumSize(nameLabel.getPreferredSize());
        nameLabel.setMaximumSize(nameLabel.getPreferredSize());
        nameLabel.setForeground(color);
        return nameLabel;
    }

    // MODIFIES: this
    // EFFECTS: initializes the buttons
    private void initializeButtons() {
        JPanel buttonPanel = new JPanel(new BorderLayout());
        JButton closeButton = new JButton("✗");
        JButton editButton = new JButton("✎");
        closeButton.addActionListener(e -> editor.promptDelete(this));
        editButton.addActionListener(e -> editor.showEventEditor(this));
        buttonPanel.add(closeButton, BorderLayout.LINE_START);
        buttonPanel.add(editButton, BorderLayout.LINE_END);
        add(buttonPanel, BorderLayout.LINE_START);
        closeButton.setPreferredSize(new Dimension(30, closeButton.getPreferredSize().height));
        editButton.setPreferredSize(new Dimension(30, editButton.getPreferredSize().height));
        buttonPanel.setOpaque(false);
    }

    // MODIFIES: this
    // EFFECTS: initializes the size and alignment of panel
    private void initializeSize() {
        setMinimumSize(getPreferredSize());
        setMaximumSize(getPreferredSize());
        setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    // EFFECTS: returns scheduleEvent
    public abstract ScheduleEvent getScheduleEvent();

    // MODIFIES: this
    // EFFECTS: called when pressed
    public abstract void showEventsForCorrespondingTime();

    // MODIFIES: this
    // EFFECTS: remove the panel with the event from editor
    public abstract void remove();
}
