package game.ui;

import game.audio.SoundManager;
import game.util.FontManager;
import game.core.Game;

import javax.swing.*;
import java.awt.*;

/**
 * A panel that displays a drop‑up button in the bottom‑left corner.
 * When clicked, it shows a popup menu with options "Menu" and "Controls."
 * <p>
 * This panel provides a minimalist UI element that gives players access to
 * game controls and menu options without cluttering the main game interface.
 * The panel is designed to be unobtrusive during gameplay but easily accessible
 * when needed.
 * </p>
 * <p>
 * When the user clicks the button (displayed as a "hamburger" icon ≡), a popup
 * menu appears above the button with two options:
 * <ul>
 *   <li><b>Menu</b> - Pauses the game and displays the pause overlay</li>
 *   <li><b>Controls</b> - Pauses the game and displays the controls panel</li>
 * </ul>
 * </p>
 */
public class DropUpControlPanel extends JPanel {
    /**
     * The button that, when clicked, triggers the display of the popup menu.
     * <p>
     * Represented visually by a "hamburger" (≡) icon.
     * </p>
     */
    private JButton dropUpButton;

    /**
     * The popup menu that contains the "Menu" and "Controls" options.
     * It appears above the drop‑up button when activated.
     */
    private JPopupMenu dropUpMenu;

    /**
     * Constructs a new {@code DropUpControlPanel}, providing a bottom‑left
     * corner control with a popup menu.
     * <p>
     * The panel is configured with the following characteristics:
     * <ul>
     *   <li>Transparent background (setOpaque(false))</li>
     *   <li>BorderLayout for component arrangement</li>
     *   <li>A centered button with the hamburger icon (≡)</li>
     *   <li>A popup menu with "Menu" and "Controls" options</li>
     *   <li>Event handlers that trigger appropriate game actions</li>
     * </ul>
     * </p>
     *
     * @param game The main {@code Game} instance used to trigger pause and controls actions.
     *             Must not be {@code null} if the panel should invoke game-specific behavior.
     */
    public DropUpControlPanel(Game game) {
        setLayout(new BorderLayout());
        setOpaque(false);

        // Drop-up hamburger button
        dropUpButton = new JButton("≡");
        dropUpButton.setFocusable(false);
        dropUpButton.setFont(new Font("Helvetica", Font.BOLD, 24));
        add(dropUpButton, BorderLayout.CENTER);

        // Popup menu with "Menu" and "Controls"
        dropUpMenu = new JPopupMenu();
        JMenuItem menuItem = new JMenuItem("Menu");
        menuItem.setFont(FontManager.getFont("emulogic", Font.PLAIN, 14f));
        JMenuItem controlsItem = new JMenuItem("Controls");
        controlsItem.setFont(FontManager.getFont("emulogic", Font.PLAIN, 14f));

        dropUpMenu.add(menuItem);
        dropUpMenu.add(controlsItem);

        // Configure the button to show the popup menu when clicked
        // The menu appears above the button (y-offset is negative)
        dropUpButton.addActionListener(e -> {
            SoundManager.playButtonSound();
            dropUpMenu.show(dropUpButton, 0, -dropUpMenu.getPreferredSize().height);
        });


        // Configure the "Menu" option to pause the game and show the pause overlay
        menuItem.addActionListener(e -> {
            SoundManager.playButtonSound();
            game.pauseGame();
            game.showPauseOverlay();
        });

        // Configure the "Controls" option to pause the game and show the controls panel
        controlsItem.addActionListener(e -> {
            SoundManager.playButtonSound();
            game.pauseGame();
            game.showControlsPanel();
        });
    }
}