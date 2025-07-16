package game.core;

import city.cs.engine.*;
import game.entities.Player;
import game.util.FontManager;

import javax.swing.*;
import java.awt.*;

/**
 * The {@code GameView} class represents the main visual rendering component of the game.
 * It extends {@link UserView} to display the game world and overlays UI elements such as score and health.
 * It also integrates a {@link PlayerController} for handling player input.
 * <p>
 * This class serves as the primary visual interface between the player and the game world,
 * responsible for:
 * <ul>
 *   <li>Rendering the appropriate background based on the current level</li>
 *   <li>Displaying real-time game statistics (score, health, elapsed time)</li>
 *   <li>Capturing and processing player input via keyboard controls</li>
 *   <li>Updating visual elements in response to game state changes</li>
 * </ul>
 * </p>
 * <p>
 * The view uses a layered rendering approach:
 * <ol>
 *   <li>Background layer: Level-specific background image</li>
 *   <li>World layer: Game entities (player, platforms, collectibles, enemies)</li>
 *   <li>Foreground layer: HUD elements (score, health bar, timer)</li>
 * </ol>
 * </p>
 */
public class GameView extends UserView {

    /**
     * Reference to the player character in the world.
     * Used to pass input commands from the controller.
     */
    private final Player player;

    /**
     * Reference to the custom game world instance.
     * Used to access game state information like score, health, and level.
     */
    private final GameWorld gameWorld;

    /**
     * Controller for processing keyboard input to control the player.
     * Handles key presses and releases to move the player character.
     */
    private final PlayerController playerController;

    /**
     * Reference to the Game instance for accessing timer data.
     * Used to display the current elapsed time and best time.
     */
    private final Game game;

    /**
     * Constructs a new {@code GameView} to render the specified game world.
     * <p>
     * This constructor:
     * <ol>
     *   <li>Initializes the view with the specified dimensions</li>
     *   <li>Retrieves the player character from the world</li>
     *   <li>Creates a controller for handling player input</li>
     *   <li>Sets up keyboard focus to capture player commands</li>
     * </ol>
     * </p>
     *
     * @param world  The {@link GameWorld} instance to be displayed.
     * @param width  The width of the view in pixels.
     * @param height The height of the view in pixels.
     * @param game   The main {@code Game} instance, used for displaying live timer HUD.
     */
    public GameView(GameWorld world, int width, int height, Game game) {
        super(world, width, height);
        this.player = (Player) world.getPlayer();
        this.gameWorld = world;
        this.game = game;

        // Set up player controller and input handling
        playerController = new PlayerController(player);
        addKeyListener(playerController);

        setFocusable(true);
        requestFocusInWindow();
    }

    /**
     * Updates the game logic associated with the view, primarily delegated to the player controller.
     * <p>
     * This method is called on each frame update by the game loop and ensures
     * that player input is processed and applied to the player character.
     * </p>
     */
    public void update() {
        playerController.update();
    }

    /**
     * Resets the player's jump ability, typically called when landing on a platform.
     * <p>
     * This method delegates to the player controller to reset the jump state,
     * allowing the player to jump again after landing.
     * </p>
     */
    public void resetJump() {
        playerController.resetJump();
    }

    /**
     * Paints the game background depending on the current level.
     * <p>
     * This method selects and renders a different background image based on
     * the current level number, providing visual variety as the player progresses.
     * The background image is scaled to fill the entire view.
     * </p>
     *
     * @param g The {@link Graphics2D} context used for rendering.
     */
    @Override
    protected void paintBackground(Graphics2D g) {
        String bgPath;

        // Select background image based on level
        if (gameWorld.getLevel() == 1) {
            bgPath = "data/bg001.png";
        } else if (gameWorld.getLevel() == 2) {
            bgPath = "data/bg002.png";
        } else if (gameWorld.getLevel() == 3) {
            bgPath = "data/bg003.png";
        } else {
            bgPath = "data/bg004.png";
        }

        // Load and draw the selected background
        Image background = new ImageIcon(bgPath).getImage();
        g.drawImage(background, 0, 0, this.getWidth(), this.getHeight(), null);
    }

    /**
     * Paints foreground UI elements such as the score and health bar, and adds a live timer HUD.
     * <p>
     * This method renders the game's Heads-Up Display (HUD) with:
     * <ul>
     *   <li>A semi-transparent white background panel</li>
     *   <li>Current score display</li>
     *   <li>Health text and a color-coded health bar (green/red)</li>
     *   <li>Current elapsed time (excluding pauses)</li>
     *   <li>Best completion time across all games</li>
     * </ul>
     * </p>
     *
     * @param g The {@link Graphics2D} context used for rendering.
     */
    @Override
    protected void paintForeground(Graphics2D g) {
        // Draw a semi-transparent white background for the HUD
        g.setColor(new Color(255, 255, 255, 200)); // Semi-transparent white
        g.fillRect(10, 5, 245, 100); // Adjust the dimensions as needed

        // Set HUD font and color
        Font hudFont = FontManager.getFont("default", Font.PLAIN, 12f);
        g.setFont(hudFont);
        g.setColor(Color.BLACK);

        // Draw score on the top-left
        g.drawString("Score: " + gameWorld.getScore(), 20, 30);

        // Draw health text and health bar on the top-right
        g.drawString("Health: " + gameWorld.getHealth(), 140, 30);
        int barWidth = 100;
        int barHeight = 10;
        int health = gameWorld.getHealth();
        g.setColor(Color.RED);
        g.fillRect(144, 35, barWidth, barHeight);
        g.setColor(Color.GREEN);
        g.fillRect(144, 35, (int) ((health / 5.0) * barWidth), barHeight);

        // --- Live Timer HUD ---
        // Retrieve current net elapsed time (excluding pauses) and best time from the Game instance
        long currentTime = game.getCurrentNetElapsedTime();
        long[] bestTimes = game.getBestTimes();
        long best = Long.MAX_VALUE;
        for (long t : bestTimes) {
            if (t < best) {
                best = t;
            }
        }
        String currentTimeStr = formatTime(currentTime);
        String bestTimeStr = (best == Long.MAX_VALUE) ? "N/A" : formatTime(best);

        Font timerFont = FontManager.getFont("default", Font.BOLD, 10f);
        g.setFont(timerFont);
        g.setColor(Color.BLUE);
        g.drawString("Time: " + currentTimeStr, 20, 80);
        g.drawString("Best: " + bestTimeStr, 20, 100);
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
     * If the input is zero or negative, "00:00.000" is returned.
     * </p>
     *
     * @param ms Time in milliseconds.
     * @return A formatted string in the pattern "MM:SS.mmm".
     */
    private String formatTime(long ms) {
        if (ms <= 0) return "00:00.000";
        long minutes = ms / 60000;
        long seconds = (ms % 60000) / 1000;
        long millis = ms % 1000;
        return String.format("%02d:%02d.%03d", minutes, seconds, millis);
    }
}