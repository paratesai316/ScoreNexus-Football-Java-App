package com.scorenexus.ui;

import com.scorenexus.model.Player;
import com.scorenexus.model.Team;
import javax.swing.*;
import javax.swing.plaf.basic.BasicMenuItemUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.net.URL;

public class PlayerCircle extends JComponent {
    private final Player player;
    private final GamePanel gamePanel;
    private final boolean isTeamA;

    // MODIFICATION: Re-implementing free-form drag logic
    private Point dragStartPoint;
    private boolean isDraggable = true;

    private static Image goalImg, assistImg, yellowCardImg, redCardImg, subInImg, subOutImg;

    static {
        goalImg = loadImage("resources/image_goal.png");
        assistImg = loadImage("resources/image_assist.png");
        yellowCardImg = loadImage("resources/image_yellow_card.png");
        redCardImg = loadImage("resources/image_red_card.png");
        subInImg = loadImage("resources/image_sub_in.png");
        subOutImg = loadImage("resources/image_sub_out.png");
    }

    private static Image loadImage(String path) {
        URL url = PlayerCircle.class.getClassLoader().getResource(path);
        if (url != null) {
            return new ImageIcon(url).getImage();
        } else {
            System.err.println("ERROR: Could not find image resource at: " + path);
            return null;
        }
    }

    public PlayerCircle(Player player, GamePanel gamePanel, boolean isTeamA) {
        this.player = player;
        this.gamePanel = gamePanel;
        this.isTeamA = isTeamA;

        // MODIFICATION: Reverting to the original mouse listeners for free dragging.
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (isDraggable && SwingUtilities.isLeftMouseButton(e)) {
                    dragStartPoint = e.getPoint();
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isDraggable && dragStartPoint != null && SwingUtilities.isLeftMouseButton(e)) {
                    Point currentPos = getLocation();
                    int newX = currentPos.x + e.getX() - dragStartPoint.x;
                    int newY = currentPos.y + e.getY() - dragStartPoint.y;
                    setLocation(newX, newY);
                }
            }
        });

        setSize(50, 50);
        setPreferredSize(new Dimension(50, 50));
        setToolTipText(player.getName());
        createPopupMenu();
    }

    public void setDraggable(boolean draggable) {
        this.isDraggable = draggable;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int circleSize = Math.min(w, h) - 4;
        int circleX = (w - circleSize) / 2;
        int circleY = (h - circleSize) / 2;

        if (isTeamA) g2d.setColor(new Color(220, 30, 30)); else g2d.setColor(new Color(30, 30, 220));
        g2d.fillOval(circleX, circleY, circleSize, circleSize);
        g2d.setColor(Color.WHITE);
        g2d.drawOval(circleX, circleY, circleSize, circleSize);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
        String number = String.valueOf(player.getNumber());
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(number, (w - fm.stringWidth(number)) / 2, (h - fm.getHeight()) / 2 + fm.getAscent());

        // MODIFICATION: Icon size is now smaller.
        int iconSize = (int) (circleSize * 0.4);
        
        // MODIFICATION: Icon positions are updated as per your request.
        if (player.getGoals() > 0) drawIcon(g2d, goalImg, w - iconSize/2, h - iconSize/2, iconSize);          // Bottom-right
        if (player.getAssists() > 0) drawIcon(g2d, assistImg, iconSize/2, h - iconSize/2, iconSize);       // Bottom-left
        if (player.hasRedCard()) drawIcon(g2d, redCardImg, iconSize/2, iconSize/2, iconSize);              // Top-left (replaces yellow)
        else if (player.getYellowCards() > 0) drawIcon(g2d, yellowCardImg, iconSize/2, iconSize/2, iconSize); // Top-left
        if (player.wasSubstitutedOut()) drawIcon(g2d, subOutImg, w - iconSize/2, iconSize/2, iconSize);      // Top-right
        if (player.wasSubstitutedIn()) drawIcon(g2d, subInImg, w - iconSize/2, iconSize/2, iconSize);       // Top-right
    }

    private void drawIcon(Graphics2D g2d, Image img, int centerX, int centerY, int size) {
        if (img != null) {
            g2d.drawImage(img, centerX - size / 2, centerY - size / 2, size, size, null);
        }
    }
    
    public Player getPlayer() {
        return player;
    }
    
    // The menu creation logic is unchanged from the last fix.
    private void createPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        Team playerTeam = isTeamA ? gamePanel.getGame().getTeamA() : gamePanel.getGame().getTeamB();
        JMenuItem goalItem = createStyledMenuItem("Goal ⚽", e -> gamePanel.recordGoal(player, playerTeam));
        JMenuItem assistItem = createStyledMenuItem("Assist 👟", e -> gamePanel.recordAssist(player, playerTeam));
        JMenuItem yellowItem = createStyledMenuItem("Yellow Card 🟨", e -> gamePanel.recordYellowCard(player, playerTeam));
        JMenuItem redItem = createStyledMenuItem("Red Card 🟥", e -> gamePanel.recordRedCard(player, playerTeam, false));
        JMenuItem subItem = createStyledMenuItem("Substitute 🔄", e -> gamePanel.performSubstitution(player, playerTeam));
        popupMenu.add(goalItem);
        popupMenu.add(assistItem);
        popupMenu.add(yellowItem);
        popupMenu.add(redItem);
        popupMenu.add(subItem);
        setComponentPopupMenu(popupMenu);
    }
    private JMenuItem createStyledMenuItem(String text, java.awt.event.ActionListener listener) {
        JMenuItem item = new JMenuItem(text);
        item.addActionListener(listener);
        item.setBackground(Color.WHITE);
        item.setForeground(Color.BLACK);
        item.setUI(new BasicMenuItemUI() {
            @Override
            protected void paintBackground(Graphics g, JMenuItem menuItem, Color bgColor) {
                ButtonModel model = menuItem.getModel();
                Color oldColor = g.getColor();
                if (model.isArmed() || model.isSelected()) {
                    g.setColor(new Color(200, 225, 255));
                    g.fillRect(0, 0, menuItem.getWidth(), menuItem.getHeight());
                } else {
                    super.paintBackground(g, menuItem, menuItem.getBackground());
                }
                g.setColor(oldColor);
            }
        });
        return item;
    }
}