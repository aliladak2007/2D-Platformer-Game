package game.ui;

import game.util.FontManager;
import game.core.Game;
import game.audio.SoundManager;

import javax.swing.*;
import java.awt.*;

/**
 * A semi-transparent overlay panel used for pausing the game.
 * <p>
 * This panel replicates the functionality of the previous PauseMenuDialog
 * by providing volume controls (sliders and mute checkboxes), a button to view
 * controls, and a button to resume the game. Unlike a separate dialog, this is
 * displayed as an overlay on top of the game using a {@link JLayeredPane}.
 * </p>
 * <p>
 * The PauseOverlayPanel appears when:
 * <ul>
 *   <li>The player presses the 'P' key during gameplay</li>
 *   <li>The player selects "Menu" from the drop-up control panel</li>
 *   <li>The game is explicitly paused through code via {@link Game#pauseGame()}</li>
 * </ul>
 * </p>
 * <p>
 * This overlay provides the following functionality:
 * <ul>
 *   <li>Background music volume control with mute option</li>
 *   <li>Sound effects (SFX) volume control with mute option</li>
 *   <li>Global mute option for all game audio</li>
 *   <li>Access to game controls information</li>
 *   <li>Game save and load functionality</li>
 *   <li>Option to resume gameplay</li>
 * </ul>
 * </p>
 * <p>
 * When displayed, the panel uses a semi-transparent black background to darken
 * the game screen while keeping it partially visible, creating a non-intrusive
 * pause experience.
 * </p>
 */
public class PauseOverlayPanel extends JPanel {

    /**
     * Constructs a {@code PauseOverlayPanel} with semi-transparent background
     * and various sound control components and buttons.
     * <p>
     * The panel is organized into several sections:
     * <ol>
     *   <li><b>Header</b>: "Game Menu" title</li>
     *   <li><b>Audio Controls</b>:
     *     <ul>
     *       <li>Background music volume slider (0-200%) and mute checkbox</li>
     *       <li>SFX volume slider (0-200%) and mute checkbox</li>
     *       <li>Global "Mute All Sounds" checkbox</li>
     *     </ul>
     *   </li>
     *   <li><b>Game Controls</b>: Button to view game controls</li>
     *   <li><b>Game Flow</b>: Button to resume gameplay</li>
     *   <li><b>Game State</b>: Buttons to save and load game progress</li>
     * </ol>
     * </p>
     * <p>
     * All audio controls interact directly with the {@link SoundManager} singleton
     * to adjust volume levels in real-time. The panel uses a vertical {@link BoxLayout}
     * with appropriate spacing between components for visual clarity.
     * </p>
     *
     * @param game The main {@link Game} instance used for hiding this overlay,
     *             showing the controls panel, and resuming the game. Also provides
     *             access to save and load functionality.
     */
    public PauseOverlayPanel(Game game) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(0, 0, 0, 200)); // Transparent black

        // --- Header ---
        JLabel menuLabel = new JLabel("Game Menu", SwingConstants.CENTER);
        menuLabel.setFont(FontManager.getFont("default", Font.BOLD, 24f));
        menuLabel.setForeground(new Color(255, 222, 0));
        menuLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(Box.createVerticalStrut(20));
        add(menuLabel);
        add(Box.createVerticalStrut(20));

        // --- Background Volume ---
        JLabel bgVolumeLabel = new JLabel("Background Music Volume", SwingConstants.CENTER);
        bgVolumeLabel.setFont(FontManager.getFont("default", Font.PLAIN, 16f));
        bgVolumeLabel.setForeground(Color.WHITE);
        bgVolumeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(bgVolumeLabel);

        JSlider bgVolumeSlider = new JSlider(0, 200, 100);
        bgVolumeSlider.addChangeListener(e -> {
            double volume = bgVolumeSlider.getValue() / 100.0;
            SoundManager.getInstance().setBackgroundVolume(volume);
        });
        add(Box.createVerticalStrut(10));
        add(bgVolumeSlider);
        add(Box.createVerticalStrut(20));

        JCheckBox muteBgCheckbox = new JCheckBox("Mute Background Music");
        muteBgCheckbox.setFont(FontManager.getFont("default", Font.PLAIN, 14f));
        muteBgCheckbox.setForeground(Color.WHITE);
        muteBgCheckbox.setOpaque(false);
        muteBgCheckbox.setAlignmentX(Component.CENTER_ALIGNMENT);
        muteBgCheckbox.addActionListener(e -> {
            double volume = muteBgCheckbox.isSelected() ? 0.0 : bgVolumeSlider.getValue() / 100.0;
            SoundManager.getInstance().setBackgroundVolume(volume);
        });
        add(muteBgCheckbox);
        add(Box.createVerticalStrut(20));

        // --- SFX Volume ---
        JLabel sfxVolumeLabel = new JLabel("SFX Sounds Volume", SwingConstants.CENTER);
        sfxVolumeLabel.setFont(FontManager.getFont("default", Font.PLAIN, 16f));
        sfxVolumeLabel.setForeground(Color.WHITE);
        sfxVolumeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(sfxVolumeLabel);

        JSlider sfxVolumeSlider = new JSlider(0, 200, 100);
        sfxVolumeSlider.addChangeListener(e -> {
            double volume = sfxVolumeSlider.getValue() / 100.0;
            SoundManager.getInstance().setSFXVolume(volume);
        });
        add(Box.createVerticalStrut(10));
        add(sfxVolumeSlider);
        add(Box.createVerticalStrut(20));

        JCheckBox muteSFXCheckbox = new JCheckBox("Mute SFX Sounds");
        muteSFXCheckbox.setFont(FontManager.getFont("default", Font.PLAIN, 14f));
        muteSFXCheckbox.setForeground(Color.WHITE);
        muteSFXCheckbox.setOpaque(false);
        muteSFXCheckbox.setAlignmentX(Component.CENTER_ALIGNMENT);
        muteSFXCheckbox.addActionListener(e -> {
            double volume = muteSFXCheckbox.isSelected() ? 0.0 : sfxVolumeSlider.getValue() / 100.0;
            SoundManager.getInstance().setSFXVolume(volume);
        });
        add(muteSFXCheckbox);
        add(Box.createVerticalStrut(20));

        // --- Mute All ---
        JCheckBox muteAllCheckbox = new JCheckBox("Mute All Sounds");
        muteAllCheckbox.setFont(FontManager.getFont("default", Font.PLAIN, 14f));
        muteAllCheckbox.setForeground(Color.WHITE);
        muteAllCheckbox.setOpaque(false);
        muteAllCheckbox.setAlignmentX(Component.CENTER_ALIGNMENT);
        muteAllCheckbox.addActionListener(e -> {
            boolean mute = muteAllCheckbox.isSelected();
            muteBgCheckbox.setSelected(mute);
            muteSFXCheckbox.setSelected(mute);
            double bgVol = mute ? 0.0 : bgVolumeSlider.getValue() / 100.0;
            double SFXVol = mute ? 0.0 : sfxVolumeSlider.getValue() / 100.0;
            SoundManager.getInstance().setBackgroundVolume(bgVol);
            SoundManager.getInstance().setSFXVolume(SFXVol);
        });
        add(muteAllCheckbox);
        add(Box.createVerticalStrut(20));

        // --- View Controls Button ---
        JButton controlsButton = new JButton("View Controls");
        controlsButton.setFont(FontManager.getFont("default", Font.BOLD, 16f));
        controlsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        controlsButton.addActionListener(e -> {
            SoundManager.playButtonSound();
            game.hidePauseOverlay();
            game.showControlsPanel();
        });
        add(controlsButton);
        add(Box.createVerticalStrut(20));

        // --- Resume Button ---
        JButton resumeButton = new JButton("Resume");
        resumeButton.setFont(FontManager.getFont("default", Font.BOLD, 16f));
        resumeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        resumeButton.addActionListener(e -> {
            SoundManager.playButtonSound();
            game.hidePauseOverlay();
            game.resumeGame();
        });
        add(resumeButton);

        // Push contents up
        add(Box.createVerticalGlue());

        // --- Save Game Button ---
        JButton saveButton = new JButton("Save Game");
        saveButton.setFont(FontManager.getFont("default", Font.BOLD, 16f));
        saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveButton.addActionListener(e -> {
            SoundManager.playButtonSound();
            SwingUtilities.invokeLater(() -> {
                game.saveGame();
            });
        });
        add(saveButton);
        add(Box.createVerticalStrut(20));

        // --- Load Game Button ---
        JButton loadButton = new JButton("Load Game");
        loadButton.setFont(FontManager.getFont("default", Font.BOLD, 16f));
        loadButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadButton.addActionListener(e -> {
            SoundManager.playButtonSound();
            SwingUtilities.invokeLater(() -> {
                game.loadGame();
                game.hidePauseOverlay();
            });
        });
        add(loadButton);
        add(Box.createVerticalStrut(20));
    }


    /**
     * Overrides {@link JPanel#paintComponent(Graphics)} to draw a semi-transparent
     * black background over the entire panel, enhancing the overlay effect.
     * <p>
     * This method is called automatically by Swing's painting system whenever
     * the panel needs to be redrawn. It first calls the superclass implementation
     * to handle basic painting, then adds a semi-transparent black
     * rectangle covering the entire panel area.
     * </p>
     * <p>
     * The transparency level (alpha value of 150 out of 255) creates a darkening
     * effect that allows the game to remain partially visible in the background
     * while emphasizing the pause menu in the foreground.
     * </p>
     *
     * @param g The {@link Graphics} context for drawing, provided by the Swing painting system.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}