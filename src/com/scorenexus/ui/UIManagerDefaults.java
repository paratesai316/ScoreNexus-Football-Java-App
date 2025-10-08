package com.scorenexus.ui;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;

public class UIManagerDefaults {
    public static void setModernLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        Color darkGray = new Color(60, 63, 65);
        UIManager.put("PopupMenu.background", new ColorUIResource(darkGray));
        UIManager.put("PopupMenu.border", BorderFactory.createLineBorder(Color.GRAY));
    }
}