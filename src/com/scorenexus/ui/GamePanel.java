package com.scorenexus.ui;

import com.scorenexus.main.ScoreNexusApp;
import com.scorenexus.model.Game;
import com.scorenexus.model.GameEvent;
import com.scorenexus.model.Player;
import com.scorenexus.model.Team;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.stream.Collectors;

public class GamePanel extends JPanel {
    private final ScoreNexusApp app;
    private Game game;
    private JLabel timerLabel, scoreLabel, halfTimeLabel;
    private JPanel pitchPanel;
    private JList<String> teamASubsList, teamBSubsList;
    private JButton startButton, stopButton, resumeButton, endButton;
    private Timer gameTimer;
    private long elapsedTimeSeconds = 0;
    private boolean isPaused = true;
    private boolean isHalftime = false;

    public GamePanel(ScoreNexusApp app) {
        this.app = app;
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(50, 50, 50));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new BorderLayout()); topPanel.setOpaque(false); JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0)); infoPanel.setOpaque(false); timerLabel = new JLabel("00:00"); timerLabel.setFont(new Font("Segoe UI", Font.BOLD, 48)); timerLabel.setForeground(Color.WHITE); scoreLabel = new JLabel("Team A 0 - 0 Team B"); scoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 24)); scoreLabel.setForeground(Color.WHITE); halfTimeLabel = new JLabel(""); halfTimeLabel.setFont(new Font("Segoe UI", Font.BOLD, 20)); halfTimeLabel.setForeground(Color.ORANGE); infoPanel.add(scoreLabel); infoPanel.add(timerLabel); infoPanel.add(halfTimeLabel); topPanel.add(infoPanel, BorderLayout.CENTER); JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); controlPanel.setOpaque(false); startButton = new JButton("Start Game"); stopButton = new JButton("Pause"); resumeButton = new JButton("Resume"); endButton = new JButton("End Game"); setupButton(startButton); setupButton(stopButton); setupButton(resumeButton); setupButton(endButton); controlPanel.add(startButton); controlPanel.add(stopButton); controlPanel.add(resumeButton); controlPanel.add(endButton); topPanel.add(controlPanel, BorderLayout.SOUTH); add(topPanel, BorderLayout.NORTH);

        teamASubsList = new JList<>(); teamBSubsList = new JList<>(); JScrollPane subsAScroll = new JScrollPane(teamASubsList); JScrollPane subsBScroll = new JScrollPane(teamBSubsList); subsAScroll.getViewport().setBackground(new Color(70, 70, 70)); subsBScroll.getViewport().setBackground(new Color(70, 70, 70)); setupSubsPanel(teamASubsList); setupSubsPanel(teamBSubsList); add(createTitledPanel(subsAScroll, "Team A Subs"), BorderLayout.WEST); add(createTitledPanel(subsBScroll, "Team B Subs"), BorderLayout.EAST);

        pitchPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(0, 120, 0));
                g.fillRect(0, 0, getWidth(), getHeight());
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 255, 255, 128));
                g2d.setStroke(new BasicStroke(2));
                int w = getWidth(); int h = getHeight(); g2d.drawRect(10, 10, w - 20, h - 20); g2d.drawLine(w / 2, 10, w / 2, h - 10); int ccd = Math.min(w, h) / 5; g2d.drawOval(w / 2 - ccd / 2, h / 2 - ccd / 2, ccd, ccd); g2d.drawRect(10, h / 2 - h / 4, w / 6, h / 2); g2d.drawRect(w - 10 - w / 6, h / 2 - h / 4, w / 6, h / 2); g2d.drawRect(10, h/2 - h/8, w/12, h/4); g2d.drawRect(w - 10 - w/12, h/2 - h/8, w/12, h/4);
            }
        };
        pitchPanel.setLayout(null); 
        add(pitchPanel, BorderLayout.CENTER);

        gameTimer = new Timer(1000, e -> updateTimer()); startButton.addActionListener(e -> startGame()); stopButton.addActionListener(e -> pauseGame()); resumeButton.addActionListener(e -> resumeGame()); endButton.addActionListener(e -> { if (JOptionPane.showConfirmDialog(this, "Are you sure you want to end the game?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) { gameTimer.stop(); app.endGame(); } });
    }

    public void initializeGame(Game game) {
        this.game = game;
        pitchPanel.removeAll();
        elapsedTimeSeconds = 0;
        isPaused = true;
        isHalftime = false;
        updateScoreLabel();
        updateTimerLabel();
        halfTimeLabel.setText("");

        SwingUtilities.invokeLater(() -> {
            positionPlayers(game.getTeamA(), true);
            positionPlayers(game.getTeamB(), false);
            pitchPanel.revalidate();
            pitchPanel.repaint();
        });
        updateSubsLists();
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        resumeButton.setEnabled(false);
        endButton.setEnabled(true);
        JOptionPane.showMessageDialog(this, "Game created! Drag players into position, then press 'Start Game'.", "Setup Formations", JOptionPane.INFORMATION_MESSAGE);
    }

    private void positionPlayers(Team team, boolean isTeamA) {
        if (pitchPanel.getWidth() == 0) return; 

        int xOffset = isTeamA ? 50 : pitchPanel.getWidth() - 100;
        int numPlayers = (int) team.getPlayers().stream().filter(p -> !p.isSubstitute()).count();
        int ySpacing = (pitchPanel.getHeight() - 40) / (Math.max(1, numPlayers));
        int currentY = 20;

        for (Player p : team.getPlayers()) {
            if (!p.isSubstitute()) {
                PlayerCircle pc = new PlayerCircle(p, this, isTeamA);
                pc.setBounds(xOffset, currentY, 50, 50);
                pitchPanel.add(pc);
                currentY += ySpacing;
            }
        }
    }

    private void startGame() {
        isPaused = false;
        gameTimer.start();
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        resumeButton.setEnabled(false);

        for (Component comp : pitchPanel.getComponents()) {
            if (comp instanceof PlayerCircle) {
                ((PlayerCircle) comp).setDraggable(false);
            }
        }
    }

    public void recordGoal(Player player, Team team) { player.addGoal(); team.incrementScore(); updateScoreLabel(); logEvent("Goal scored by " + player.getName(), team.getName()); findAndRepaintPlayerCircle(player); }
    public void recordAssist(Player player, Team team) { player.addAssist(); logEvent("Assist by " + player.getName(), team.getName()); findAndRepaintPlayerCircle(player); }
    public void recordYellowCard(Player player, Team team) { player.addYellowCard(); logEvent("Yellow card for " + player.getName(), team.getName()); findAndRepaintPlayerCircle(player); if (player.getYellowCards() >= 2) { recordRedCard(player, team, true); } }

    public void recordRedCard(Player player, Team team, boolean fromTwoYellows) {
        player.setHasRedCard(true);
        String reason = fromTwoYellows ? " (2nd yellow)" : "";
        logEvent("Red card for " + player.getName() + reason, team.getName());
        Component toRemove = null;
        for (Component comp : pitchPanel.getComponents()) {
            if (comp instanceof PlayerCircle && ((PlayerCircle) comp).getPlayer() == player) {
                toRemove = comp;
                break;
            }
        }
        if (toRemove != null) {
            pitchPanel.remove(toRemove);
            pitchPanel.revalidate();
            pitchPanel.repaint();
        }
    }

    public void performSubstitution(Player playerOut, Team team) {
        java.util.List<Player> subs = team.getPlayers().stream().filter(Player::isSubstitute).collect(Collectors.toList());
        if (subs.isEmpty()) { JOptionPane.showMessageDialog(this, "No substitutes available.", "Substitution", JOptionPane.WARNING_MESSAGE); return; }
        String[] subNames = subs.stream().map(Player::getName).toArray(String[]::new);
        String selectedSubName = (String) JOptionPane.showInputDialog(this, "Substitute for " + playerOut.getName(), "Select Substitute", JOptionPane.PLAIN_MESSAGE, null, subNames, subNames[0]);
        if (selectedSubName != null) {
            Player playerIn = subs.stream().filter(p -> p.getName().equals(selectedSubName)).findFirst().orElse(null);
            if (playerIn != null) {
                playerOut.setSubstitute(true);
                playerOut.setWasSubstitutedOut(true);
                playerIn.setSubstitute(false);
                Component toRemove = null;
                Point oldPos = null;
                for (Component comp : pitchPanel.getComponents()) {
                    if (comp instanceof PlayerCircle && ((PlayerCircle) comp).getPlayer() == playerOut) {
                        toRemove = comp;
                        oldPos = comp.getLocation();
                        break;
                    }
                }
                if (toRemove != null) {
                    pitchPanel.remove(toRemove);
                    PlayerCircle pcIn = new PlayerCircle(playerIn, this, team == game.getTeamA());
                    pcIn.setBounds(oldPos.x, oldPos.y, 50, 50);
                    pitchPanel.add(pcIn);
                    pitchPanel.revalidate();
                    pitchPanel.repaint();
                }
                updateSubsLists();
                logEvent(playerOut.getName() + " substituted by " + playerIn.getName(), team.getName());
            }
        }
    }

    private void findAndRepaintPlayerCircle(Player player) { for (Component comp : pitchPanel.getComponents()) { if (comp instanceof PlayerCircle && ((PlayerCircle) comp).getPlayer() == player) { comp.repaint(); return; } } }
    private void pauseGame() { isPaused = true; gameTimer.stop(); stopButton.setEnabled(false); resumeButton.setEnabled(true); }
    private void resumeGame() { isPaused = false; gameTimer.start(); if(isHalftime) halfTimeLabel.setText(""); stopButton.setEnabled(true); resumeButton.setEnabled(false); }
    public Game getGame() { return game; }
    private void setupButton(JButton button) { button.setFont(new Font("Segoe UI", Font.BOLD, 12)); button.setFocusPainted(false); }
    private JPanel createTitledPanel(Component component, String title) { JPanel panel = new JPanel(new BorderLayout()); panel.setOpaque(false); panel.setBorder(BorderFactory.createTitledBorder( BorderFactory.createLineBorder(Color.GRAY), title, TitledBorder.CENTER, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), Color.WHITE )); panel.add(component, BorderLayout.CENTER); return panel; }
    private void updateTimer() { if (!isPaused) { elapsedTimeSeconds++; updateTimerLabel(); if (!isHalftime && elapsedTimeSeconds >= (game.getGameDurationMinutes() * 60) / 2) { isHalftime = true; isPaused = true; gameTimer.stop(); halfTimeLabel.setText("HALF TIME"); stopButton.setEnabled(false); resumeButton.setEnabled(true); JOptionPane.showMessageDialog(this, "Half Time!", "Info", JOptionPane.INFORMATION_MESSAGE); } if (elapsedTimeSeconds >= game.getGameDurationMinutes() * 60) { gameTimer.stop(); app.endGame(); } } }
    private void updateTimerLabel() { long minutes = elapsedTimeSeconds / 60; long seconds = elapsedTimeSeconds % 60; timerLabel.setText(String.format("%02d:%02d", minutes, seconds)); }
    private void updateScoreLabel() { scoreLabel.setText(String.format("%s %d - %d %s", game.getTeamA().getName(), game.getTeamA().getScore(), game.getTeamB().getScore(), game.getTeamB().getName())); }
    private void setupSubsPanel(JList<String> list) { list.setBackground(new Color(70, 70, 70)); list.setForeground(Color.WHITE); list.setFont(new Font("Segoe UI", Font.PLAIN, 14)); list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); }
    private void updateSubsLists() { DefaultListModel<String> teamAModel = new DefaultListModel<>(); game.getTeamA().getPlayers().stream().filter(Player::isSubstitute).forEach(p -> teamAModel.addElement(p.getName() + " (#" + p.getNumber() + ")")); teamASubsList.setModel(teamAModel); DefaultListModel<String> teamBModel = new DefaultListModel<>(); game.getTeamB().getPlayers().stream().filter(Player::isSubstitute).forEach(p -> teamBModel.addElement(p.getName() + " (#" + p.getNumber() + ")")); teamBSubsList.setModel(teamBModel); }
    private void logEvent(String action, String teamName) { String timestamp = String.format("%02d:%02d", elapsedTimeSeconds / 60, elapsedTimeSeconds % 60); GameEvent event = new GameEvent(timestamp, action, teamName); game.addGameEvent(event); app.getDbManager().logEvent(game.getGameId(), timestamp, action, teamName); }
}