package game.levels;

import city.cs.engine.World;
import org.jbox2d.common.Vec2;

/**
 * Represents Level 3 of the game.
 * <p>
 * This class extends the {@link Level} abstract base class and provides
 * the specific layout and elements for Level 3, including a higher number
 * of collectibles, obstacles, and flying enemies compared to previous levels.
 * It also sets a unique texture for platforms used in this level.
 * </p>
 */
public class Level3 extends Level {

    /**
     * Constructs Level 3 and sets the platform texture specific to this level.
     * <p>
     * By assigning the {@code platformTexture} field here, the base class methods
     * (e.g., {@link Level#createPlatform(World, Vec2)}) will use this specific texture
     * when creating platforms for Level 3.
     * </p>
     */
    public Level3() {
        // Assign the texture path for Level 3 platforms
        platformTexture = "data/platform3.png";
    }

    /**
     * Defines the layout and entities for Level 3.
     * <p>
     * Adds multiple platforms at varied heights, collectibles scattered
     * around the level, several obstacles, and both ground and flying enemies.
     * This setup encourages more vertical exploration compared to earlier levels.
     * </p>
     *
     * @param world The {@link World} where the level's entities will be created.
     */
    @Override
    protected void setupLevelSpecific(World world) {
        // Create a series of platforms with increasing height to promote vertical movement
        createPlatform(world, new Vec2(-10, -7f));  // Far left, low platform
        createPlatform(world, new Vec2(0, -3f));     // Center platform at moderate height
        createPlatform(world, new Vec2(5, -4f));    // Slightly lower, right platform
        createPlatform(world, new Vec2(10, -6f));   // Far right, low platform

        // Place collectibles on or near each platform
        createCollectible(world, new Vec2(-10, -5f));  // Near leftmost platform
        createCollectible(world, new Vec2(-5, -2f));   // Above the second platform
        createCollectible(world, new Vec2(0, 1f));     // Highest collectible, above center platform
        createCollectible(world, new Vec2(5, -2f));    // Above the platform to the right
        createCollectible(world, new Vec2(10, -5f));   // Near the far right platform

        // Introduce a ground-based enemy and two flying enemies at varying heights
        createEnemy(world, new Vec2(-8, -9), 3f);      // Ground enemy patrolling the lower area
        createFlyingEnemy(world, new Vec2(5, -4f));    // Another flying enemy on the right side
    }
}
