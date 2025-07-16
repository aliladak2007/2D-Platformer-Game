package game.core;

import game.audio.SoundManager;
import game.entities.Player;
import org.jbox2d.common.Vec2;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

/**
 * Handles keyboard input for controlling the {@link Player} character.
 * <p>
 * This class listens for key events to move the player left and right,
 * apply acceleration and friction, and control jumping behavior.
 * It also updates the player's animation based on current movement.
 * </p>
 * <p>
 * The controller implements a physics-based movement system with:
 * <ul>
 *   <li>Gradual acceleration when movement keys are pressed</li>
 *   <li>Maximum speed limits to prevent excessive velocity</li>
 *   <li>Friction to slow the player when no movement keys are pressed</li>
 *   <li>Jump mechanics with a single-jump limitation (until landing)</li>
 * </ul>
 * </p>
 * <p>
 * Control scheme:
 * <ul>
 *   <li>Left movement: Left arrow key or A key</li>
 *   <li>Right movement: Right arrow key or D key</li>
 *   <li>Jump: Up arrow key or W key</li>
 * </ul>
 * </p>
 */
public class PlayerController implements KeyListener {

    /**
     * The {@link Player} character that this controller manipulates.
     * This reference is used to modify the player's velocity and animation state.
     */
    private final Player player;

    /**
     * Current horizontal speed of the player. This value is updated on key presses
     * and applies friction when no key is pressed.
     * <p>
     * Positive values move the player right, negative values move the player left.
     * The magnitude is capped by {@link #maxSpeed}.
     * </p>
     */
    private float speed = 0;

    /**
     * The maximum horizontal speed the player can reach (in units/second).
     * <p>
     * This value caps both positive (rightward) and negative (leftward) movement
     * to prevent the player from moving too quickly.
     * </p>
     */
    private final float maxSpeed = 5;

    /**
     * The acceleration value applied when the left or right movement keys are held down.
     * The final speed is clamped by {@link #maxSpeed}.
     * <p>
     * Higher values make the player accelerate more quickly, creating a more
     * responsive but potentially less precise control feel.
     * </p>
     */
    private final float acceleration = 0.3f;

    /**
     * The friction value applied each frame when no movement key is pressed,
     * causing the player to gradually slow down horizontally.
     * <p>
     * This value is applied as a multiplier: speed *= (1 - friction).
     * Higher values make the player stop more quickly when no key is pressed.
     * </p>
     */
    private final float friction = 0.2f;

    /**
     * A flag indicating whether the player is currently allowed to jump.
     * This is typically reset when the player lands on a solid surface.
     * <p>
     * This prevents the player from performing multiple jumps while airborne,
     * implementing a single-jump mechanic common in platformer games.
     * </p>
     */
    private boolean canJump = true;

    /**
     * A boolean indicating whether a movement key (left or right) is currently pressed.
     * Used to apply friction when no key is pressed.
     * <p>
     * When true, the player maintains their current speed (subject to acceleration).
     * When false, friction is applied to gradually slow the player down.
     * </p>
     */
    private boolean keyIsPressed = false;

    /**
     * Constructs a {@code PlayerController} for the specified player.
     * <p>
     * This controller will modify the player's velocity based on keyboard input
     * and update the player's animation state accordingly.
     * </p>
     *
     * @param player The {@link Player} character to control. Must not be null.
     */
    public PlayerController(Player player) {
        this.player = player;
    }

    /**
     * Updates the player's movement on each frame.
     * <p>
     * This method applies friction if no movement key is pressed. Otherwise, the player maintains
     * their current horizontal speed (bounded by {@link #maxSpeed}). The vertical velocity is
     * preserved from the player's current velocity.
     * </p>
     * <p>
     * This method should be called once per frame in the game loop to ensure
     * smooth movement and proper physics application.
     * </p>
     */
    public void update() {
        Vec2 currentVelocity = player.getLinearVelocity();

        // Apply friction when no key is held
        if (!keyIsPressed) {
            if (Math.abs(speed) < 0.1f) {
                speed = 0;
            } else {
                speed *= (1 - friction);
            }
        }

        // Apply updated horizontal speed while preserving vertical motion
        player.setLinearVelocity(new Vec2(speed, currentVelocity.y));

        // Update the player's animation based on velocity
        player.updateAnimation(player.getLinearVelocity());
    }

    /**
     * Resets the ability to jump, allowing the player to jump again on the next jump input.
     * Typically called when the player lands on a platform or ground.
     * <p>
     * This method is usually invoked by collision detection code when the player
     * makes contact with a surface that can be jumped from.
     * </p>
     */
    public void resetJump() {
        canJump = true;
    }

    /**
     * Handles key press events for movement (left, right) and jumping (up).
     * <p>
     * Pressing left or right modifies the horizontal speed by {@link #acceleration}.
     * Pressing up performs a jump if {@link #canJump} is true.
     * </p>
     * <p>
     * Supported keys:
     * <ul>
     *   <li>Left movement: Left arrow key or A key</li>
     *   <li>Right movement: Right arrow key or D key</li>
     *   <li>Jump: Up arrow key or W key</li>
     * </ul>
     * </p>
     *
     * @param e The {@link KeyEvent} triggered when a key is pressed.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        // Detect movement key press
        if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_RIGHT ||
                code == KeyEvent.VK_A || code == KeyEvent.VK_D) {
            keyIsPressed = true;
        }

        switch (code) {
            case KeyEvent.VK_LEFT, KeyEvent.VK_A -> {
                // Move left
                speed = Math.max(speed - acceleration, -maxSpeed);
            }
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> {
                // Move right
                speed = Math.min(speed + acceleration, maxSpeed);
            }
            case KeyEvent.VK_UP, KeyEvent.VK_W -> {
                // Jump
                if (canJump) {
                    Vec2 currentVelocity = player.getLinearVelocity();
                    player.setLinearVelocity(new Vec2(currentVelocity.x, 10));
                    canJump = false;
                    SoundManager.getInstance().playJumpSound();
                }
            }
        }
    }

    /**
     * Handles key release events to detect when movement keys are no longer pressed.
     * <p>
     * When movement keys (left/right or A/D) are released, this method sets
     * {@link #keyIsPressed} to false, which allows friction to be applied in the
     * {@link #update()} method.
     * </p>
     *
     * @param e The {@link KeyEvent} triggered when a key is released.
     */
    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_RIGHT ||
                code == KeyEvent.VK_A || code == KeyEvent.VK_D) {
            keyIsPressed = false;
        }
    }

    /**
     * Unused method for handling key typed events (not needed for game controls).
     * <p>
     * This method is part of the {@link KeyListener} interface but is not used
     * for game control purposes. Key typed events represent character input rather
     * than physical key presses, which are not relevant for movement controls.
     * </p>
     *
     * @param e The {@link KeyEvent} triggered when a key is typed.
     */
    @Override
    public void keyTyped(KeyEvent e) {
        // Not used in this context
    }
}