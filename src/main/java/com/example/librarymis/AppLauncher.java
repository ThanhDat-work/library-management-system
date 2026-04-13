package com.example.librarymis;

import com.example.librarymis.config.DataSeeder;
import com.example.librarymis.config.JpaUtil;
import com.example.librarymis.view.LoginView;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;

public class AppLauncher {
    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(JpaUtil::shutdown));
        FlatLightLaf.setup();
        UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("Component.arc", 16);
        UIManager.put("TextComponent.arc", 16);
        UIManager.put("Button.arc", 18);
        UIManager.put("ScrollBar.showButtons", true);
        UIManager.put("Table.showHorizontalLines", true);
        UIManager.put("Table.showVerticalLines", false);
        UIManager.put("TitlePane.unifiedBackground", true);
        UIManager.put("TableHeader.height", 38);

        DataSeeder.seed();

        SwingUtilities.invokeLater(() -> new LoginView().setVisible(true));
    }
}
