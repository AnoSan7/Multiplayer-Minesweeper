package ui;

import java.awt.CardLayout;
import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import ui.pages.AboutPage;
import ui.pages.GamePage;
import ui.pages.HelpPage;
import ui.pages.HomePage;

public class MainFrame extends JFrame {
    private static MainFrame instance;
    private CardLayout cardLayout;
    private JPanel contentPanel;

    public static final String HOME_PAGE = "HOME";
    public static final String GAME_PAGE = "GAME";
    public static final String HELP_PAGE = "HELP";
    public static final String ABOUT_PAGE = "ABOUT";

    private MainFrame() {
        setTitle("Multiplayer Minesweeper");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        initializePages();
        add(contentPanel);
    }

    private void initializePages() {
        contentPanel.add(new HomePage(), HOME_PAGE);
        contentPanel.add(new GamePage(), GAME_PAGE);
        contentPanel.add(new HelpPage(), HELP_PAGE);
        contentPanel.add(new AboutPage(), ABOUT_PAGE);
    }

    public static MainFrame getInstance() {
        if (instance == null) {
            instance = new MainFrame();
        }
        return instance;
    }

    public void navigateTo(String page) {
        cardLayout.show(contentPanel, page);
    }

    public void refreshGamePage() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Find and remove the existing GamePage using the constraint
                Component[] components = contentPanel.getComponents();
                for (Component comp : components) {
                    if (comp instanceof GamePage) {
                        contentPanel.remove(comp);
                        break;
                    }
                }

                // Add new GamePage
                GamePage newGamePage = new GamePage();
                contentPanel.add(newGamePage, GAME_PAGE);

                // Revalidate and repaint
                contentPanel.revalidate();
                contentPanel.repaint();

                // Show the game page
                cardLayout.show(contentPanel, GAME_PAGE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}