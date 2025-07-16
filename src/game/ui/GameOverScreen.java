package game.ui;

import game.audio.SoundManager;
import game.util.FontManager;
import game.core.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Represents the Game Over screen shown when the player loses.
 * <p>
 * This panel displays a "Game Over!" message along with buttons that allow
 * the player to either restart the current level or exit the application.
 * </p>
 * <p>
 * The Game Over screen appears when:
 * <ul>
 *   <li>The player's health reaches zero</li>
 *   <li>The player falls off the screen (below y = -15)</li>
 *   <li>The player encounters a fatal obstacle</li>
 * </ul>
 * </p>
 * <p>
 * The screen features a classic arcade-style design with:
 * <ul>
 *   <li>A black background reminiscent of classic arcade games</li>
 *   <li>A prominent red "Game Over!" message in the center</li>
 *   <li>Two action buttons at the bottom: "Restart Level" and "Exit"</li>
 * </ul>
 * </p>
 */
public class GameOverScreen extends JPanel {
    /**
     * The main application window that contains this Game Over screen.
     * Used for disposing the frame when restarting the level.
     */
    private final JFrame frame;

    /**
     * The level at which the game was over.
     * This information is used when restarting the level.
     */
    private final int currentLevel;

    /**
     * A reference to the main {@code Game} instance used for restarting the level.
     * This allows the restart button to create a new game at the appropriate level.
     */
    private final Game game;

    /**
     * Constructs a {@code GameOverScreen} panel.
     * <p>
     * This constructor initializes the Game Over screen with:
     * <ol>
     *   <li>A black background</li>
     *   <li>A centered "Game Over!" label in red</li>
     *   <li>Two buttons at the bottom:
     *     <ul>
     *       <li>"Restart Level" - Restarts the current level</li>
     *       <li>"Exit" - Terminates the application</li>
     *     </ul>
     *   </li>
     * </ol>
     * </p>
     * <p>
     * The color scheme uses:
     * <ul>
     *   <li>Background: Black</li>
     *   <li>"Game Over!" text: Ghost Red (RGB: 255, 0, 0)</li>
     *   <li>Button text: Pacman Yellow (RGB: 255, 222, 0)</li>
     *   <li>Button background: Deep Purple (RGB: 60, 0, 60)</li>
     * </ul>
     * </p>
     *
     * @param frame The {@link JFrame} that hosts this panel. Used to dispose the window when restarting.
     * @param level The current level number when the game ended. Used to restart at the same level.
     * @param game  The main {@link Game} instance used to restart the level via {@link Game#restartLevel()}.
     */
    public GameOverScreen(JFrame frame, int level, Game game) {
        super();
        this.frame = frame;
        this.currentLevel = level;
        this.game = game;

        this.setLayout(new BorderLayout());
        this.setBackground(Color.BLACK); // Pac-Man style background

        // Create a label to display "Game Over!" in the center
        JLabel gameOverLabel = new JLabel("Game Over!", SwingConstants.CENTER);
        gameOverLabel.setFont(FontManager.getFont("default", Font.BOLD, 26f));
        gameOverLabel.setForeground(new Color(255, 0, 0)); // Ghost red
        this.add(gameOverLabel, BorderLayout.CENTER);

        // Create the "Restart Level" button
        JButton restartButton = new JButton("Restart Level");
        restartButton.setFont(FontManager.getFont("default", Font.PLAIN, 10f));
        restartButton.setForeground(new Color(255, 222, 0)); // Pacman Yellow
        restartButton.setBackground(new Color(60, 0, 60));   // Deep purple
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SoundManager.playButtonSound();
                game.restartLevel();
                frame.dispose();
            }
        });

        // Create the "Exit" button
        JButton exitButton = new JButton("Exit");
        exitButton.setFont(FontManager.getFont("default", Font.PLAIN, 10f));
        exitButton.setForeground(new Color(255, 222, 0)); // Pacman Yellow
        exitButton.setBackground(new Color(60, 0, 60));   // Deep purple
        exitButton.addActionListener(e -> {
            SoundManager.playButtonSound();
            System.exit(0);
        });

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false); // Transparent to show black background
        buttonPanel.add(restartButton);
        buttonPanel.add(exitButton);
        this.add(buttonPanel, BorderLayout.SOUTH);
    }
}