package game.core;

import city.cs.engine.Body;
import game.audio.*;
import game.entities.Collectible;
import game.entities.Enemy;
import game.entities.FlyingEnemy;
import game.ui.*;
import game.util.*;
import org.jbox2d.common.Vec2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

/**
 * The main class that manages the game lifecycle, including world creation,
 * view rendering, input handling, pause/menu interaction, and game loop timing.
 * <p>
 * This class serves as the central controller for the platformer game, handling:
 * <ul>
 *   <li>Game initialization and setup</li>
 *   <li>Level progression and management</li>
 *   <li>UI components and overlays</li>
 *   <li>Game state (pause, resume, save, load)</li>
 *   <li>Timing and high score tracking</li>
 * </ul>
 * </p>
 */
public class   Game {
    // Core Game Components

    /**
     * The game world that handles physics and contains all entities.
     * Each level creates a new {@code GameWorld} instance.
     */
    private GameWorld world;

    /**
     * The view component responsible for rendering the {@code GameWorld} to the screen.
     */
    private GameView view;

    /**
     * The main application window (a {@code JFrame}) that displays the game.
     */
    private JFrame frame;

    /**
     * A {@code Timer} that drives the main game loop, typically running
     * at ~60 frames per second.
     */
    private Timer gameLoopTimer;

    /**
     * An integer representing the current level. Levels typically start at 1
     * and increment as the player progresses.
     */
    private int level;

    /**
     * A {@code JLayeredPane} that allows multiple overlapping panels or overlays.
     * Used for pause menus, controls overlays, etc.
     */
    private JLayeredPane layeredPane;

    // Timing and High Score Fields

    /**
     * The timestamp (in milliseconds) at which the current level run began.
     */
    private long startTime;

    /**
     * The timestamp (in milliseconds) at which the current level run ended.
     */
    private long endTime;

    /**
     * The net elapsed time (in milliseconds) for the current run, excluding paused intervals.
     */
    private long elapsedTime;

    /**
     * A static array storing the best times across all levels. Each index
     * corresponds to a different level, and times are in milliseconds.
     */
    private static long[] bestTimes = { Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE };

    /**
     * The total accumulated time (in milliseconds) spent in a paused state.
     * This is subtracted from the net elapsed time.
     */
    private long totalPausedTime = 0;

    /**
     * The timestamp (in milliseconds) of when the current pause began. If {@code -1},
     * the game is not currently paused.
     */
    private long pauseStart = -1;

    // Constructor and Initialization

    /**
     * Constructs a {@code Game} instance and initializes the world, view, controls,
     * input handling, game loop, and audio.
     *
     * @param level The starting level of the game. Typically 1 for a new game,
     *             but can be higher for level progression.
     */
    public Game(int level) {
        this.level = level;

        // Load best times from persistence
        bestTimes = HighScoreManager.getHighScores();

        // Initialize game world and pass 'this' so the world can call gameCompleted() on final level
        world = new GameWorld(level, this);
        view = new GameView(world, 500, 500, this);
        world.setView(view);

        // Set up the main game window using a layered pane for overlays
        frame = new JFrame("Platformer Game");
        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(500, 500));
        view.setBounds(0, 0, 500, 500);
        layeredPane.add(view, JLayeredPane.DEFAULT_LAYER);

        // Add the drop-up control panel to the bottom-left corner
        DropUpControlPanel dropUpControl = new DropUpControlPanel(this);
        dropUpControl.setBounds(10, 500 - 50, 40, 40);  // Adjust position and size as needed
        layeredPane.add(dropUpControl, JLayeredPane.PALETTE_LAYER);

        // Add the layered pane to the frame
        frame.setLayout(new BorderLayout());
        frame.add(layeredPane, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveGame(); // Auto-save when closing the game
            }
        });

        // Ensure view receives keyboard input
        view.requestFocusInWindow();

        // Additional key listener for pause/resume via 'P' and for menu via 'M'
        view.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_P) {
                    if (gameLoopTimer != null && gameLoopTimer.isRunning()) {
                        pauseGame();
                    } else {
                        resumeGame();
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_M) {
                    pauseGame();
                    showPauseOverlay();
                }
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    int keyCode = e.getKeyCode();
                    if (keyCode == KeyEvent.VK_S && e.isControlDown()) {
                        saveGame();
                    } else if (keyCode == KeyEvent.VK_L && e.isControlDown()) {
                        loadGame();
                    }
                }
            }
        });

        // Start world physics simulation
        world.start();

        // Initialize timer only if starting from Level 1
        if (level == 1) {
            startTime = System.currentTimeMillis();
            totalPausedTime = 0;
        } else {
            // For higher levels, ensure startTime is set from the previous level's state
            if (startTime == 0) {
                startTime = System.currentTimeMillis() - totalPausedTime;
            }
        }

        // Start background music
        SoundManager.getInstance().loopBackgroundMusic();

        // Start the game loop timer (approx. 60 FPS = 16ms/frame)
        gameLoopTimer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.update();
                world.checkPlayerOutOfBounds();
            }
        });
        gameLoopTimer.start();
    }

    // Level Management Methods

    /**
     * Progresses to the next level by creating a new {@code Game} instance.
     * <p>
     * The new instance preserves timer state (start time, paused time) so the overall
     * run timer continues seamlessly.
     * </p>
     */
    public void progressToNextLevel() {
        // Stop and reset game over sound before proceeding
        SoundManager.getInstance().stopGameOverSound();
        SoundManager.getInstance().resetGameOverSound();

        // Obtain how much net time has elapsed so far
        long currentElapsed = getCurrentNetElapsedTime();

        // Close the current game window
        frame.dispose();

        // Create the new level game instance, preserving the elapsed time
        Game nextLevelGame = new Game(level + 1);
        // Adjust the start time in the next level so it appears no time has passed
        nextLevelGame.startTime = System.currentTimeMillis() - currentElapsed;
        nextLevelGame.totalPausedTime = this.totalPausedTime;
    }

    /**
     * Restarts the current level while preserving the timer state.
     */
    public void restartLevel() {
        SoundManager.getInstance().resetGameOverSound();
        // Calculate the elapsed time up to this point
        long currentElapsed = getCurrentNetElapsedTime();

        // Dispose the current frame
        frame.dispose();

        // Create a new Game instance for the current level
        Game restartGame = new Game(level);

        // Set the start time for the new game instance to maintain timer continuity
        restartGame.startTime = System.currentTimeMillis() - currentElapsed;
        restartGame.totalPausedTime = this.totalPausedTime;
    }

    /**
     * Called when the final level is completed. Stops the timer, calculates the net
     * active time (excluding pauses), updates the best times, and displays the
     * "Game Complete" screen with the top three times.
     */
    public void gameCompleted() {
        // If the game is paused, resume before finalizing times
        if (pauseStart >= 0) {
            resumeGame();
        }

        // Mark the end of the run
        endTime = System.currentTimeMillis();
        long rawElapsed = endTime - startTime;
        // Subtract total paused time to get actual gameplay duration
        elapsedTime = rawElapsed - totalPausedTime;

        // Update high scores and store the result
        bestTimes = HighScoreManager.updateHighScores(elapsedTime);

        // Clear the frame and display the completion screen
        frame.getContentPane().removeAll();
        frame.add(new GameCompleteScreen(frame, bestTimes));
        frame.revalidate();
        frame.repaint();
    }

    // UI Overlay Methods

    /**
     * Displays a semi-transparent pause overlay on top of the game.
     */
    public void showPauseOverlay() {
        PauseOverlayPanel overlay = new PauseOverlayPanel(this);
        // Overlay occupies the entire 500x500 layer
        overlay.setBounds(0, 0, 500, 500);
        layeredPane.add(overlay, JLayeredPane.MODAL_LAYER);
        layeredPane.revalidate();
        layeredPane.repaint();
    }

    /**
     * Removes the pause overlay from the layered pane, returning the user to gameplay.
     */
    public void hidePauseOverlay() {
        for (Component comp : layeredPane.getComponentsInLayer(JLayeredPane.MODAL_LAYER)) {
            if (comp instanceof PauseOverlayPanel) {
                layeredPane.remove(comp);
            }
        }
        layeredPane.revalidate();
        layeredPane.repaint();
    }

    /**
     * Displays the controls overlay panel on top of the game (MODAL_LAYER).
     */
    public void showControlsPanel() {
        ControlsPanel controlsPanel = new ControlsPanel(this);
        controlsPanel.setBounds(0, 0, 500, 500);
        layeredPane.add(controlsPanel, JLayeredPane.MODAL_LAYER);
        layeredPane.revalidate();
        layeredPane.repaint();
    }

    /**
     * Removes the controls overlay panel from the layered pane.
     */
    public void hideControlsPanel() {
        for (Component comp : layeredPane.getComponentsInLayer(JLayeredPane.MODAL_LAYER)) {
            if (comp instanceof ControlsPanel) {
                layeredPane.remove(comp);
            }
        }
        layeredPane.revalidate();
        layeredPane.repaint();
    }

    // Game State Management Methods

    /**
     * Pauses the game by stopping the main loop, pausing physics,
     * and halting background music. Records the pause start time
     * for calculating total paused intervals.
     */
    public void pauseGame() {
        // Stop the timer if it's running
        if (gameLoopTimer != null && gameLoopTimer.isRunning()) {
            gameLoopTimer.stop();
        }
        // Record the start time of this pause
        if (pauseStart < 0) {
            pauseStart = System.currentTimeMillis();
        }
        // Pause physics updates
        world.pauseWorld();
        // Stop background music
        SoundManager.getInstance().stopBackgroundMusic();
    }

    /**
     * Resumes the game by restarting the game loop, resuming physics updates,
     * and looping background music. Subtracts the paused interval from the total
     * paused time to maintain correct net elapsed time.
     */
    public void resumeGame() {
        // Calculate how long we've been paused, if we were paused
        if (pauseStart >= 0) {
            long now = System.currentTimeMillis();
            totalPausedTime += (now - pauseStart);
            pauseStart = -1;
        }
        // Restart the main game loop if it was stopped
        if (gameLoopTimer != null && !gameLoopTimer.isRunning()) {
            gameLoopTimer.start();
        }
        // Resume physics updates
        world.resumeWorld();
        // Resume background music
        SoundManager.getInstance().loopBackgroundMusic();

        // Request focus again so the view can receive keyboard input
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                view.requestFocusInWindow();
            }
        });
    }

    /**
     * Calculates and returns the current net elapsed time, excluding paused intervals.
     * <p>
     * This is done by subtracting {@code totalPausedTime} and any ongoing pause from
     * the total time since {@code startTime}.
     * </p>
     *
     * @return The net elapsed time in milliseconds (excluding pauses).
     */
    public long getCurrentNetElapsedTime() {
        long now = System.currentTimeMillis();
        long rawElapsed = now - startTime;
        long additionalPause = (pauseStart >= 0) ? (now - pauseStart) : 0;
        long netElapsed = rawElapsed - totalPausedTime - additionalPause;
        return netElapsed;
    }

    // Save/Load Game Methods

    /**
     * Saves the current game state to a file using Java serialization.
     * <p>
     * The saved state includes level, health, score, elapsed time, and player position.
     * </p>
     */
    public void saveGame() {
        GameState gameState = new GameState(
                level,
                this.world.getHealth(),
                this.world.getScore(),
                this.getCurrentNetElapsedTime(),
                this.world.getPlayer().getPosition().x,
                this.world.getPlayer().getPosition().y
        );

        // Iterate through all dynamic bodies to find enemies and collectibles
        for (Body body : world.getDynamicBodies()) {
            if (body instanceof Enemy && !((Enemy) body).isDestroyed()) {
                if (body instanceof FlyingEnemy) {
                    gameState.flyingEnemyPositions.add(body.getPosition());
                } else {
                    gameState.enemyPositions.add(body.getPosition());
                }
            }
            if (body instanceof Collectible && !((Collectible) body).isDestroyed()) {
                gameState.collectiblePositions.add(body.getPosition());
            }
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("data/savegame.dat"))) {
            oos.writeObject(gameState);
            System.out.println("Game saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving game: " + e.getMessage());
        }
    }

    /**
     * Loads the game state from a file using Java serialization.
     * <p>
     * Restores level, health, score, elapsed time, and player position from the saved state.
     * </p>
     */
    public void loadGame() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data/savegame.dat"))) {
            GameState gameState = (GameState) ois.readObject();

            // Close the current game window
            frame.dispose();

            // Create a new Game instance with the loaded state
            Game loadedGame = new Game(gameState.level);
            loadedGame.getGameWorld().setHealth(gameState.health);
            loadedGame.getGameWorld().setScore(gameState.score);
            loadedGame.startTime = System.currentTimeMillis() - gameState.elapsedTime;
            loadedGame.getGameWorld().getPlayer().setPosition(new Vec2(gameState.playerX, gameState.playerY));

            // Clear existing enemies and collectibles before loading
            loadedGame.getGameWorld().clearEnemiesAndCollectibles();

            // Load enemies from the saved positions
            for (Vec2 position : gameState.enemyPositions) {
                new Enemy(loadedGame.getGameWorld(), position, 3f);
            }
            for (Vec2 position : gameState.flyingEnemyPositions) {
                new FlyingEnemy(loadedGame.getGameWorld(), position);
            }

            // Load collectibles from the saved positions
            for (Vec2 position : gameState.collectiblePositions) {
                new Collectible(loadedGame.getGameWorld(), position);
            }

            // Start background music
            SoundManager.getInstance().loopBackgroundMusic();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading game: " + e.getMessage());
        }
    }

    /**
     * Static method to load a game from a file without an existing game instance.
     * <p>
     * This is typically used when starting the game from a saved state rather than a new game.
     * </p>
     *
     * @return A new {@code Game} instance with the loaded state, or {@code null} if loading fails.
     */
    public static Game loadGameFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data/savegame.dat"))) {
            GameState gameState = (GameState) ois.readObject();

            // Create a new Game instance with the loaded state
            Game loadedGame = new Game(gameState.level);
            loadedGame.getGameWorld().setHealth(gameState.health);
            loadedGame.getGameWorld().setScore(gameState.score);
            loadedGame.startTime = System.currentTimeMillis() - gameState.elapsedTime;
            loadedGame.getGameWorld().getPlayer().setPosition(new Vec2(gameState.playerX, gameState.playerY));

            // Clear existing enemies and collectibles before loading
            loadedGame.getGameWorld().clearEnemiesAndCollectibles();

            // Load enemies from saved positions
            for (Vec2 position : gameState.enemyPositions) {
                new Enemy(loadedGame.getGameWorld(), position, 3f);
            }
            for (Vec2 position : gameState.flyingEnemyPositions) {
                new FlyingEnemy(loadedGame.getGameWorld(), position);
            }

            // Load collectibles from saved positions
            for (Vec2 position : gameState.collectiblePositions) {
                new Collectible(loadedGame.getGameWorld(), position);
            }

            // Set the loadedFromSave flag
            loadedGame.getGameWorld().setLoadedFromSave(true);

            // Start background music
            SoundManager.getInstance().loopBackgroundMusic();

            return loadedGame;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading game: " + e.getMessage());
            return null;
        }
    }

    // Getter Methods

    /**
     * Returns the game world instance that contains all game entities and physics.
     *
     * @return The current {@code GameWorld} instance.
     */
    public GameWorld getGameWorld() {
        return this.world;
    }

    /**
     * Returns the main application window.
     *
     * @return The {@code JFrame} instance representing the game window.
     */
    public JFrame getFrame() {
        return frame;
    }

    /**
     * Returns the array of best times (in milliseconds) across all levels.
     *
     * @return A {@code long[]} representing the best times for each level.
     */
    public long[] getBestTimes() {
        return bestTimes;
    }

    // Main Method

    /**
     * The entry point for launching the game.
     * <p>
     * Creates and displays the startup screen in the Swing event dispatch thread.
     * </p>
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new StartupScreen();
        });
    }
}