package com.accordserver;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ServerWindow {
    private static ServerWindow INSTANCE;

    private JTextArea consoleTextArea;
    private JLabel statusLabel;

    private ServerWindow() {
    }

    public static ServerWindow getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ServerWindow();
        }
        return INSTANCE;
    }

    // Method to set up and show window
    public void showWindow() {
        // Basics
        JFrame frame = new JFrame();
        frame.setTitle("Accord Server");
        frame.setSize(800, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Text
        statusLabel = new JLabel();
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        frame.add(statusLabel, BorderLayout.NORTH);

        // Console output
        consoleTextArea = new JTextArea();
        consoleTextArea.setEditable(false);
        consoleTextArea.setLineWrap(true);
        DefaultCaret caret = (DefaultCaret) consoleTextArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane scrollPane = new JScrollPane(consoleTextArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Show window
        frame.setVisible(true);
    }

    // Method to set the status title
    public void setStatusTitle(String text) {
        statusLabel.setText(text);
    }

    // Method to print stuff to console
    public void printToConsole(String text) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String currentTime = dtf.format(now);
        consoleTextArea.append("[Server - " + currentTime + "] " + text + "\n");
    }

}
