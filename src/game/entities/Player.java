package game.entities;

import city.cs.engine.*;
import game.util.Animation;
import org.jbox2d.common.Vec2;

/**
 * Represents the player character in the game, handling movement and animations.
 * <p>
 * The {@code Player} class extends {@link Walker} from the <em>city.cs.engine</em> library,
 * giving it physics-based movement. The player features multiple animations (idle, walk, jump),
 * which are selected based on velocity and facing direction.
 * </p>
 */
public class Player extends Walker {

    /**
     * Defines the player's physical shape as a box (2 units wide, 4 units tall).
     * <p>
     * In {@code BoxShape}, the parameters are half-width and half-height, so <code>(1f, 2f)</code>
     * corresponds to a 2x4 box in the game world.
     * </p>
     */
    private static final Shape PLAYER_SHAPE = new BoxShape(1f, 2f);

    /**
     * An animation containing the single idle frame when facing left.
     */
    private Animation idleLeftAnim;

    /**
     * An animation containing the single idle frame when facing right.
     */
    private Animation idleRightAnim;

    /**
     * An animation containing several frames for a walking cycle when facing left.
     */
    private Animation walkLeftAnim;

    /**
     * An animation containing several frames for a walking cycle when facing right.
     */
    private Animation walkRightAnim;

    /**
     * An animation with multiple frames for a jumping sequence when facing left.
     */
    private Animation jumpLeftAnim;

    /**
     * An animation with multiple frames for a jumping sequence when facing right.
     */
    private Animation jumpRightAnim;

    /**
     * A reference to the player's currently active animation. Updated based on movement state.
     */
    private Animation currentAnim;

    /**
     * Flag that indicates the player's last facing direction.
     * {@code true} means facing left, {@code false} means facing right.
     */
    private boolean facingLeft = false;

    /**
     * A scale factor for all player images, applied when loading {@link BodyImage} frames.
     */
    private final float imageScale = 4.0f;

    /**
     * The delay (in milliseconds) between frames in each animation.
     */
    private final long frameDelay = 100;

    /**
     * Constructs a {@code Player} instance in the specified {@link World}.
     * <p>
     * Initializes all animations (idle, walk, jump) for both left and right orientations,
     * and sets the default orientation to face right, displaying the idle-right frame.
     * </p>
     *
     * @param world The game world in which the player exists.
     */
    public Player(World world) {
        super(world, PLAYER_SHAPE);         // Create the player body with the specified shape
        setPosition(new Vec2(7, -9));       // Set the initial player position

        // Load all animations (idle, walk, jump) for both left and right orientations
        idleLeftAnim = loadAnimation("data/SteamMan_idle_left_", 1);
        idleRightAnim = loadAnimation("data/SteamMan_idle_right_", 1);
        walkLeftAnim = loadAnimation("data/SteamMan_walk_left_", 5);
        walkRightAnim = loadAnimation("data/SteamMan_walk_right_", 5);
        jumpLeftAnim = loadAnimation("data/SteamMan_jump_left_", 6);
        jumpRightAnim = loadAnimation("data/SteamMan_jump_right_", 6);

        // Default to facing right and show the idle-right animation
        facingLeft = false;
        currentAnim = idleRightAnim;
        removeAllImages();
        addImage(currentAnim.getCurrentFrame());
    }

    /**
     * Loads an animation from a sequence of image files that share a prefix and an index.
     * <p>
     * For example, a prefix of <code>"data/SteamMan_walk_left_"</code> and a frame count of 5
     * would expect files <code>"data/SteamMan_walk_left_0.png"</code> through
     * <code>"data/SteamMan_walk_left_4.png"</code>.
     * </p>
     *
     * @param prefix     The file name prefix for the animation frames.
     * @param frameCount The number of frames in this animation.
     * @return An {@link Animation} object containing the loaded frames.
     */
    private Animation loadAnimation(String prefix, int frameCount) {
        BodyImage[] frames = new BodyImage[frameCount];
        for (int i = 0; i < frameCount; i++) {
            frames[i] = new BodyImage(prefix + i + ".png", imageScale);
        }
        return new Animation(frames, frameDelay);
    }

    /**
     * Updates the player's animation based on their current velocity.
     * <p>
     * If the player is moving vertically (jumping), it shows a jump animation.
     * If the player is moving horizontally, it shows a walk animation.
     * Otherwise, an idle animation is shown.
     * This method also updates the {@code facingLeft} flag if the player is moving left or right.
     * </p>
     *
     * @param velocity The current velocity vector of the player.
     */
    public void updateAnimation(Vec2 velocity) {
        // Small threshold to determine if the player is "stopped" horizontally
        float horizontalStopThreshold = 0.05f;

        if (Math.abs(velocity.y) > 0.1f) {
            // If in the air, display the jump animation
            currentAnim = facingLeft ? jumpLeftAnim : jumpRightAnim;
        } else if (Math.abs(velocity.x) > horizontalStopThreshold) {
            // If moving horizontally, display the walking animation
            if (velocity.x < 0) {
                currentAnim = walkLeftAnim;
                facingLeft = true;
            } else {
                currentAnim = walkRightAnim;
                facingLeft = false;
            }
        } else {
            // If not moving horizontally or vertically, show the idle animation
            currentAnim = facingLeft ? idleLeftAnim : idleRightAnim;
        }

        // Update the current frame and refresh the displayed image
        removeAllImages();
        addImage(currentAnim.getCurrentFrame());
    }
}
