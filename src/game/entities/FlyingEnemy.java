package game.entities;

import city.cs.engine.*;
import org.jbox2d.common.Vec2;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Represents a flying enemy that extends the behavior of a regular enemy by adding vertical movement.
 */
public class FlyingEnemy extends Enemy {

    /**
     * A separate Timer for vertical (flying) movement.
     * We store it here so we can stop/resume it independently.
     */
    private Timer flyingTimer;

    /**
     * Indicates whether this entity has already been destroyed.
     * <p>
     * Once {@link #destroy()} is called, this flag is set to {@code true},
     * allowing game logic to check destruction state without relying solely
     * on the physics engine’s body removal.
     * </p>
     */
    private boolean isDestroyed = false;

    /**
     * Constructs a FlyingEnemy with a specified position in the game world.
     * <p>
     * This constructor calls the superclass constructor with a speed of 0 to disable horizontal movement,
     * removes the default enemy images, adds a custom .gif image, and starts the flying-specific vertical movement.
     * </p>
     *
     * @param world    The game world the flying enemy exists in.
     * @param position The starting position of the flying enemy.
     */
    public FlyingEnemy(World world, Vec2 position) {
        // Call the Enemy constructor (set speed to 0 so horizontal movement is disabled)
        super(world, position, 0);

        // Remove the default enemy image(s) and add .gif
        removeAllImages();
        addImage(new BodyImage("data/flying_enemy.gif", 3.0f)); // adjust scale as needed

        // Start the flying-specific vertical movement
        startFlyingMovement();
    }

    /**
     * Overrides updateAnimation to prevent the timer in the superclass from changing the .gif image.
     * <p>
     * This method is intentionally left empty to maintain the custom flying enemy image.
     * </p>
     */
    @Override
    public void updateAnimation() {
        // Do nothing so that the custom .gif remains displayed.
    }

    /**
     * Starts the vertical oscillation movement of the flying enemy.
     * <p>
     * A timer is used to alternate the vertical velocity between moving up and moving down.
     * </p>
     */
    private void startFlyingMovement() {
        flyingTimer = new Timer(800, new ActionListener() {
            private boolean movingUp = true;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (movingUp) {
                    setLinearVelocity(new Vec2(0, 3f)); // Move up
                } else {
                    setLinearVelocity(new Vec2(0, -3f)); // Move down
                }
                movingUp = !movingUp; // Toggle direction
            }
        });
        flyingTimer.start();
    }

    /**
     * Stops both the superclass movement timer (if any) and this flyingTimer,
     * so the flying enemy no longer moves horizontally or vertically.
     */
    @Override
    public void stopMovement() {
        super.stopMovement(); // stops the superclass movementTimer
        if (flyingTimer != null && flyingTimer.isRunning()) {
            flyingTimer.stop();
        }
    }

    /**
     * Resumes both the superclass movement timer (if any) and this flyingTimer,
     * so the flying enemy continues moving horizontally and vertically.
     */
    @Override
    public void resumeMovement() {
        super.resumeMovement(); // resumes the superclass movementTimer
        if (flyingTimer != null && !flyingTimer.isRunning()) {
            flyingTimer.start();
        }
    }

    /**
     * Marks this entity as destroyed and invokes the superclass destruction logic.
     * <p>
     * Setting {@code isDestroyed} before calling {@code super.destroy()} ensures
     * that any subsequent checks to {@link #isDestroyed()} will accurately reflect
     * that this object has been removed.
     * </p>
     */
    public void destroy() {
        isDestroyed = true;
        super.destroy();
    }

    /**
     * Checks whether this entity has been destroyed.
     *
     * @return {@code true} if {@link #destroy()} has been called on this object;
     *         {@code false} otherwise.
     */
    public boolean isDestroyed() {
        return isDestroyed;
    }
}
