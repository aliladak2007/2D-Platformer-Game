package game.core;

import city.cs.engine.CollisionEvent;
import city.cs.engine.CollisionListener;
import city.cs.engine.StaticBody;
import game.audio.SoundManager;
import game.entities.Collectible;
import game.entities.Enemy;
import game.entities.Flag;
import game.entities.Obstacle;
import game.entities.Player;
import org.jbox2d.common.Vec2;

/**
 * Handles collision events in the game world.
 * <p>
 * Implements {@link CollisionListener} to detect and respond to various
 * in-game collisions involving the player, enemies, collectibles, obstacles,
 * and flags. Triggers actions such as reducing player health, destroying enemies,
 * collecting items, or completing a level based on collision context.
 * </p>
 */
public class CollisionHandler implements CollisionListener {

    // === Instance Field ===
    /**
     * Reference to the {@code GameWorld} containing game state (health, score, view).
     */
    private final GameWorld world;

    // === Constructor ===
    /**
     * Constructs a CollisionHandler tied to a specific game world.
     *
     * @param world the game world where collisions occur and state is managed
     */
    public CollisionHandler(GameWorld world) {
        this.world = world;
    }

    // === Collision Handling ===
    /**
     * Invoked by the physics engine when two bodies collide.
     * <p>
     * Determines the collision types and executes appropriate game logic,
     * such as playing sounds, updating health, score, or completing levels.
     * </p>
     *
     * @param e details of the collision event (reporting and other bodies)
     */
    @Override
    public void collide(CollisionEvent e) {
        // Player stomping an enemy
        if (e.getReportingBody() instanceof Player) {
            Player player = (Player) e.getReportingBody();
            if (e.getOtherBody() instanceof Enemy) {
                Enemy enemy = (Enemy) e.getOtherBody();

                // Check downward motion and vertical overlap for a "stomp"
                if (player.getLinearVelocity().y < 0) {
                    float playerBottom = player.getPosition().y - 2;
                    float enemyTop = enemy.getPosition().y + 0.5f;
                    if (playerBottom > enemyTop) {
                        SoundManager.getInstance().playEnemyDestroyedSound();
                        enemy.destroy();
                        // Bounce the player upward after stomping
                        player.setLinearVelocity(new Vec2(player.getLinearVelocity().x, 4));
                        return; // Skip damage if stomped
                    }
                }

                // Regular collision: damage player
                SoundManager.getInstance().playCollisionSound();
                world.reduceHealth();
                if (world.getHealth() <= 0) {
                    SoundManager.getInstance().stopCollisionSound();
                }
            }
        }

        // Player collects a collectible item
        if (e.getOtherBody() instanceof Collectible collectible) {
            SoundManager.getInstance().playCollectibleSound();
            collectible.destroy();
            world.incrementScore();
        }

        // Player collides with an obstacle: damage
        if (e.getOtherBody() instanceof Obstacle) {
            SoundManager.getInstance().playCollisionSound();
            world.reduceHealth();
        }

        // Player reaches flag: level completion
        if (e.getOtherBody() instanceof Flag) {
            world.checkLevelCompletion();
        }

        // Reset jump capability upon landing on static surfaces
        if (e.getReportingBody() instanceof Player && e.getOtherBody() instanceof StaticBody) {
            GameView view = world.getView();
            if (view != null) {
                view.resetJump();
            }
        }
    }
}
