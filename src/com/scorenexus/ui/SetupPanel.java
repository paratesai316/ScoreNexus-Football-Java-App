package com.scorenexus.ui;

import com.scorenexus.main.ScoreNexusApp;
import com.scorenexus.model.Game;
import com.scorenexus.model.Player;
import com.scorenexus.model.Team;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class SetupPanel extends JPanel {

    private final ScoreNexusApp app;
    private JTextField teamANameField, teamBNameField;
    // MODIFICATION: Replaced JComboBox with JSpinner for custom duration
    private JSpinner durationSpinner; 
    private JComboBox<Integer> playersCombo, subsCombo;
    private JTextArea teamAPlayersArea, teamBPlayersArea;

    public SetupPanel(ScoreNexusApp app) {
        this.app = app;
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 40, 20, 40));
        setBackground(Color.DARK_GRAY.darker());

        // --- TOP: Logo (Resized) ---
        // MODIFICATION: Resized the logo to a more appropriate size
        ImageIcon originalLogo = new ImageIcon("resources/app_logo.png");
        Image scaledLogo = originalLogo.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(logoLabel, BorderLayout.NORTH);


        // --- CENTER: Form ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Labels Style
        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Color labelColor = Color.WHITE;

        // Team Names
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(createLabel("Team A Name:", labelFont, labelColor), gbc);
        gbc.gridx = 1;
        teamANameField = new JTextField("Team A", 15);
        formPanel.add(teamANameField, gbc);

        gbc.gridx = 2;
        formPanel.add(createLabel("Team B Name:", labelFont, labelColor), gbc);
        gbc.gridx = 3;
        teamBNameField = new JTextField("Team B", 15);
        formPanel.add(teamBNameField, gbc);

        // Game Settings
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(createLabel("Duration (mins):", labelFont, labelColor), gbc);
        gbc.gridx = 1;
        // MODIFICATION: Using JSpinner for custom time input
        durationSpinner = new JSpinner(new SpinnerNumberModel(90, 1, 200, 1));
        formPanel.add(durationSpinner, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        formPanel.add(createLabel("Players:", labelFont, labelColor), gbc);
        gbc.gridx = 3;
        playersCombo = new JComboBox<>(new Integer[]{5, 6, 7, 8, 9, 10, 11});
        playersCombo.setSelectedItem(11);
        formPanel.add(playersCombo, gbc);

        gbc.gridx = 2; gbc.gridy = 2;
        formPanel.add(createLabel("Substitutes:", labelFont, labelColor), gbc);
        gbc.gridx = 3;
        subsCombo = new JComboBox<>(new Integer[]{2, 3, 4, 5, 6, 7});
        subsCombo.setSelectedItem(5);
        formPanel.add(subsCombo, gbc);
        
        // Player Input Areas
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        formPanel.add(createLabel("Team A Players (Name, Number per line)", labelFont, labelColor), gbc);
        
        gbc.gridx = 2;
        formPanel.add(createLabel("Team B Players (Name, Number per line)", labelFont, labelColor), gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.ipady = 150; // make taller
        teamAPlayersArea = new JTextArea("PlayerA1, 1\nPlayerA2, 2\n...");
        // MODIFICATION: Set background, foreground, and caret color for dark theme
        teamAPlayersArea.setBackground(new Color(60, 63, 65));
        teamAPlayersArea.setForeground(Color.LIGHT_GRAY);
        teamAPlayersArea.setCaretColor(Color.WHITE);
        formPanel.add(new JScrollPane(teamAPlayersArea), gbc);
        
        gbc.gridx = 2;
        teamBPlayersArea = new JTextArea("PlayerB1, 1\nPlayerB2, 2\n...");
        // MODIFICATION: Set background, foreground, and caret color for dark theme
        teamBPlayersArea.setBackground(new Color(60, 63, 65));
        teamBPlayersArea.setForeground(Color.LIGHT_GRAY);
        teamBPlayersArea.setCaretColor(Color.WHITE);
        formPanel.add(new JScrollPane(teamBPlayersArea), gbc);


        add(formPanel, BorderLayout.CENTER);

        // --- BOTTOM: Button ---
        JButton startGameButton = new JButton("Create & Start Game");
        startGameButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        startGameButton.setBackground(new Color(0, 150, 0));
        startGameButton.setForeground(Color.WHITE);
        startGameButton.setFocusPainted(false);
        startGameButton.addActionListener(e -> createAndStartGame());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(startGameButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JLabel createLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    private void createAndStartGame() {
        if (teamANameField.getText().trim().isEmpty() || teamBNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Team names cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Team teamA = new Team(teamANameField.getText().trim());
        Team teamB = new Team(teamBNameField.getText().trim());
        int numPlayers = (int) playersCombo.getSelectedItem();
        int numSubs = (int) subsCombo.getSelectedItem();
        
        try {
            parsePlayers(teamAPlayersArea, teamA, numPlayers, numSubs);
            parsePlayers(teamBPlayersArea, teamB, numPlayers, numSubs);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Player Data Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // MODIFICATION: Get value from the JSpinner
        int duration = (int) durationSpinner.getValue();
        Game newGame = new Game(teamA, teamB, duration);
        app.startGame(newGame);
    }

    private void parsePlayers(JTextArea area, Team team, int numPlayers, int numSubs) throws Exception {
        String[] lines = area.getText().split("\\n");
        Set<Integer> usedNumbers = new HashSet<>();
        boolean autoNumberMode = false;
        
        if(lines.length < (numPlayers + numSubs) || lines[0].split(",").length < 2) {
             autoNumberMode = true;
        }

        for (int i = 0; i < numPlayers + numSubs; i++) {
            String name;
            int number;
            boolean isSub = i >= numPlayers;
            
            if (autoNumberMode) {
                 name = (i < lines.length && !lines[i].trim().isEmpty()) ? lines[i].trim() : team.getName() + " Player " + (i + 1);
                 number = i + 1;
            } else {
                 String[] parts = lines[i].split(",");
                 if(parts.length < 2) throw new Exception("Invalid format for player line: " + lines[i]);
                 name = parts[0].trim();
                 number = Integer.parseInt(parts[1].trim());
            }

            if (!usedNumbers.add(number)) {
                throw new Exception("Duplicate player number " + number + " in " + team.getName());
            }
            team.addPlayer(new Player(name, number, isSub));
        }
    }
}