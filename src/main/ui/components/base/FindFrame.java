package ui.components.base;

import ui.VisualEditor;
import ui.components.input.DateInputPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.time.LocalDate;

public class FindFrame extends JFrame {
    private static final int HEIGHT = 200;
    private static final int WIDTH = 400;

    private VisualEditor editor;
    private DateInputPanel startDatePanel;
    private DateInputPanel endDatePanel;

    public FindFrame(VisualEditor editor) {
        this.editor = editor;
        setTitle("Find Events Between...");
        initializeComponents();
        initializeDisplay();
        getContentPane().setBackground(VisualEditor.BG_PANEL_COLOR);
        setVisible(true);
    }

    // MODIFIES: this
    // EFFECTS: initializes the components of the panel
    private void initializeComponents() {
        JPanel mainPanel = new JPanel();
        startDatePanel = new DateInputPanel("From: ", editor.pickingDate());
        endDatePanel = new DateInputPanel("To: ", editor.pickingDate());
        JButton matchDateButton = new JButton("Match Date");
        matchDateButton.addActionListener(e -> endDatePanel.setDate(startDatePanel.getDate()));
        mainPanel.add(startDatePanel);
        mainPanel.add(endDatePanel);
        mainPanel.add(matchDateButton);
        mainPanel.setOpaque(false);
        JButton bottomButton = new JButton("Find");
        bottomButton.addActionListener(e -> findEventInEditor());
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        getContentPane().add(bottomButton, BorderLayout.PAGE_END);
    }

    // MODIFIES: this
    // EFFECTS: initializes the display of the panel
    private void initializeDisplay() {
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setResizable(false);
    }

    // MODIFIES: this
    // EFFECTS: closes this frame and finds the events in editor
    private void findEventInEditor() {
        LocalDate startDate = startDatePanel.getDate();
        LocalDate endDate = endDatePanel.getDate();
        if (endDate.isAfter(startDate)) {
            editor.showEventsForDates(startDate, endDate);
        } else {
            editor.showEventsForDates(endDate, startDate);
        }
        editor.refresh();
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }
}
