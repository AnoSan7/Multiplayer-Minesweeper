import javax.swing.SwingUtilities;

import ui.MainFrame;
import ui.theme.DarkTheme;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set the dark theme
                DarkTheme.apply();
                // Create and show the main window
                MainFrame.getInstance().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}