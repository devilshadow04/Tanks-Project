package Tanks;

public class Projectile {
    private float x;
    private float y;
    private float dx;
    private float dy;
    private char name;
    private boolean isLargerExplosion;

    public Projectile(char name, float x, float y, float dx, float dy, boolean isLargerExplosion) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.isLargerExplosion = isLargerExplosion;
    }

    public char getName() {
        return this.name;
    }

    public int getX() {
        return (int)this.x;
    }

    public int getY() {
        return (int) this.y;
    }

    public boolean isLargerExplosion() {
        return this.isLargerExplosion;
    }

    public void update(int windSpeed) {
        // Apply gravity acceleration
        dx += windSpeed * 0.03f/30;
        dy -= 0.12;

        // Update position based on velocity
        x += dx;
        y -= dy;
    }


    
    public boolean checkTerrainCollision(int[] moving_average) {
        if (x >= 864 || x <= 0) {
            return true;
        }
        if (y >= 640) {
            return true;
        }
        int terrainHeight = moving_average[(int) x]; // Get the terrain height at the projectile's x-coordinate
 
        return (y >= terrainHeight); // Check if the projectile's y-coordinate is at or below the terrain height
    }




    
    public void draw(App app) {
        // Draw the projectile at its current position
        String fill_index = app.playerColours.getString(Character.toString(this.name));
        int index_1 = Integer.parseInt(fill_index.split(",")[0]);
        int index_2 = Integer.parseInt(fill_index.split(",")[1]);
        int index_3 = Integer.parseInt(fill_index.split(",")[2]);
        app.fill(index_1, index_2, index_3);
        app.noStroke();
        app.ellipse(x, y, 6, 6);
    }
}
    



