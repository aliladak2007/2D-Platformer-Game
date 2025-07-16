package game.util;

import city.cs.engine.BodyImage;

/**
 * The {@code Animation} class represents a simple frame-based animation system
 * for cycling through an array of images at a specified delay.
 * <p>
 * This class allows you to create an animation by providing an array of images
 * (frames) and a delay between frame transitions. It automatically updates
 * the current frame when the specified delay has elapsed.
 * </p>
 */
public class Animation {
    /**
     * An array of images (frames) used in the animation.
     * Each element represents one visual frame in the sequence.
     */
    private BodyImage[] frames;

    /**
     * The index of the current frame in the animation sequence.
     * This value cycles through 0 to {@code frames.length - 1}.
     */
    private int currentFrame;

    /**
     * The timestamp (in milliseconds) when the current frame was last updated.
     * This is used to determine when to switch to the next frame.
     */
    private long lastFrameTime;

    /**
     * The delay (in milliseconds) between frame transitions.
     * Once the delay has passed, the animation progresses to the next frame.
     */
    private long frameDelay;

    /**
     * Constructs an {@code Animation} instance with the given frames and frame delay.
     *
     * @param frames     An array of {@link city.cs.engine.BodyImage} objects representing the animation frames.
     * @param frameDelay The time in milliseconds between frame changes.
     */
    public Animation(BodyImage[] frames, long frameDelay) {
        this.frames = frames;                 // Store the provided frames for the animation
        this.frameDelay = frameDelay;         // Store how long to wait before changing frames
        this.currentFrame = 0;                // Start the animation on the first frame
        this.lastFrameTime = System.currentTimeMillis(); // Record the current time for comparison
    }

    /**
     * Returns the current frame of the animation.
     * <p>
     * This method checks whether the delay period has elapsed since the last frame change.
     * If so, and if the animation contains more than one frame, it advances to the next frame.
     * The frame index is cycled using modulo arithmetic so that the animation repeats.
     * </p>
     *
     * @return The current {@link city.cs.engine.BodyImage} frame.
     */
    public BodyImage getCurrentFrame() {
        long now = System.currentTimeMillis();   // Capture the current time
        // Check if enough time has passed since the last frame update,
        // and ensure there is more than one frame before cycling
        if (now - lastFrameTime >= frameDelay && frames.length > 1) {
            currentFrame = (currentFrame + 1) % frames.length;  // Advance to the next frame (looping back if needed)
            lastFrameTime = now;                                // Update the last frame time to the current time
        }
        return frames[currentFrame];  // Return the current frame
    }

    /**
     * Resets the animation to the first frame.
     * <p>
     * This method also updates the {@code lastFrameTime} to the current time,
     * ensuring that the frame delay is recalculated from the time of the reset.
     * </p>
     */
    public void reset() {
        currentFrame = 0;  // Move the animation back to the first frame
        lastFrameTime = System.currentTimeMillis(); // Reset the timer for the animation
    }
}
