package game.levels;

import city.cs.engine.World;
import org.jbox2d.common.Vec2;

/**
 * Represents Level 2 of the game.
 * <p>
 * This class extends the {@link Level} base class and defines the
 * layout and elements specific to Level 2, including multiple platforms,
 * collectibles, enemies (both ground and flying), and obstacles.
 * It also sets a unique platform texture for visual distinction.
 * </p>
 */
public class Level2 extends Level {

    /**
     * Constructs Level 2 and sets the platform texture specific to this level.
     * <p>
     * The {@code platformTexture} is used by {@link Level#createPlatform(World, Vec2)}
     * to provide a distinct visual style for the platforms in Level 2.
     * </p>
     */
    public Level2() {
        // Assign the texture used for Level 2 platforms
        platformTexture = "data/platform2.png";
    }

    /**
     * Sets up the game objects specific to Level 2.
     * <p>
     * This method places:
     * <ul>
     *   <li>Platforms at varying heights for diverse platforming challenges</li>
     *   <li>Three collectibles positioned near or on platforms</li>
     *   <li>Two obstacles for the player to avoid</li>
     *   <li>One walking enemy and one flying enemy to increase difficulty</li>
     * </ul>
     * </p>
     *
     * @param world The {@link World} in which to place all level elements.
     */
    @Override
    protected void setupLevelSpecific(World world) {
        // Create platforms at strategic positions to encourage jumping and movement
        createPlatform(world, new Vec2(-10, -6f));
        createPlatform(world, new Vec2(0, -4f));
        createPlatform(world, new Vec2(8, -2f));

        // Place collectibles on or near the platforms
        createCollectible(world, new Vec2(-10, -5f));
        createCollectible(world, new Vec2(0, -3f));
        createCollectible(world, new Vec2(8, -1f));

        // Add an obstacle that the player must avoid or navigate around
        createObstacle(world, new Vec2(-6, 4));

        // Create a ground-based enemy and a flying enemy to challenge the player
        createEnemy(world, new Vec2(2, -9), 2f);
        createFlyingEnemy(world, new Vec2(4, 6));
    }
}
