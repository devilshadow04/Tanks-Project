package Tanks;

/**
 * Represents a projectile in the game. Projectiles are fired by tanks and travel until they collide with terrain or go out of bounds.
 */
public class Projectile {
    private float x;
    private float y;
    private float vx;
    private float vy;
    private char name;
    private double ax;
    private double ay;
    private double t;
    private boolean isLargerExplosion;

    /**
     * Create a new projectile with the given parameters.
     *
     * @param name the name of the tank that fired the projectile
     * @param x the x-coordinate of the projectile
     * @param y the y-coordinate of the projectile
     * @param vx the velocity in the x-direction
     * @param vy the velocity in the y-direction
     * @param isLargerExplosion indicates whether the projectile causes a larger explosion
     */

    public Projectile(char name, float x, float y, float vx, float vy, boolean isLargerExplosion) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.isLargerExplosion = isLargerExplosion;

    }

    /**
     * Gets the name of the tank that fired the projectile.
     * @return the name of the tank
     */

    public char getName() {
        return this.name;
    }

    /**
     * Gets the current x-coordinate of the projectile.
     * @return the x-coordinate of the projectile
     */
    public int getX() {
        return (int)this.x;
    }

    /**
     * Gets the current y-coordinate of the projectile.
     * @return the y-coordinate of the projectile
     */
    public int getY() {
        return (int) this.y;
    }

    /**
     * Checks whether the projectile causes a larger explosion.
     * @return true if the projectile causes a larger explosion, false otherwise
     */
    public boolean isLargerExplosion() {
        return this.isLargerExplosion;
    }

    /**
     * Updates the position of the projectile based on the wind speed.
     * @param windSpeed the current wind speed
     */
    public void update(int windSpeed) {
        // Apply gravity acceleration
        
        vx += windSpeed * 0.03f/30; // Adjust for frame rate

        // Apply gravity acceleration
        vy -= 0.24; // Constant gravity (pixels per second squared)

        // Update position based on velocity
        x += vx;
        y -= vy;
    }

    /**
     * Checks whether the projectile collides with the terrain.
     * @param terrainHeight the array that contains terrain heights
     * @return true if the projectile collides with the terrain, false otherwise
     */
    
    public boolean checkTerrainCollision(int[] terrainHeight) {

        int height = terrainHeight[(int) x]; // Get the terrain height at the projectile's x-coordinate
 
        return (y >= height); // Check if the projectile's y-coordinate is at or below the terrain height
    }

    /**
     * Checks if the projectile is out of the map bounds.
     * @return true if the projectile is out of the map bounds, false otherwise
     */
    public boolean isOutOfMap() {
        if (x >= 864 || x <= 0) {
            return true;
        }
        if (y >= 640) {
            return true;
        }
        return false;
    }

    /**
     * Draws the projectile.
     * @param app the application instance
     */
    
    public void draw(App app) {
        // Draw the projectile at its current position
        String fill_index = app.playerColours.getString(Character.toString(this.name));
        int index_1 = Integer.parseInt(fill_index.split(",")[0]);
        int index_2 = Integer.parseInt(fill_index.split(",")[1]);
        int index_3 = Integer.parseInt(fill_index.split(",")[2]);
        app.fill(index_1, index_2, index_3);
        app.noStroke();
        app.ellipse((int) x, (int) y, 6, 6);
    }
}
    



