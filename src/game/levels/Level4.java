package game.levels;

import city.cs.engine.*;
import org.jbox2d.common.Vec2;

/**
 * Represents Level 4 of the game.
 * <p>
 * This class extends the {@link Level} abstract base class, providing a distinct
 * set of platforms, collectibles, obstacles, enemies, and a flag to signify level completion.
 * Level 4 uses a unique platform texture and offers a more challenging layout for the player.
 * </p>
 */
public class Level4 extends Level {

    /**
     * Constructs Level 4 and assigns a distinct platform texture.
     */
    public Level4() {
        // Use a unique texture for Level 4 platforms
        platformTexture = "data/platform4.png";
    }

    /**
     * Sets up the specific layout for Level 4, including platforms, collectibles,
     * obstacles, enemies, and a level completion flag.
     *
     * @param world The {@link World} instance where all level objects will be created.
     */
    @Override
    protected void setupLevelSpecific(World world) {
        // Create a series of platforms at various positions within jump range
        createPlatform(world, new Vec2(-10, -7f));  // Far left, low platform
        createPlatform(world, new Vec2(-5, -1f));     // Center platform at moderate height
        createPlatform(world, new Vec2(5, -4f));    // Slightly lower, right platform
        createPlatform(world, new Vec2(10, -6f));   // Far right, low platform

        // Place collectibles on or near the platforms
        createCollectible(world, new Vec2(-10, -6f));
        createCollectible(world, new Vec2(0, 0f));
        createCollectible(world, new Vec2(5, -3f));
        createCollectible(world, new Vec2(10, -6f));

        // Add obstacles at strategic positions to challenge the player's navigation
        createObstacle(world, new Vec2(2.5f, -2f));

        // Spawn ground-based and flying enemies in various parts of the level
        createEnemy(world, new Vec2(-8, -9), 3f);    // Ground enemy
        createFlyingEnemy(world, new Vec2(-2, -1f));  // Flying enemy near center

        // Create a flag near the right edge of the level for progression
        // Once the player reaches this flag (and meets the collectible requirements), the level can be completed
        createFlag(world, new Vec2(10, -9));
    }
}
