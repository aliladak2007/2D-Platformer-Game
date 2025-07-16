package game.entities;

import city.cs.engine.*;
import org.jbox2d.common.Vec2;

/**
 * Represents a collectible item in the game world.
 * <p>
 * This class extends {@link city.cs.engine.DynamicBody} to create an item
 * that can be collected by the player or other entities. It uses a circular shape
 * and displays a coin animation image.
 * </p>
 */
public class Collectible extends DynamicBody {
    /**
     * A circular shape defining the physical boundary for the collectible.
     * <p>
     * The radius of the circle is set to 0.5 units.
     * </p>
     */
    private static final Shape COLLECTIBLE_SHAPE = new CircleShape(1f);

    /**
     * A coin GIF image used as the visual representation of the collectible.
     * <p>
     * The image is scaled to a height of 1 unit.
     * </p>
     */
    private static final BodyImage COLLECTIBLE_IMAGE = new BodyImage("data/coin.gif", 2);

    private boolean isDestroyed = false;
    /**
     * Constructs a new {@code Collectible} in the specified world at a given position.
     *
     * @param world    The game world where the collectible will be placed.
     * @param position The initial position of the collectible in the world.
     */
    public Collectible(World world, Vec2 position) {
        super(world, COLLECTIBLE_SHAPE); // Create the body in the given world with the circular shape
        addImage(COLLECTIBLE_IMAGE);     // Attach the coin image to this body
        setPosition(position);           // Position the collectible at the specified location
    }

    public void destroy() {
        isDestroyed = true;
        super.destroy();
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }
}
