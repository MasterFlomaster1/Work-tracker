package src;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class WorkTracker {
    private static final String MESSAGE_FILENAME = "messages.txt";
    private static final String SUMMARY_FILENAME = "summaries.txt";

    private static JTextArea messagesArea;
    private static JButton addSummaryButton;

    public static void main(String[] args) {
        // Create a frame
        JFrame frame = new JFrame("Work Tracker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        // Add a label with padding
        JLabel label = new JLabel("Work Tracker");
        label.setFont(new Font("Arial", Font.BOLD, 20));
        label.setBorder(new EmptyBorder(10, 10, 10, 10));
        frame.add(label, BorderLayout.NORTH);

        // Add a panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addMessageButton = new JButton("Add Message");
        addMessageButton.addActionListener(e -> addMessage());
        buttonPanel.add(addMessageButton);

        addSummaryButton = new JButton("Add Summary");
        addSummaryButton.setEnabled(false);
        addSummaryButton.addActionListener(e -> addSummary());
        buttonPanel.add(addSummaryButton);

        // Add a button to clear messages and summaries
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> clearMessagesAndSummaries());
        buttonPanel.add(clearButton);

        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Add a text area for displaying messages and summaries
        messagesArea = new JTextArea();
        messagesArea.setEditable(false);
        messagesArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        messagesArea.setBorder(new LineBorder(Color.GRAY));
        JScrollPane messagesScrollPane = new JScrollPane(messagesArea);
        frame.add(messagesScrollPane, BorderLayout.CENTER);

        // Load and display existing messages and summaries
        loadMessages();
        loadSummaries();

        // Enable/disable "Add Summary" button based on the time
        updateAddSummaryButtonState();

        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

    private static void addMessage() {
        String message = JOptionPane.showInputDialog(null, "Enter what you are thinking now", "Add Message", JOptionPane.PLAIN_MESSAGE);
        if (message != null && !message.isEmpty()) {
            saveMessage(MESSAGE_FILENAME, message);
            messagesArea.append(getFormattedDateTime() + " - Message: " + message + "\n");
            messagesArea.setCaretPosition(messagesArea.getDocument().getLength());
        }
    }

    private static void addSummary() {
        String summary = JOptionPane.showInputDialog(null, "Enter a summary of the day", "Add Summary", JOptionPane.PLAIN_MESSAGE);
        if (summary != null && !summary.isEmpty()) {
            saveSummary(SUMMARY_FILENAME, summary);
            messagesArea.append(getFormattedDateTime() + " - Summary:\n" + summary + "\n\n");
            messagesArea.setCaretPosition(messagesArea.getDocument().getLength());
        }
    }

    private static void saveMessage(String filename, String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            File file = new File(filename);
            boolean fileExists = file.exists();
            if (!fileExists) {
                writer.write("Message Log\n");
            }
            writer.write(message + "\n");
        } catch (IOException ex) {
            System.err.println("Error saving Message: " + ex.getMessage());
            JOptionPane.showMessageDialog(null, "Error saving message: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void saveSummary(String filename, String summary) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            File file = new File(filename);
            boolean fileExists = file.exists();
            if (!fileExists) {
                writer.write("Summary Log\n");
            }
            writer.write(summary + "\n\n");
        } catch (IOException ex) {
            System.err.println("Error saving Summary: " + ex.getMessage());
            JOptionPane.showMessageDialog(null, "Error saving summary: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void loadMessages() {
        List<String> messages = readFile(MESSAGE_FILENAME);
        for (String message : messages) {
            messagesArea.append("Message: " + message + "\n");
        }
    }

    private static void loadSummaries() {
        List<String> summaries = readFile(SUMMARY_FILENAME);
        for (String summary : summaries) {
            messagesArea.append("Summary:\n" + summary + "\n\n");
        }
    }

    private static List<String> readFile(String filename) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException ex) {
            System.err.println("Error reading file: " + ex.getMessage());
        }
        return lines;
    }

    private static void clearMessagesAndSummaries() {
        int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to clear all messages and summaries?", "Clear Data", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (option == JOptionPane.YES_OPTION) {
            try {
                new File(MESSAGE_FILENAME).delete();
                new File(SUMMARY_FILENAME).delete();
                messagesArea.setText("");
            } catch (Exception ex) {
                System.err.println("Error clearing data: " + ex.getMessage());
                JOptionPane.showMessageDialog(null, "Error clearing data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static String getFormattedDateTime() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    private static void updateAddSummaryButtonState() {
        addSummaryButton.setEnabled(LocalTime.now().isAfter(LocalTime.of(21, 0)));
        Timer timer = new Timer(60000, e -> updateAddSummaryButtonState()); // Update every minute
        timer.setRepeats(true);
        timer.start();
    }
}