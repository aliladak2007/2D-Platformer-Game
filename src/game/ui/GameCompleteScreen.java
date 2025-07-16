package game.ui;

import game.audio.SoundManager;
import game.util.FontManager;
import game.core.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * The screen shown when all levels are completed.
 * Displays the best three times (fastest first).
 * <p>
 * This celebratory screen serves as the final interface presented to players after
 * successfully completing all game levels. It features:
 * <ul>
 *   <li>A congratulatory title announcing game completion</li>
 *   <li>A ranked list of the top three completion times</li>
 *   <li>A "Play Again" button that allows players to restart the game</li>
 * </ul>
 * </p>
 * <p>
 * The screen uses a distinctive royal purple background with gold text to create
 * a visually rewarding experience that acknowledges the player's achievement.
 * If no high scores exist yet, an appropriate message is displayed instead.
 * </p>
 */
public class GameCompleteScreen extends JPanel {

    /**
     * Constructs the Game Complete screen.
     * <p>
     * The screen is organized into three main sections:
     * <ol>
     *   <li><b>Title Section (NORTH)</b>: Contains the "Game Completed!" heading with spacing</li>
     *   <li><b>Time List Section (CENTER)</b>: Displays the top three completion times in a scrollable panel</li>
     *   <li><b>Button Section (SOUTH)</b>: Contains the "Play Again" button to restart the game</li>
     * </ol>
     * </p>
     * <p>
     * The color scheme uses:
     * <ul>
     *   <li>Background: Royal Purple (RGB: 102, 51, 153)</li>
     *   <li>Text and borders: Pacman Yellow (RGB: 255, 222, 0)</li>
     *   <li>Button background: Dark Purple (RGB: 60, 0, 60)</li>
     * </ul>
     * </p>
     *
     * @param frame         The main game window, used to dispose the current window when restarting.
     * @param bestTimeArray An array containing the top three times (in ms), ordered fastest first.
     *                      If null, empty, or contains only {@code Long.MAX_VALUE} entries, a "No high scores yet" message is shown.
     */
    public GameCompleteScreen(JFrame frame, long[] bestTimeArray) {
        setLayout(new BorderLayout());
        setBackground(new Color(102, 51, 153)); // Royal Purple

        // --- Title Section ---
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(new Color(102, 51, 153));
        titlePanel.add(Box.createVerticalStrut(30));

        JLabel title = new JLabel("Game Completed!", SwingConstants.CENTER);
        title.setFont(FontManager.getFont("default", Font.BOLD, 26f));
        title.setForeground(new Color(255, 222, 0));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(20));

        add(titlePanel, BorderLayout.NORTH);

        // --- Time List Section ---
        JPanel timePanel = new JPanel();
        timePanel.setLayout(new BorderLayout());
        timePanel.setBackground(new Color(102, 51, 153));
        timePanel.add(Box.createVerticalStrut(20), BorderLayout.NORTH);

        if (bestTimeArray == null || bestTimeArray.length == 0 || bestTimeArray[0] == Long.MAX_VALUE) {
            JLabel noScore = new JLabel("No high scores yet.", SwingConstants.CENTER);
            noScore.setFont(FontManager.getFont("default", 16f));
            noScore.setForeground(new Color(255, 222, 0));
            noScore.setAlignmentX(Component.CENTER_ALIGNMENT);
            timePanel.add(noScore, BorderLayout.CENTER);
        } else {
            // Create a scrollable list to display all times
            JList<String> timeList = new JList<>();
            DefaultListModel<String> listModel = new DefaultListModel<>();

            for (int i = 0; i < bestTimeArray.length; i++) {
                long time = bestTimeArray[i];
                if (time == Long.MAX_VALUE) continue;
                String formattedTime = formatTime(time);
                listModel.addElement("Rank " + (i + 1) + ": " + formattedTime + " seconds");
            }

            // Custom renderer to center text and add spacing
            timeList.setCellRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    label.setFont(FontManager.getFont("default", 16f));
                    label.setForeground(new Color(255, 222, 0));
                    label.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Add vertical spacing
                    return label;
                }
            });

            timeList.setModel(listModel);
            timeList.setBackground(new Color(102, 51, 153));
            timeList.setVisibleRowCount(10);

            JScrollPane scrollPane = new JScrollPane(timeList);
            scrollPane.setBorder(BorderFactory.createLineBorder(new Color(255, 222, 0), 1));
            scrollPane.getViewport().setOpaque(false);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setPreferredSize(new Dimension(400, 300));

            timePanel.add(scrollPane, BorderLayout.CENTER);
        }

        add(timePanel, BorderLayout.CENTER);

        // --- Button Section ---
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);

        JButton restartButton = new JButton("Play Again");
        restartButton.setFont(FontManager.getFont("default", Font.BOLD, 18f));
        restartButton.setForeground(new Color(255, 222, 0));
        restartButton.setBackground(new Color(60, 0, 60));
        restartButton.setFocusPainted(false);
        restartButton.setBorderPainted(false);
        restartButton.setContentAreaFilled(true);
        restartButton.setOpaque(true);
        restartButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        restartButton.addActionListener((ActionEvent e) -> {
            SoundManager.playButtonSound();
            frame.dispose();
            new Game(1); // Restart from level 1
        });

        buttonPanel.add(restartButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Formats a given time in milliseconds into a string of format mm:ss.mmm.
     * <p>
     * This method converts raw milliseconds into a human-readable time format
     * with minutes, seconds, and milliseconds. The format follows the pattern:
     * <pre>MM:SS.mmm</pre>
     * where:
     * <ul>
     *   <li>MM = minutes (zero-padded to 2 digits)</li>
     *   <li>SS = seconds (zero-padded to 2 digits)</li>
     *   <li>mmm = milliseconds (zero-padded to 3 digits)</li>
     * </ul>
     * </p>
     * <p>
     * For example, 65432 milliseconds would be formatted as "01:05.432".
     * </p>
     *
     * @param ms Time in milliseconds.
     * @return A formatted string in the pattern "MM:SS.mmm".
     */
    private String formatTime(long ms) {
        long minutes = ms / 60000;
        long seconds = (ms % 60000) / 1000;
        long millis = ms % 1000;
        return String.format("%02d:%02d.%03d", minutes, seconds, millis);
    }
}