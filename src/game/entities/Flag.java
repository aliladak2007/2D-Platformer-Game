package game.entities;

import city.cs.engine.*;
import org.jbox2d.common.Vec2;

/**
 * Represents the level completion flag in the game.
 * <p>
 * The {@code Flag} is a {@link StaticBody} that marks the end of a level.
 * When the player collides with it, the game checks for level completion.
 * </p>
 */
public class Flag extends StaticBody {

    /** Shape of the flag: tall and narrow rectangle. */
    private static final Shape FLAG_SHAPE = new BoxShape(0.5f, 2);

    /** Image representing the visual appearance of the flag. */
    private static final BodyImage FLAG_IMAGE = new BodyImage("data/flag.png", 4);

    /**
     * Constructs a {@code Flag} at the specified position in the given game world.
     *
     * @param world    The {@link World} where the flag is placed.
     * @param position The {@link Vec2} position where the flag will appear.
     */
    public Flag(World world, Vec2 position) {
        super(world, FLAG_SHAPE);     // Create the flag with the defined shape
        addImage(FLAG_IMAGE);         // Set the flag's image
        setPosition(position);        // Position the flag in the game world
    }
}
