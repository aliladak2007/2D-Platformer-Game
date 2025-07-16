package game.entities;

import city.cs.engine.*;
import org.jbox2d.common.Vec2;

import javax.swing.Timer;

/**
 * Represents a vibrating obstacle in the game.
 * <p>
 * This obstacle is a {@link StaticBody} that appears stationary to the physics engine
 * but visually vibrates left and right at a fixed interval using a {@link Timer}.
 * </p>
 */
public class Obstacle extends StaticBody {

    /**
     * The shape of the obstacle (a 2x2 square, since BoxShape sizes are half-width and half-height).
     */
    private static final Shape OBSTACLE_SHAPE = new BoxShape(1, 1);

    /**
     * The visual image for the obstacle, scaled to 2 units in height.
     */
    private static final BodyImage OBSTACLE_IMAGE = new BodyImage("data/obstacle.png", 2);

    /**
     * A boolean flag indicating the current direction of vibration:
     * {@code true} if moving right, {@code false} if moving left.
     */
    private boolean movingRight = true;

    /**
     * The distance (in world units) that the obstacle moves left or right per vibration tick.
     */
    private final float vibrationDistance = 0.1f;

    /**
     * A Swing {@link Timer} that periodically updates the obstacle's position
     * to simulate a left-right vibration.
     */
    private final Timer vibrationTimer;

    /**
     * Constructs a vibrating {@code Obstacle} at the specified position in the given world.
     * <p>
     * The obstacle vibrates left and right every 100 milliseconds, toggling direction each time.
     * </p>
     *
     * @param world    The {@link World} in which the obstacle is placed.
     * @param position The initial position of the obstacle.
     */
    public Obstacle(World world, Vec2 position) {
        super(world, OBSTACLE_SHAPE); // Define the physical shape of the obstacle
        addImage(OBSTACLE_IMAGE);     // Assign a visual representation
        setPosition(position);        // Place the obstacle at the specified position

        // Create and start the vibration timer to move the obstacle every 100ms
        vibrationTimer = new Timer(100, e -> {
            // Get the current position of the obstacle
            Vec2 currentPosition = getPosition();

            // Depending on the current direction, move the obstacle
            if (movingRight) {
                setPosition(new Vec2(currentPosition.x + vibrationDistance, currentPosition.y));
            } else {
                setPosition(new Vec2(currentPosition.x - vibrationDistance, currentPosition.y));
            }

            // Toggle the direction after each vibration tick
            movingRight = !movingRight;
        });

        // Start the timer so the obstacle begins vibrating immediately
        vibrationTimer.start();
    }

    /**
     * Stops the vibration timer so the obstacle no longer moves.
     * <p>
     * Typically called when the game is paused to freeze all activity.
     * </p>
     */
    public void stopMovement() {
        if (vibrationTimer != null && vibrationTimer.isRunning()) {
            vibrationTimer.stop();
        }
    }

    /**
     * Resumes the vibration timer so the obstacle continues vibrating.
     * <p>
     * Typically called when the game is resumed.
     * </p>
     */
    public void resumeMovement() {
        if (vibrationTimer != null && !vibrationTimer.isRunning()) {
            vibrationTimer.start();
        }
    }
}
