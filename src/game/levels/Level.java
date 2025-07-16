package game.levels;

import city.cs.engine.World;
import game.core.GameWorld;
import game.entities.*;
import org.jbox2d.common.Vec2;

/**
 * Abstract base class for a game level.
 * <p>
 * Provides a common structure and helper methods for setting up game objects
 * like platforms, enemies, collectibles, and flags. Subclasses must implement
 * the {@code setupLevelSpecific} method to define their unique level layout.
 * </p>
 * <p>
 * This class follows the Template Method design pattern, where:
 * <ul>
 *   <li>The {@link #setupLevel(World)} method defines the common algorithm for level setup</li>
 *   <li>The {@link #setupLevelSpecific(World)} abstract method allows subclasses to customize the level layout</li>
 *   <li>Helper methods like {@link #createPlatform(World, Vec2)} provide standardized ways to create game entities</li>
 * </ul>
 * </p>
 * <p>
 * Concrete level implementations (e.g., {@code Level1}, {@code Level2}) extend this class
 * and define their specific layouts by overriding {@code setupLevelSpecific}.
 * This approach ensures consistency across levels while allowing for unique designs.
 * </p>
 */
public abstract class Level {

    /**
     * The texture to use for platforms in the level. Subclasses must assign this.
     * <p>
     * This field should be initialized in the constructor of concrete level classes
     * to specify the visual appearance of platforms for that specific level.
     * </p>
     */
    protected String platformTexture;

    /**
     * Sets up the level by invoking the level-specific setup routine,
     * followed by placing the common level-end flag.
     * <p>
     * This method implements the Template Method design pattern by:
     * <ol>
     *   <li>First calling the abstract {@link #setupLevelSpecific(World)} method that subclasses must implement</li>
     *   <li>Then placing a level-completion flag at a standard position</li>
     * </ol>
     * </p>
     * <p>
     * This ensures that all levels have a consistent structure while allowing
     * for customization in the specific layout of platforms, enemies, and collectibles.
     * </p>
     *
     * @param world The {@link World} in which the level is being set up.
     *              This is typically a {@link GameWorld} instance.
     */
    public void setupLevel(World world) {
        setupLevelSpecific(world);

        // If this is not a saved game, add default enemies and collectibles
        if (!((GameWorld) world).isLoadedFromSave()) {
            createDefaultEntities(world);
        }

        createFlag(world, new Vec2(10, -9)); // Common flag position
    }

    /**
     * Abstract method to define level-specific setup logic.
     * Subclasses must override this to add their custom platforms, enemies, etc.
     * <p>
     * This is where the unique layout and challenges of each level are defined.
     * Implementations typically use the helper methods provided by this class
     * (such as {@link #createPlatform}, {@link #createEnemy}, etc.) to place
     * game objects at specific positions.
     * </p>
     * <p>
     * Example implementation:
     * <pre>
     * {@code
     * protected void setupLevelSpecific(World world) {
     *     // Set platform texture for this level
     *     platformTexture = "data/platform2.png";
     *
     *     // Create platforms
     *     createPlatform(world, new Vec2(-8, -8));
     *     createPlatform(world, new Vec2(0, -5));
     *
     *     // Add collectibles
     *     createCollectible(world, new Vec2(-8, -6));
     *
     *     // Add enemies
     *     createEnemy(world, new Vec2(5, -9), 2.0f);
     * }
     * }
     * </pre>
     * </p>
     *
     * @param world The game world in which to place the objects.
     */
    protected abstract void setupLevelSpecific(World world);

    /**
     * Creates default entities in the specified world.
     * <p>
     * This method is responsible for adding default enemies and collectibles to the world.
     * It is typically called during the initialization of the world to set up the initial game state.
     * The specific entities added are determined by the implementation of this method in subclasses.
     * </p>
     *
     * @param world The world in which to create the default entities.
     * @see World
     */
    protected void createDefaultEntities(World world) {
        // Add default enemies and collectibles here
    }

    /**
     * Creates a static platform at the given position.
     * <p>
     * Platforms are the primary surfaces that the player can walk and jump on.
     * They use the texture specified by the {@link #platformTexture} field,
     * which should be set by the concrete level implementation.
     * </p>
     *
     * @param world    The game world.
     * @param position The position for the platform, specified as a 2D vector.
     */
    protected void createPlatform(World world, Vec2 position) {
        new Platform(world, position, platformTexture);
    }

    /**
     * Creates a collectible item at the specified position.
     * <p>
     * Collectibles are items that the player can gather to increase their score.
     * The player typically needs to collect all items in a level to complete it.
     * </p>
     *
     * @param world    The game world.
     * @param position The position for the collectible, specified as a 2D vector.
     */
    protected void createCollectible(World world, Vec2 position) {
        new Collectible(world, position);
    }

    /**
     * Creates an obstacle at the specified position.
     * <p>
     * Obstacles are hazardous objects that damage the player upon contact.
     * They typically reduce the player's health when touched.
     * </p>
     *
     * @param world    The game world.
     * @param position The position for the obstacle, specified as a 2D vector.
     */
    protected void createObstacle(World world, Vec2 position) {
        new Obstacle(world, position);
    }

    /**
     * Creates a walking enemy at the specified position and with the given speed.
     * <p>
     * Walking enemies move horizontally along platforms and damage the player upon contact.
     * They can be defeated by jumping on top of them.
     * </p>
     *
     * @param world    The game world.
     * @param position The starting position of the enemy, specified as a 2D vector.
     * @param speed    The speed at which the enemy moves, in units per second.
     *                 Positive values move right, negative values move left.
     */
    protected void createEnemy(World world, Vec2 position, float speed) {
        new Enemy(world, position, speed);
    }

    /**
     * Creates a flying enemy at the specified position.
     * <p>
     * Flying enemies move in a pattern through the air and are not constrained to platforms.
     * They damage the player upon contact and typically follow a predefined flight path.
     * </p>
     * <p>
     * Note: This method requires the world parameter to be a {@link GameWorld} instance,
     * as flying enemies need access to game-specific functionality.
     * </p>
     *
     * @param world    The game world, must be a {@link GameWorld} instance.
     * @param position The starting position of the flying enemy, specified as a 2D vector.
     */
    protected void createFlyingEnemy(World world, Vec2 position) {
        new FlyingEnemy((GameWorld) world, position);
    }

    /**
     * Places the level-completion flag at the specified position.
     * <p>
     * The flag represents the goal that the player must reach to complete the level.
     * Typically, the player must also collect all collectibles before the flag
     * will trigger level completion.
     * </p>
     *
     * @param world    The game world.
     * @param position The location of the flag, specified as a 2D vector.
     */
    protected void createFlag(World world, Vec2 position) {
        new Flag(world, position);
    }
}