package game.ui;

import game.util.FontManager;
import game.core.Game;
import game.audio.SoundManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Represents the Level Complete screen shown when a player finishes a level.
 * <p>
 * Displays a congratulatory message and provides options to proceed to the
 * next level or exit the game entirely.
 * </p>
 * <p>
 * This screen appears when:
 * <ul>
 *   <li>The player collects all required collectibles in a level</li>
 *   <li>The player reaches a designated end point or goal</li>
 *   <li>The {@code GameWorld.checkLevelCompletion()} method determines all level objectives are met</li>
 * </ul>
 * </p>
 * <p>
 * The screen features a celebratory design with:
 * <ul>
 *   <li>A royal purple background signifying achievement</li>
 *   <li>A gold "Level Complete!" message prominently displayed</li>
 *   <li>Two action buttons: "Next Level" to progress and "Exit" to quit</li>
 * </ul>
 * </p>
 * <p>
 * When displayed, this screen also manages audio state by stopping background music
 * and any game over sounds that might be playing.
 * </p>
 */
public class LevelCompleteScreen extends JPanel {
    /**
     * The main application window hosting this screen.
     * Used for disposing the frame when proceeding to the next level.
     */
    private final JFrame frame;

    /**
     * The current level number that has just been completed.
     * Used to determine if there are more levels or if the game is complete.
     */
    private final int currentLevel;

    /**
     * A reference to the main {@code Game} instance. This is used to
     * facilitate level progression or trigger game completion.
     * Provides access to methods like {@link Game#progressToNextLevel()} and {@link Game#gameCompleted()}.
     */
    private final Game game;

    /**
     * Constructs a {@code LevelCompleteScreen} panel, providing a congratulatory
     * message and buttons to either proceed to the next level or exit the game.
     * <p>
     * The screen is organized into three main sections:
     * <ol>
     *   <li><b>Message Section (CENTER)</b>: Contains the "Level Complete!" heading</li>
     *   <li><b>Button Section (SOUTH)</b>: Contains the "Next Level" and "Exit" buttons</li>
     * </ol>
     * </p>
     * <p>
     * The color scheme uses:
     * <ul>
     *   <li>Background: Royal Purple (RGB: 102, 51, 153)</li>
     *   <li>Text and button text: Pacman Yellow (RGB: 255, 222, 0)</li>
     *   <li>Button background: Deep Purple (RGB: 60, 0, 60)</li>
     * </ul>
     * </p>
     * <p>
     * The "Next Level" button behavior depends on the current level:
     * <ul>
     *   <li>For levels 1-3: Proceeds to the next level</li>
     *   <li>For level 4 (final level): Triggers the game completion sequence</li>
     * </ul>
     * </p>
     *
     * @param frame  The {@link JFrame} hosting this panel. Used to dispose the window when proceeding.
     * @param level  The current level number that was just completed. Used to determine next actions.
     * @param game   The main {@code Game} instance for triggering progression or completion.
     */
    public LevelCompleteScreen(JFrame frame, int level, Game game) {
        super();
        this.frame = frame;         // Store the main application window
        this.currentLevel = level;  // Store the level that has been completed
        this.game = game;           // Reference to the Game instance for progression

        // Stop background music when level is complete
        SoundManager.getInstance().stopBackgroundMusic();

        // Use BorderLayout for straightforward positioning of components
        this.setLayout(new BorderLayout());

        // Set the background color to royal purple
        this.setBackground(new Color(102, 51, 153)); // Royal Purple

        // Create and configure a label to display a congratulatory message
        JLabel completeLabel = new JLabel("Level Complete!", SwingConstants.CENTER);
        completeLabel.setFont(FontManager.getFont("default", Font.BOLD, 26f));
        completeLabel.setForeground(new Color(255, 222, 0)); // Pacman Yellow
        this.add(completeLabel, BorderLayout.CENTER);

        // Create a button to proceed to the next level
        JButton nextLevelButton = new JButton("Next Level");
        nextLevelButton.setFont(FontManager.getFont("default", Font.PLAIN, 10f));
        nextLevelButton.setForeground(new Color(255, 222, 0)); // Pacman Yellow text
        nextLevelButton.setBackground(new Color(60, 0, 60));
        nextLevelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SoundManager.playButtonSound();

                // Stop and reset game over sound explicitly
                SoundManager.getInstance().stopGameOverSound();
                SoundManager.getInstance().resetGameOverSound();

                // Use the existing Game instance to progress to the next level
                if (currentLevel < 4) {
                    frame.dispose();           // Close the current window
                    game.progressToNextLevel(); // Progress to the next level
                } else {
                    // If the current level is 4, the game is considered complete
                    game.gameCompleted();
                }
            }
        });

        // Create a button to exit the game entirely
        JButton exitButton = new JButton("Exit");
        exitButton.setFont(FontManager.getFont("default", Font.PLAIN, 10f));
        exitButton.setForeground(new Color(255, 222, 0)); // Pacman Yellow text
        exitButton.setBackground(new Color(60, 0, 60));
        exitButton.addActionListener(e -> {
            SoundManager.playButtonSound();
            System.exit(0);
        });

        // Organize both buttons on a panel at the bottom
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false); // Allow underlying background to show through
        buttonPanel.add(nextLevelButton);
        buttonPanel.add(exitButton);
        this.add(buttonPanel, BorderLayout.SOUTH);
    }
}