package Tanks;

import java.util.*;

/**
 * Represents an explosion in the game. Each explosion has a position, radius, and duration, and can update its progress and affect the terrain.
 */
 public class Explosion {
    private int x;
    private int y;
    private int radius;
    private final float explosionDuration = 0.2f;
    private float elapsedTime;
    private float progress;
    private float redRadius;
    private float orangeRadius;
    private float yellowRadius;

    /**
     * Create a new explosion with the given position and radius.
     *
     * @param x x-coordinate of the explosion
     * @param y y-coordinate of the explosion
     * @param radius the radius of the explosion
     */

    public Explosion(int x, int y, int radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.elapsedTime = 0;
    }

    /**
     * Gets the x-coordinate of the explosion.
     * @return the x-coordinate of the explosion
     */

    public int getX() {
        return this.x;
    }

    /**
     * Gets the y-coordinate of the explosion.
     * @return the y-coordinate of the explosion
     */

    public int getY() {
        return this.y;
    }

    /**
     * Gets the radius of the explosion.
     *
     * @return the radius of the explosion
     */

    public int getRadius() {
        return this.radius;
    }

    /**
     * Gets the progress of the explosion animation.
     *
     * @return the progress of the explosion animation
     */

    public float getProgress() {
        return this.progress;
    }

    /**
     * Updates the explosion animation progress.
     */
    public void update() {
        // Calculate progress of explosion animation
        elapsedTime += 1/30f;
        progress = (float) elapsedTime / explosionDuration;

        // Ensure progress is capped at 1
        if (progress > 1.0f) {
            progress = 1.0f;
        }
        
    }

    /**
     * Modifies the terrain height array based on the explosion.
     *
     * @param terrainHeight the array that contains terrain heights
     * @return the modified terrain height array
     */

    public int[] terrainExplosion(int[] terrainHeight) {
        for (int i = Math.max(0, x - radius); i < Math.min(896, x + radius); i++) {
            int height = terrainHeight[i];
            int explosionYrange = (int) Math.sqrt(Math.pow(this.radius, 2) - Math.pow(i - x, 2));
            if (height <= this.y - explosionYrange) {
                height = height + 2 * explosionYrange;
            }
            else if (height <= this.y + explosionYrange && height >= this.y - explosionYrange) {
                height = this.y + explosionYrange;
            }
            terrainHeight[i] = height;
        }
        return terrainHeight;

    }

    /**
     * Draws the explosion.
     *
     * @param app the application instance
     */
    
    public void draw(App app) {
        if (elapsedTime < explosionDuration) {
            float current_radius = elapsedTime/explosionDuration * radius;

            redRadius = (int) (2 * current_radius);
            orangeRadius = (int) (current_radius);
            yellowRadius = (int) (0.4f * current_radius);

            app.noStroke();
            app.fill(255, 0, 0);
            app.ellipse(x, y, redRadius, redRadius);
            app.fill(255, 128, 0);
            app.ellipse(x, y, orangeRadius, orangeRadius);
            app.fill(255, 255, 0); 
            app.ellipse(x, y, yellowRadius, yellowRadius);
            
        }
        
    }
}
