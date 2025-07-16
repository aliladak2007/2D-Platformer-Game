package game.ui;

import game.audio.SoundManager;
import game.core.Game;
import game.ui.BestTimesScreen;
import game.util.FontManager;
import game.util.HighScoreManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The main entry point and title screen for the 2D platformer game.
 * <p>
 * This screen serves as the first interface presented to players when launching
 * the game. It displays the game title "PIXEL QUEST" and provides options to:
 * <ul>
 *   <li>Start a new game from level 1</li>
 *   <li>Load a previously saved game</li>
 *   <li>View the game controls</li>
 *   <li>View the best completion times</li>
 *   <li>Exit the game</li>
 * </ul>
 * </p>
 * <p>
 * The visual design is inspired by classic arcade games like Pac-Man, featuring:
 * <ul>
 *   <li>A dark blue background reminiscent of the Pac-Man startup screen</li>
 *   <li>Bright yellow text and button borders for high contrast</li>
 *   <li>Deep purple button backgrounds</li>
 *   <li>Pixel-style fonts for an authentic retro gaming feel</li>
 * </ul>
 * </p>
 * <p>
 * This class extends {@link JFrame} to create a standalone window that
 * serves as the container for all startup screen components.
 * </p>
 */
public class StartupScreen extends JFrame {

    /**
     * Constructs and initializes the game's startup screen.
     * <p>
     * This constructor:
     * <ol>
     *   <li>Sets up the main application window properties (size, title, etc.)</li>
     *   <li>Creates the UI components with a retro arcade game aesthetic</li>
     *   <li>Organizes the components in a vertical layout</li>
     *   <li>Configures action listeners for the buttons</li>
     * </ol>
     * </p>
     * <p>
     * The screen contains three main sections:
     * <ul>
     *   <li>Title section: Displays "PIXEL QUEST" in large, bold text</li>
     *   <li>Button section: Contains "Play", "Load Game", "View Best Times", and "Exit" buttons</li>
     *   <li>Footer section: Displays a prompt "Press Play to Begin"</li>
     * </ul>
     * </p>
     * <p>
     * Button actions:
     * <ul>
     *   <li>"Play": Creates a new {@link Game} instance starting at level 1</li>
     *   <li>"Load Game": Attempts to load a saved game using {@link Game#loadGameFromFile()}</li>
     *   <li>"View Best Times": Opens the {@link BestTimesScreen} to show the best completion times</li>
     *   <li>"Exit": Terminates the application</li>
     * </ul>
     * </p>
     */
    public StartupScreen() {
        SoundManager.getInstance();
        setTitle("Pixel Quest");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);

        // Pac-Man inspired color palette
        Color backgroundPurple = new Color(0, 0, 128);       // Pac-Man startup screen background
        Color pacmanYellow = new Color(255, 222, 0);         // Pac-Man yellow
        Color ghostPurple = new Color(60, 0, 60);            // Button background

        // --- Main Panel ---
        JPanel panel = new JPanel();
        panel.setBackground(backgroundPurple);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // --- Title ---
        JLabel titleLabel = new JLabel("PIXEL QUEST", SwingConstants.CENTER);
        titleLabel.setFont(FontManager.getFont("emulogic", Font.BOLD, 36f));
        titleLabel.setForeground(pacmanYellow);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalStrut(30));
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(40));

        // --- Button Panel ---
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        JButton playButton = new JButton("Play");
        JButton loadButton = new JButton("Load Game");
        JButton bestTimesButton = new JButton("View Best Times");
        JButton controlsButton = new JButton("Controls");
        JButton exitButton = new JButton("Exit");

        // Set common properties for all buttons
        Dimension buttonSize = new Dimension(200, 40);
        Font buttonFont = FontManager.getFont("default", Font.BOLD, 18f);

        for (JButton button : new JButton[]{playButton, loadButton,controlsButton, exitButton}) {
            button.setFont(buttonFont);
            button.setBackground(ghostPurple);
            button.setForeground(pacmanYellow);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createLineBorder(pacmanYellow, 2));
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setPreferredSize(buttonSize);
            button.setMaximumSize(buttonSize);
        }

        // Configure the View Best Times button separately with a larger size
        bestTimesButton.setFont(buttonFont);
        bestTimesButton.setBackground(ghostPurple);
        bestTimesButton.setForeground(pacmanYellow);
        bestTimesButton.setFocusPainted(false);
        bestTimesButton.setBorder(BorderFactory.createLineBorder(pacmanYellow, 2));
        bestTimesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        bestTimesButton.setPreferredSize(new Dimension(300, 50)); // Larger size
        bestTimesButton.setMaximumSize(bestTimesButton.getPreferredSize());
        buttonPanel.add(playButton);
        buttonPanel.add(Box.createVerticalStrut(15));
        buttonPanel.add(loadButton);
        buttonPanel.add(Box.createVerticalStrut(15));
        buttonPanel.add(bestTimesButton);
        buttonPanel.add(Box.createVerticalStrut(15));
        buttonPanel.add(controlsButton);
        buttonPanel.add(Box.createVerticalStrut(15));
        buttonPanel.add(exitButton);

        panel.add(buttonPanel);
        panel.add(Box.createVerticalStrut(30));

        // --- Footer Tagline ---
        JLabel footer = new JLabel("Press Play to Begin", SwingConstants.CENTER);
        footer.setFont(FontManager.getFont("default", Font.PLAIN, 12f));
        footer.setForeground(pacmanYellow);
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(footer);
        panel.add(Box.createVerticalGlue());

        // --- Add panel to frame ---
        add(panel);
        setVisible(true);

        // --- Actions ---
        playButton.addActionListener(e -> {
            SoundManager.playButtonSound();
            new Game(1);
            dispose();
        });

        loadButton.addActionListener(e -> {
            SoundManager.playButtonSound();
            Game loadedGame = Game.loadGameFromFile();
            if (loadedGame != null) {
                dispose();
            }
        });

        bestTimesButton.addActionListener(e -> {
            SoundManager.playButtonSound();
            JFrame bestTimesFrame = new JFrame("Best Times");
            bestTimesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            bestTimesFrame.add(new BestTimesScreen(bestTimesFrame));
            bestTimesFrame.pack();
            bestTimesFrame.setLocationRelativeTo(null);
            bestTimesFrame.setVisible(true);
        });

        controlsButton.addActionListener(e -> { // Add action listener for Controls button
            SoundManager.playButtonSound();
            JFrame controlsFrame = new JFrame("Game Controls");
            controlsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            controlsFrame.add(new ControlsPanel(null)); // Pass null since we're not in a game context
            controlsFrame.pack();
            controlsFrame.setSize(600,400);
            controlsFrame.setLocationRelativeTo(null);
            controlsFrame.setVisible(true);
        });

        exitButton.addActionListener(e -> {
            SoundManager.playButtonSound();
            System.exit(0);
        });
    }
}