package game.ui;

import game.audio.SoundManager;
import game.util.FontManager;
import game.core.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The {@code ControlsPanel} class displays the game controls as an attractive, modern overlay panel.
 * It uses a semi-transparent dark background, modern fonts, and a scrollable text area for instructions.
 * A "Back" button allows the user to close the overlay.
 */
public class ControlsPanel extends JPanel {
    /**
     * Constructs a {@code ControlsPanel}.
     * <p>
     * This panel features:
     * <ul>
     *   <li>A header label with the title "Game Controls"</li>
     *   <li>A scrollable text area describing the controls</li>
     *   <li>A "Back" button that either hides this panel or disposes its parent window</li>
     * </ul>
     * </p>
     *
     * @param game The main {@code Game} instance. If {@code null}, the "Back" button will simply
     *             dispose its parent window instead of calling {@code hideControlsPanel()}.
     */
    public ControlsPanel(Game game) {
        setLayout(new BorderLayout());
        setBackground(new Color(0, 0, 0, 230)); // Semi-transparent dark background

        // Header label
        JLabel headerLabel = new JLabel("GAME CONTROLS", SwingConstants.CENTER);
        headerLabel.setFont(FontManager.getFont("emulogic", Font.BOLD, 28f));
        headerLabel.setForeground(new Color(0xFFFF00)); // Pac-Man Yellow
        add(headerLabel, BorderLayout.NORTH);

        // Instructions
        JTextArea instructionsArea = new JTextArea(
                "Controls:\n\n" +
                        "- Arrow keys or WAD: Move the player\n" +
                        "- P: Pause/Resume the game\n" +
                        "- M: Open the menu (with sound options and controls)\n" +
                        "- Ctrl + S: Save the game\n" +
                        "- Ctrl + L: Load the game\n" +
                        "- Collect the coins to progress\n" +
                        "- Enjoy the game!"
        );
        instructionsArea.setFont(FontManager.getFont("emulogic", Font.PLAIN, 16f));
        instructionsArea.setForeground(Color.WHITE);
        instructionsArea.setBackground(new Color(0, 0, 0, 0));
        instructionsArea.setEditable(false);
        instructionsArea.setFocusable(false);
        instructionsArea.setCaretColor(new Color(0, 0, 0, 0));
        instructionsArea.setLineWrap(true);
        instructionsArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(instructionsArea);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        // Back button
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);

        JButton backButton = new JButton("BACK");
        backButton.setFont(FontManager.getFont("emulogic", Font.BOLD, 18f));
        backButton.setForeground(new Color(0x00FFFF)); // Inky Cyan
        backButton.setBackground(Color.BLACK);
        backButton.setFocusPainted(false);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(SoundManager::playButtonSound).start();
                if (game != null) {
                    game.hideControlsPanel();
                    game.resumeGame();
                } else {
                    Window w = SwingUtilities.getWindowAncestor(ControlsPanel.this);
                    if (w != null) {
                        w.dispose();
                    }
                }
            }
        });
    }
}
