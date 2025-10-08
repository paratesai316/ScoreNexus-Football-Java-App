package com.scorenexus.main;

import com.scorenexus.db.DatabaseManager;
import com.scorenexus.model.Game;
import com.scorenexus.ui.GamePanel;
import com.scorenexus.ui.SetupPanel;
import com.scorenexus.ui.SummaryPanel;
import com.scorenexus.ui.UIManagerDefaults;

import javax.swing.*;
import java.awt.*;

public class ScoreNexusApp {

    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private GamePanel gamePanel;
    private SummaryPanel summaryPanel;

    private Game currentGame;
    private DatabaseManager dbManager;

    public ScoreNexusApp() {
        UIManagerDefaults.setModernLookAndFeel();
        dbManager = new DatabaseManager();
        dbManager.createNewDatabase();

        frame = new JFrame("ScoreNexus - Football Score Tracker");
        frame.setIconImage(new ImageIcon("resources/app_icon.png").getImage());

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        SetupPanel setupPanel = new SetupPanel(this);
        gamePanel = new GamePanel(this);
        summaryPanel = new SummaryPanel(this);

        mainPanel.add(setupPanel, "SETUP");
        mainPanel.add(gamePanel, "GAME");
        mainPanel.add(summaryPanel, "SUMMARY");

        frame.add(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void startGame(Game game) {
        this.currentGame = game;
        long gameId = dbManager.insertNewGame(game.getTeamA().getName(), game.getTeamB().getName(), game.getGameDurationMinutes());
        this.currentGame.setGameId(gameId);
        
        gamePanel.initializeGame(currentGame);
        cardLayout.show(mainPanel, "GAME");
    }
    
    public void endGame() {
        dbManager.finalizeGame(currentGame);
        summaryPanel.loadSummary(currentGame);
        cardLayout.show(mainPanel, "SUMMARY");
    }

    public void createNewGame() {
        cardLayout.show(mainPanel, "SETUP");
    }

    public DatabaseManager getDbManager() {
        return dbManager;
    }

    public Game getCurrentGame() {
        return currentGame;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ScoreNexusApp::new);
    }
}