package game.ui;

import game.audio.SoundManager;
import game.util.FontManager;
import game.util.HighScoreManager;

import javax.swing.*;
import java.awt.*;

public class BestTimesScreen extends JPanel {

    /**
     * Displays the best completion times in a scrollable list with a consistent design.
     * <p>
     * This screen features:
     * <ul>
     *   <li>A royal purple background</li>
     *   <li>Bright yellow text for high contrast</li>
     *   <li>A scrollable list of best times</li>
     *   <li>A "Back to Menu" button</li>
     * </ul>
     * </p>
     */
    public BestTimesScreen(JFrame frame) {
        setLayout(new BorderLayout());
        setBackground(new Color(102, 51, 153)); // Royal Purple

        // --- Title Section ---
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(new Color(102, 51, 153));
        titlePanel.add(Box.createVerticalStrut(30));

        JLabel title = new JLabel("Best Completion Times", SwingConstants.CENTER);
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

        long[] bestTimes = HighScoreManager.getHighScores();

        if (bestTimes == null || bestTimes.length == 0 || bestTimes[0] == Long.MAX_VALUE) {
            JLabel noScore = new JLabel("No high scores yet.", SwingConstants.CENTER);
            noScore.setFont(FontManager.getFont("default", 16f));
            noScore.setForeground(new Color(255, 222, 0));
            noScore.setAlignmentX(Component.CENTER_ALIGNMENT);
            timePanel.add(noScore, BorderLayout.CENTER);
        } else {
            // Create a scrollable list to display all times
            JList<String> timeList = new JList<>();
            DefaultListModel<String> listModel = new DefaultListModel<>();

            for (int i = 0; i < bestTimes.length; i++) {
                long time = bestTimes[i];
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
                    label.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
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

        JButton backButton = new JButton("Back to Menu");
        backButton.setFont(FontManager.getFont("default", Font.BOLD, 18f));
        backButton.setForeground(new Color(255, 222, 0));
        backButton.setBackground(new Color(60, 0, 60));
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(true);
        backButton.setOpaque(true);
        backButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        backButton.addActionListener(e -> {
            SoundManager.playButtonSound();
            frame.dispose();
            new StartupScreen();
        });

        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private String formatTime(long ms) {
        long minutes = ms / 60000;
        long seconds = (ms % 60000) / 1000;
        long millis = ms % 1000;
        return String.format("%02d:%02d.%03d", minutes, seconds, millis);
    }
}