package game.levels;

import city.cs.engine.World;
import org.jbox2d.common.Vec2;

/**
 * Represents Level 1 of the game.
 * <p>
 * This class extends the {@link Level} abstract base class and defines
 * specific platforms, enemies, collectibles, and obstacles for Level 1.
 * It also sets a unique texture for the platforms used in this level.
 * </p>
 */
public class Level1 extends Level {

    /**
     * Constructs Level 1 and sets the platform texture specific to this level.
     * <p>
     * By assigning {@code platformTexture} in the constructor,
     * {@link Level#createPlatform(World, Vec2)} will use the correct texture
     * when generating platforms.
     * </p>
     */
    public Level1() {
        // Assign the texture for platforms in Level 1
        platformTexture = "data/platform1.png";
    }

    /**
     * Sets up the specific objects and layout for Level 1.
     * <p>
     * This method places platforms at different heights, adds collectibles on
     * or near those platforms, and spawns an enemy in the lower region.
     * </p>
     *
     * @param world The {@link World} where the level's objects will be created.
     */
    @Override
    protected void setupLevelSpecific(World world) {
        // Create platforms at positions within reasonable jump distance
        createPlatform(world, new Vec2(-8, -7f));   // Lower platform
        createPlatform(world, new Vec2(1, -3.5f));  // Middle platform

        // Place collectibles on or near the platforms
        createCollectible(world, new Vec2(-8, -6f));
        createCollectible(world, new Vec2(1, -3f));

        // Create an enemy that patrols the lower region
        createEnemy(world, new Vec2(-4, -9), 3f);
    }
}
