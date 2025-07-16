package game.core;

import city.cs.engine.*;
import game.audio.SoundManager;
import game.entities.Collectible;
import game.entities.Enemy;
import game.entities.Obstacle;
import game.entities.Player;
import game.levels.*;
import game.ui.GameOverScreen;
import game.ui.LevelCompleteScreen;
import org.jbox2d.common.Vec2;
import javax.swing.*;
import java.util.List;

/**
 * Represents the game world, including the player, levels, and game mechanics.
 * <p>
 * This class extends {@link World} from the {@code city.cs.engine} package,
 * providing physics simulation and entity management. It manages the player,
 * collectibles, enemies, obstacles, and transitions between levels or game states.
 * </p>
 * <p>
 * The GameWorld is responsible for:
 * <ul>
 *   <li>Creating and managing the physical environment (ground, platforms)</li>
 *   <li>Loading level-specific configurations</li>
 *   <li>Tracking player score and health</li>
 *   <li>Handling level completion and game over conditions</li>
 *   <li>Managing game state transitions (pausing, resuming)</li>
 * </ul>
 * </p>
 */
public class GameWorld extends World {
    /**
     * The player character. This is a {@link DynamicBody} that the user can control.
     */
    private final DynamicBody player;

    /**
     * The current level number. Used to determine which level configuration to load.
     */
    private final int level;

    /**
     * The player's current score. This increments when a collectible is gathered.
     */
    private int score;

    /**
     * The player's remaining health. Reduced upon collisions with enemies or obstacles.
     */
    private int health;

    /**
     * A reference to the current {@link GameView} for rendering this game world.
     */
    private GameView view;

    /**
     * A reference to the main {@link Game} instance. Used for callbacks such as {@link Game#gameCompleted()}.
     */
    private final Game game;

    /**
     * A boolean value used to determine if the {@link GameOverScreen} is currently in frame.
     * Used in the {@link GameWorld#showGameOverScreen()} method to prevent multiple game over screens.
     */
    private boolean isGameOverDisplayed = false;
    /**
     * A boolean value used to determine if the game currently in frame is one from the save files
     */
    private boolean loadedFromSave = false;



    /**
     * Initializes the game world by creating ground, level-specific objects, and the player.
     * <p>
     * Based on the {@code level} parameter, this constructor loads one of several level configurations.
     * It also initializes a collision handler and default score/health values.
     * </p>
     * <p>
     * The constructor performs the following setup:
     * <ol>
     *   <li>Creates the ground platform</li>
     *   <li>Loads the appropriate level configuration based on the level number</li>
     *   <li>Creates the player character</li>
     *   <li>Sets up collision detection</li>
     *   <li>Initializes score and health values</li>
     * </ol>
     * </p>
     *
     * @param level The level number to load (1-based).
     * @param game  A reference to the main {@link Game} object that manages overall game flow.
     */
    public GameWorld(int level, Game game) {
        this.level = level;
        this.game = game;

        // Create the ground shape (a 22x1 rectangle, since BoxShape's arguments are half-width and half-height)
        Shape groundShape = new BoxShape(11, 0.5f);
        StaticBody ground = new StaticBody(this, groundShape);
        ground.setPosition(new Vec2(0, -11.5f));
        // Attach an image to the ground (scaled to 1.0, no rotation, centered at (0,0))
        new AttachedImage(ground, new BodyImage("data/ground1.png", 1.0f), 1.0f, 0, new Vec2(0, 0));

        // Select and set up the level configuration based on the integer 'level'
        Level levelConfig;
        if (level == 1) {
            levelConfig = new Level1();
        } else if (level == 2) {
            levelConfig = new Level2();
        } else if (level == 3) {
            levelConfig = new Level3();
        } else if (level == 4) { // Added Level4
            levelConfig = new Level4();
        } else {
            levelConfig = new Level1(); // Default to Level1 if level is unknown
        }
        levelConfig.setupLevel(this);

        // Create the player character
        player = new Player(this);

        // Set up a collision handler for the player (detects collisions with enemies, collectibles, etc.)
        CollisionListener handler = new CollisionHandler(this);
        player.addCollisionListener(handler);

        // Initialize score (for collectibles) and health (for collisions/obstacles)
        score = 0;
        health = 5;
    }

    // Score and Health Management

    /**
     * Increments the score when a collectible is collected.
     * <p>
     * Usually called from {@code CollisionHandler} when the player collides with a {@link Collectible}.
     * </p>
     */
    public void incrementScore() {
        score++;
    }

    /**
     * Returns the player's current score.
     *
     * @return the current score as an integer.
     */
    public int getScore() {
        return score;
    }

    /**
     * Reduces the player's health by one. If health reaches zero or below, the Game Over screen is displayed.
     * <p>
     * Typically invoked when the player collides with an enemy (not stomped) or an obstacle.
     * </p>
     */
    public void reduceHealth() {
        health--;
        if (health <= 0) {
            showGameOverScreen();
        }
    }

    /**
     * Returns the player's current health.
     *
     * @return the current health as an integer.
     */
    public int getHealth() {
        return health;
    }

    /**
     * Returns the current level number.
     *
     * @return the current level (1-based index).
     */
    public int getLevel() {
        return level;
    }

    /**
     * Sets the player's health.
     *
     * @param health New health value.
     */
    public void setHealth(int health) {
        this.health = health;
    }

    /**
     * Sets the player's score.
     *
     * @param score New score value.
     */
    public void setScore(int score) {
        this.score = score;
    }

    // Game State Management

    /**
     * Displays the Game Over screen by removing all content from the frame and adding
     * a {@link GameOverScreen}.
     * <p>
     * This method:
     * <ol>
     *   <li>Checks if a Game Over screen is already displayed to prevent duplicates</li>
     *   <li>Plays the game over sound effect</li>
     *   <li>Stops background music</li>
     *   <li>Replaces the current view with the Game Over screen</li>
     * </ol>
     * </p>
     */
    public void showGameOverScreen() {
        if (!isGameOverDisplayed && getHealth() <= 0) {
            isGameOverDisplayed = true;
            SoundManager.getInstance().stopBackgroundMusic();
            SoundManager.getInstance().playGameOverSound();

            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(getView());
            if (frame != null) {
                frame.getContentPane().removeAll();
                frame.add(new GameOverScreen(frame, level, game));
                frame.revalidate();
                frame.repaint();
            }
        }
    }

    /**
     * Displays the Level Complete screen. If the current level is below 4,
     * shows {@link LevelCompleteScreen}, otherwise calls {@link Game#gameCompleted()}.
     * <p>
     * This method:
     * <ol>
     *   <li>Stops background music</li>
     *   <li>Resets game over sound to prevent unwanted playback</li>
     *   <li>For levels 1-3: Shows the Level Complete screen</li>
     *   <li>For level 4 (final level): Triggers the game completion sequence</li>
     * </ol>
     * </p>
     */
    public void showLevelCompleteScreen() {
        SoundManager.getInstance().stopBackgroundMusic();
        SoundManager.getInstance().stopGameOverSound();
        SoundManager.getInstance().resetGameOverSound();

        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(getView());
        if (frame != null) {
            frame.getContentPane().removeAll();
            if (level < 4) {
                SoundManager.getInstance().playLevelCompleteSound();
                frame.add(new LevelCompleteScreen(frame, level, game));
            } else {
                // For level 4, assume it's the last level and trigger the game completion sequence
                game.gameCompleted();
            }
            frame.revalidate();
            frame.repaint();
        }
    }

    /**
     * Checks if all collectibles in the level have been collected.
     * If yes, shows the Level Complete screen. Otherwise, prompts the player
     * to collect all coins first.
     * <p>
     * This method compares the current score against the total number of
     * collectibles in the level to determine if all have been collected.
     * </p>
     */
    public void checkLevelCompletion() {
        int totalCollectibles = getInitialCollectibleCount();
        if (score >= totalCollectibles) {
            showLevelCompleteScreen();
        } else {
            System.out.println("You need to collect all the coins first!");
        }
    }

    /**
     * Determines the number of collectibles at the start of the level by counting
     * all {@link Collectible} objects in the world.
     *
     * @return the total number of collectible items in the current level.
     */
    private int getInitialCollectibleCount() {
        int count = 0;
        for (Body body : getDynamicBodies()) {
            if (body instanceof Collectible) {
                count++;
            }
        }
        return count;
    }

    /**
     * Checks if the player has fallen off the screen (below y = -15).
     * If so, triggers the Game Over screen.
     * <p>
     * This method is typically called on each frame update to detect
     * if the player has fallen into a pit or off the level boundaries.
     * </p>
     */
    public void checkPlayerOutOfBounds() {
        if (player.getPosition().y < -15) {
            showGameOverScreen();
        }
    }

    // World State Management

    /**
     * Pauses the world by stopping the physics simulation and freezing enemies/obstacles.
     * <p>
     * This method is called when the game is paused, preventing further movement in the world.
     * It performs the following actions:
     * <ol>
     *   <li>Stops the physics simulation engine</li>
     *   <li>Halts all enemy movement</li>
     *   <li>Stops all obstacle movement</li>
     * </ol>
     * </p>
     */
    public void pauseWorld() {
        stop(); // stop the physics simulation
        // Stop all dynamic movements in enemies
        for (Body body : getDynamicBodies()) {
            if (body instanceof Enemy) {
                ((Enemy) body).stopMovement();
            }
        }
        // Stop all static movements in obstacles
        for (Body body : getStaticBodies()) {
            if (body instanceof Obstacle) {
                ((Obstacle) body).stopMovement();
            }
        }
    }

    /**
     * Resumes the world by restarting the physics simulation and resuming enemies/obstacles movement.
     * <p>
     * This method is called when the game is unpaused, allowing movement to continue.
     * It performs the following actions:
     * <ol>
     *   <li>Restarts the physics simulation engine</li>
     *   <li>Resumes all enemy movement</li>
     *   <li>Resumes all obstacle movement</li>
     * </ol>
     * </p>
     */
    public void resumeWorld() {
        start(); // restart the physics simulation
        // Resume movement in enemies and obstacles
        for (Body body : getDynamicBodies()) {
            if (body instanceof Enemy) {
                ((Enemy) body).resumeMovement();
            }
            if (body instanceof Obstacle) {
                ((Obstacle) body).resumeMovement();
            }
        }
    }

    // View Management

    /**
     * Sets the {@link GameView} associated with this world.
     * <p>
     * The GameView is responsible for rendering the world and its entities.
     * </p>
     *
     * @param view the {@code GameView} to be associated with this {@code GameWorld}.
     */
    public void setView(GameView view) {
        this.view = view;
    }

    /**
     * Returns the {@link GameView} associated with this world.
     *
     * @return the currently assigned {@code GameView}.
     */
    public GameView getView() {
        return view;
    }

    /**
     * Returns the player character.
     * <p>
     * This method provides access to the player entity, which can be used
     * to modify player properties, position, or behavior.
     * </p>
     *
     * @return the {@link DynamicBody} representing the player.
     */
    public DynamicBody getPlayer() {
        return player;
    }

    /**
     * Reloads all dynamic entities in the world from the provided saved positions.
     * <p>
     * This method first destroys any existing {@link Enemy} or {@link Collectible}
     * bodies in the world to prevent duplication. It then recreates
     * {@link Enemy} instances at each coordinate in {@code enemyPositions},
     * and {@link Collectible} instances at each coordinate in
     * {@code collectiblePositions}.
     * </p>
     *
     * @param enemyPositions       a {@code List<Vec2>} of world coordinates at which
     *                             to spawn new {@link Enemy} instances
     * @param collectiblePositions a {@code List<Vec2>} of world coordinates at which
     *                             to spawn new {@link Collectible} instances
     */
    public void loadEntities(List<Vec2> enemyPositions, List<Vec2> collectiblePositions) {
        // Clear existing enemies and collectibles before loading new ones
        for (Body body : getDynamicBodies()) {
            if (body instanceof Enemy enemy) {
                enemy.destroy();
            }
            if (body instanceof Collectible collectible) {
                collectible.destroy();
            }
        }

        // Load enemies
        for (Vec2 position : enemyPositions) {
            new Enemy(this, position, 3f);
        }

        // Load collectibles
        for (Vec2 position : collectiblePositions) {
            new Collectible(this, position);
        }
    }

    /**
     * Indicates whether this world was populated from a saved game state.
     *
     * @return {@code true} if the world was loaded from a save file; {@code false} otherwise
     */
    public boolean isLoadedFromSave() {
        return loadedFromSave;
    }

    /**
     * Marks this world as having been loaded from a saved game state.
     * <p>
     * This flag can be used to conditionally skip certain initialization logic
     * or tutorials when restoring from a save file.
     * </p>
     *
     * @param loadedFromSave {@code true} to indicate the world is restored from save;
     *                       {@code false} to indicate a fresh start
     */
    public void setLoadedFromSave(boolean loadedFromSave) {
        this.loadedFromSave = loadedFromSave;
    }

    /**
     * Clears all enemies and collectibles from the game world.
     * <p>
     * This method iterates through all dynamic bodies in the game world and destroys any entities
     * that are instances of {@link Enemy} or {@link Collectible}.
     * It is typically used when loading a saved game state to ensure that only the entities from
     * the save file are present in the world.
     * </p>
     */
    public void clearEnemiesAndCollectibles() {
        // Remove all enemies and collectibles
        for (Body body : getDynamicBodies()) {
            if (body instanceof Enemy || body instanceof Collectible) {
                body.destroy();
            }
        }
    }
}