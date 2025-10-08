package com.scorenexus.ui;

import com.scorenexus.main.ScoreNexusApp;
import com.scorenexus.model.Game;
import com.scorenexus.model.Player;
import com.scorenexus.util.CSVExporter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SummaryPanel extends JPanel {
    private final ScoreNexusApp app;
    private Game game;
    private JTable statsTable;
    private JLabel finalScoreLabel;
    private DefaultTableModel tableModel;

    public SummaryPanel(ScoreNexusApp app) {
        this.app = app;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(Color.DARK_GRAY);

        finalScoreLabel = new JLabel("Game Over!", SwingConstants.CENTER);
        finalScoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        finalScoreLabel.setForeground(Color.WHITE);
        add(finalScoreLabel, BorderLayout.NORTH);

        // Player Stats Table
        String[] columnNames = {"Team", "Name", "#", "Goals", "Assists", "YC", "RC"};
        tableModel = new DefaultTableModel(columnNames, 0);
        statsTable = new JTable(tableModel);
        statsTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statsTable.setRowHeight(25);
        statsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        JScrollPane scrollPane = new JScrollPane(statsTable);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel for actions
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        bottomPanel.setOpaque(false);
        JButton exportButton = new JButton("Export All to CSV");
        JButton newGameButton = new JButton("New Game");

        setupButton(exportButton);
        setupButton(newGameButton);
        
        exportButton.addActionListener(e -> exportData());
        newGameButton.addActionListener(e -> app.createNewGame());

        bottomPanel.add(exportButton);
        bottomPanel.add(newGameButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void setupButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 80, 150));
        button.setFocusPainted(false);
    }

    public void loadSummary(Game game) {
        this.game = game;
        finalScoreLabel.setText(String.format("Final Score: %s %d - %d %s",
            game.getTeamA().getName(), game.getTeamA().getScore(),
            game.getTeamB().getScore(), game.getTeamB().getName()));
        
        // Clear previous data
        tableModel.setRowCount(0);
        
        // Populate table
        addTeamToTable(game.getTeamA());
        addTeamToTable(game.getTeamB());
    }

    private void addTeamToTable(com.scorenexus.model.Team team) {
        for (Player p : team.getPlayers()) {
            Object[] row = {
                team.getName(),
                p.getName(),
                p.getNumber(),
                p.getGoals(),
                p.getAssists(),
                p.getYellowCards(),
                p.hasRedCard() ? "1" : "0"
            };
            tableModel.addRow(row);
        }
    }
    
    private void exportData() {
        if (game == null) return;
        try {
            CSVExporter.exportGameSummary(game);
            CSVExporter.exportGameTimeline(game);
            CSVExporter.exportPlayerStats(game);
            JOptionPane.showMessageDialog(this, "Data exported successfully to 3 CSV files!", "Export Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error exporting data: " + e.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}