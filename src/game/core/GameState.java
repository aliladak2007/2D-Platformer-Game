package game.core;

import org.jbox2d.common.Vec2;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A serializable class that encapsulates the complete state of a game session.
 * <p>
 * This class is used for saving and loading game progress, allowing players to
 * continue their game from a previously saved point. It stores essential game
 * information including:
 * <ul>
 *   <li>Current level</li>
 *   <li>Player health</li>
 *   <li>Current score</li>
 *   <li>Elapsed gameplay time</li>
 *   <li>Player position coordinates</li>
 *   <li>Positions of regular and flying enemies</li>
 * </ul>
 * </p>
 * <p>
 * GameState implements {@link Serializable} to enable Java's object serialization
 * mechanism for persistent storage.
 * </p>
 */
public class GameState implements Serializable {
    /**
     * Serial version UID for maintaining serialization compatibility
     * across different versions of the class.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The current level number.
     */
    public int level;

    /**
     * The player's current health points.
     */
    public int health;

    /**
     * The player's accumulated score.
     */
    public int score;

    /**
     * The total elapsed gameplay time in milliseconds,
     * excluding time spent in paused state.
     */
    public long elapsedTime;

    /**
     * The x-coordinate of the player's position in the game world.
     */
    public float playerX;

    /**
     * The y-coordinate of the player's position in the game world.
     */
    public float playerY;

    /**
     * Coordinates of all active regular enemies at the time of saving.
     */
    public List<Vec2> enemyPositions;

    /**
     * Coordinates of all active flying enemies at the time of saving.
     */
    public List<Vec2> flyingEnemyPositions;

    /**
     * Coordinates of all remaining collectibles at the time of saving.
     */
    public List<Vec2> collectiblePositions;

    /**
     * Constructs a new GameState with the specified parameters.
     *
     * @param level       The current level number
     * @param health      The player's current health points
     * @param score       The player's accumulated score
     * @param elapsedTime The total elapsed gameplay time in milliseconds
     * @param playerX     The x-coordinate of the player's position
     * @param playerY     The y-coordinate of the player's position
     */
    public GameState(int level, int health, int score, long elapsedTime, float playerX, float playerY) {
        this.level = level;
        this.health = health;
        this.score = score;
        this.elapsedTime = elapsedTime;
        this.playerX = playerX;
        this.playerY = playerY;
        this.enemyPositions = new ArrayList<>();
        this.flyingEnemyPositions = new ArrayList<>();
        this.collectiblePositions = new ArrayList<>();
    }
}