package Tanks;

import java.util.*;

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

    public Explosion(int x, int y, int radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.elapsedTime = 0;

    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getRadius() {
        return this.radius;
    }

    public float getElapsedTime() {
        return elapsedTime;
    }

    public float getProgress() {
        return this.progress;
    }

    public void update() {
        // Calculate progress of explosion animation
        elapsedTime += 1/30f;
        progress = (float) elapsedTime / explosionDuration;

        // Ensure progress is capped at 1
        if (progress > 1.0f) {
            progress = 1.0f;
        }
        
    }


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
