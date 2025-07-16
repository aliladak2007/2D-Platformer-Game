package game.entities;

import city.cs.engine.*;
import game.util.Animation;
import org.jbox2d.common.Vec2;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Represents an enemy character that moves back and forth and animates accordingly.
 */
public class Enemy extends Walker {
    /** Defines the enemy's shape. */
    private static final Shape ENEMY_SHAPE = new BoxShape(1, 1);

    /** Animation for walking left. */
    private Animation walkLeftAnim;

    /** Animation for walking right. */
    private Animation walkRightAnim;

    /** Currently active animation. */
    private Animation currentAnim;

    /** Speed of movement. */
    private float speed;

    /** Tracks movement direction; true if moving right. */
    private boolean movingRight = true;

    /** Scaling factor for images. */
    private final float imageScale = 2.0f;

    /** Delay between animation frames in milliseconds. */
    private final long frameDelay = 10;

    /**
     * The Timer that handles the enemy's back-and-forth movement.
     * We store it here so we can stop/resume it when the game is paused/resumed.
     */
    protected Timer movementTimer;

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
     * Constructs an enemy with a given position and speed.
     *
     * @param world    The game world the enemy exists in.
     * @param position The starting position of the enemy.
     * @param speed    The movement speed of the enemy.
     */
    public Enemy(World world, Vec2 position, float speed) {
        super(world, ENEMY_SHAPE);

        // Load animations for left and right movement
        walkLeftAnim = loadAnimation("data/enemy_walk_left_", 2, imageScale);
        walkRightAnim = loadAnimation("data/enemy_walk_right_", 2, imageScale);

        // Default to moving right
        currentAnim = walkRightAnim;
        removeAllImages();
        addImage(currentAnim.getCurrentFrame());

        // Set initial position and speed
        setPosition(position);
        this.speed = speed;

        // Start the movement cycle
        startMovement();
    }

    /**
     * Loads a series of images into an Animation object.
     *
     * @param prefix     The prefix of the image file names.
     * @param frameCount The number of frames in the animation.
     * @param scale      The scale factor for the images.
     * @return A new Animation object composed of the specified images.
     */
    private Animation loadAnimation(String prefix, int frameCount, float scale) {
        BodyImage[] frames = new BodyImage[frameCount];
        for (int i = 0; i < frameCount; i++) {
            frames[i] = new BodyImage(prefix + i + ".png", scale);
        }
        return new Animation(frames, frameDelay);
    }

    /**
     * Starts the enemy movement in a back-and-forth pattern.
     */
    protected void startMovement() {
        // Store the Timer in movementTimer so we can stop/resume it later.
        movementTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (movingRight) {
                    setLinearVelocity(new Vec2(speed, 0)); // Move right
                    currentAnim = walkRightAnim;
                } else {
                    setLinearVelocity(new Vec2(-speed, 0)); // Move left
                    currentAnim = walkLeftAnim;
                }
                movingRight = !movingRight; // Flip direction
                updateAnimation(); // Refresh animation frame
            }
        });
        movementTimer.start();
    }

    /**
     * Stops the movement timer so the enemy no longer moves.
     * Call this when pausing the game.
     */
    public void stopMovement() {
        if (movementTimer != null && movementTimer.isRunning()) {
            movementTimer.stop();
        }
    }

    /**
     * Resumes the movement timer so the enemy continues moving.
     * Call this when resuming the game.
     */
    public void resumeMovement() {
        if (movementTimer != null && !movementTimer.isRunning()) {
            movementTimer.start();
        }
    }

    /**
     * Updates the currently displayed animation frame.
     */
    public void updateAnimation() {
        removeAllImages();
        addImage(currentAnim.getCurrentFrame());
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
