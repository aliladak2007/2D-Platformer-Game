package game.audio;

import city.cs.engine.SoundClip;
import java.io.IOException;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Manages game audio using the {@link SoundClip} class.
 * <p>
 * The {@code SoundManager} class follows the Singleton pattern to ensure that
 * only one instance manages all background music and sound effects. It provides
 * controls for playback and volume adjustment of both background music and
 * various sound effects (SFX).
 *  The sounds are loaded in a background thread during initialization
 *  to prevent the UI from freezing while waiting for sound files to load.
 * </p>
 */
public class SoundManager {

    // === Singleton Instance ===
    /**
     * The single instance of {@code SoundManager}.
     */
    private static SoundManager instance;

    // === Instance Field ===
    /**
     * The {@code SoundClip} used for looping background music.
     */
    private SoundClip backgroundMusic;

    // === Static Sound Effects ===
    /** The static {@code SoundClip} for the jump sound effect. */
    private static SoundClip jumpSound;
    /** The static {@code SoundClip} for the collision sound effect. */
    private static SoundClip collisionSound;
    /** The static {@code SoundClip} for the enemy destroyed sound effect. */
    private static SoundClip enemyDestroyedSound;
    /** The static {@code SoundClip} for the collectible sound effect. */
    private static SoundClip collectibleSound;
    /** The static {@code SoundClip} for the level complete sound effect. */
    private static SoundClip levelCompleteSound;
    /** The static {@code SoundClip} for the game over sound effect. */
    private static SoundClip gameOverSound;
    /** The static {@code SoundClip} for the button press sound effect*/
    private static SoundClip buttonSound;
    /** Flag indicating if the game over sound has been played (to avoid repeats). */
    private boolean gameOverPlayed = false;

    // === Static Initialization of Sound Effects ===
    static {
        // Load all sounds in the background
        new Thread(() -> {
            try {
                jumpSound = new SoundClip("data/jump.wav");
                collisionSound = new SoundClip("data/collision.wav");
                enemyDestroyedSound = new SoundClip("data/enemy_destroyed.wav");
                collectibleSound = new SoundClip("data/collectible.wav");
                levelCompleteSound = new SoundClip("data/level_complete.wav");
                gameOverSound = new SoundClip("data/game_over.wav");
                buttonSound = new SoundClip("data/buttons.wav");
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                System.err.println("Error loading sounds: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Private constructor that loads the background music.
     * <p>
     * This is private because {@code SoundManager} follows the Singleton
     * pattern, so only one instance is allowed.
     * </p>
     */
    private SoundManager() {
        try {
            backgroundMusic = new SoundClip("data/gametheme.wav");
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error loading background music: " + e);
        }
    }

    /**
     * Returns the singleton instance of {@code SoundManager}, creating it if necessary.
     *
     * @return The single instance of {@code SoundManager}.
     */
    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    // === Background Music Controls ===

    /**
     * Starts the background music in a continuous loop.
     * Does nothing if the music failed to load.
     */
    public void loopBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.loop();
        }
    }

    /**
     * Stops the background music if it is playing.
     */
    public void stopBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }
    }

    /**
     * Adjusts the volume of the background music.
     * <p>
     * Clamps the multiplier to a minimum of 0.0001 to avoid illegal values.
     * A volume of 1.0 indicates 100%; 2.0 indicates 200%, etc.
     * </p>
     *
     * @param volume A multiplier for the default background music volume.
     */
    public void setBackgroundVolume(double volume) {
        double clamped = Math.max(volume, 0.0001);
        if (backgroundMusic != null) {
            backgroundMusic.setVolume(clamped);
        }
    }

    // === Sound Effects Volume Control ===

    /**
     * Adjusts the volume of all sound effects (SFX).
     * <p>
     * Clamps the multiplier to a minimum of 0.0001.
     * </p>
     *
     * @param volume A multiplier for the default SFX volume.
     */
    public void setSFXVolume(double volume) {
        double clamped = Math.max(volume, 0.0001);
        if (jumpSound != null) jumpSound.setVolume(clamped);
        if (collisionSound != null) collisionSound.setVolume(clamped);
        if (enemyDestroyedSound != null) enemyDestroyedSound.setVolume(clamped);
        if (collectibleSound != null) collectibleSound.setVolume(clamped);
        if (levelCompleteSound != null) levelCompleteSound.setVolume(clamped);
        if (gameOverSound != null) gameOverSound.setVolume(clamped);
        if (buttonSound != null) buttonSound.setVolume(clamped);
    }

    // === Sound Effect Playback Methods ===

    /** Plays the jump sound effect once, if loaded. */
    public void playJumpSound() {
        if (jumpSound != null) jumpSound.play();
    }

    /** Plays the collision sound effect once, if loaded. */
    public void playCollisionSound() {
        if (collisionSound != null) collisionSound.play();
    }

    /** Plays the enemy destroyed sound effect once, if loaded. */
    public void playEnemyDestroyedSound() {
        if (enemyDestroyedSound != null) enemyDestroyedSound.play();
    }

    /** Plays the collectible sound effect once, if loaded. */
    public void playCollectibleSound() {
        if (collectibleSound != null) collectibleSound.play();
    }

    /** Plays the level complete sound effect once, if loaded. */
    public void playLevelCompleteSound() {
        if (levelCompleteSound != null) levelCompleteSound.play();
    }

    /** Plays the button sound effect. */
    public static void playButtonSound() {
        if (buttonSound != null) {
            buttonSound.play();
        }
    }

    /**
     * Plays the game over sound effect once, if loaded, and prevents replay
     * until {@link #resetGameOverSound()} is called.
     */
    public synchronized void playGameOverSound() {
        if (!gameOverPlayed && gameOverSound != null) {
            gameOverSound.play();
            gameOverPlayed = true;
        }
    }

    /**
     * Stops the collision sound if it is playing.
     */
    public synchronized void stopCollisionSound() {
        if (collisionSound != null) {
            collisionSound.stop();
        }
    }

    /**
     * Stops the game over sound if it is playing.
     */
    public synchronized void stopGameOverSound() {
        if (gameOverSound != null) {
            gameOverSound.stop();
        }
    }

    /**
     * Resets the game over sound playback flag,
     * allowing it to be played again.
     */
    public void resetGameOverSound() {
        gameOverPlayed = false;
    }
}
