package game.entities;

import city.cs.engine.*;
import org.jbox2d.common.Vec2;

/**
 * Represents a static platform in the game world that players can stand or jump on.
 * <p>
 * A {@code Platform} is a {@link StaticBody} with a fixed size and customizable texture.
 * It does not move or respond to forces but serves as a surface for player interaction.
 * </p>
 */
public class Platform extends StaticBody {

    /** Defines the fixed shape of the platform: wide and flat. */
    private static final Shape PLATFORM_SHAPE = new BoxShape(2.5f, 0.5f);

    /**
     * Constructs a new {@code Platform} in the given game world at the specified position
     * and applies a texture to visually represent it.
     *
     * @param world           The {@link World} in which the platform exists.
     * @param position        The {@link Vec2} position of the platform.
     * @param platformTexture The file path of the platform's texture image.
     */
    public Platform(World world, Vec2 position, String platformTexture) {
        super(world, PLATFORM_SHAPE);           // Create the static body with predefined shape
        setPosition(position);                  // Set platform's position
        BodyImage platformImage = new BodyImage(platformTexture, 1.0f);
        BodyImage image = new BodyImage(platformTexture); // No size scaling here
        AttachedImage attached = new AttachedImage(this, image, 1.2f, 0f, new Vec2(0, 0)); // Scale to full platform width
    }
}
