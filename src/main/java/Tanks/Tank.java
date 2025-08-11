package Tanks;

import processing.core.PImage;
import java.util.*;


/**
 * Represents a tank in the game. The tank can move, rotate its turret, fire projectiles, and handle various states such as health, fuel, and parachutes.
 */

public class Tank {
    private int x;
    private int y;
    private double rotateAngle;
    private float turretTopX;
    private float turretTopY;
    private int health;
    private int power;
    private int fuel;
    private int parachute;
    private int score;
    private char name;
    private boolean exploded;
    private boolean isLargerExplosion;
    private static final int TURRET_LENGTH = 15; // Adjust according to your game's design


    /**
     * Creates a new Tank with the specified name and initial position.
     * 
     * @param name name of the tank.
     * @param x initial x-coordinate of the tank.
     * @param y initial y-coordinate of the tank.
     */

    public Tank(char name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.rotateAngle = (double) 0;
        this.turretTopX = x;
        this.turretTopY = y - 15 - TURRET_LENGTH;
        this.health = 100;
        this.power = 50;
        this.fuel = 250;
        this.parachute = 3;
        this.score = 0;
        this.exploded = false;
        this.isLargerExplosion = false;
    }

    /**
     * Gets the name of the tank.
     * @return name of the tank.
     */

    public char getName() {
        return this.name;
    }

    /**
     * Sets the y-coordinate of the tank.
     *
     * @param y the new y-coordinate of the tank.
     */

    public void setY(int y) {
        this.y = y;
    }

    /**
     * Gets the x-coordinate of the tank.
     *
     * @return the x-coordinate of the tank.
     */

    public int getX() {
        return this.x;
    }

    /**
     * Gets the y-coordinate of the tank.
     *
     * @return the y-coordinate of the tank.
     */

    public int getY() {
        return this.y;
    }

    /**
     * Gets the score of the tank.
     *
     * @return the score of the tank.
     */
    public int getScore() {
        return this.score;
    }

    /**
     * Resets the score of the tank to 0.
     */

    public void resetScore() {
        this.score = 0;
    }

    /**
     * Sets the location of the tank.
     *
     * @param x the new x-coordinate of the tank
     * @param y the new y-coordinate of the tank
     */

    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Minus the score of the tank by the specified amount.
     *
     * @param lostScore the amount of score to be losted
     */

    public void lostScore(int lostScore) {
        this.score -= lostScore;
    }


    /**
     * If the tank still have fuels, moves the tank to the left based on the terrain height.
     *
     * @param terrainHeight the array that contains terrain heights
     */

    public void moveLeft(int[] terrainHeight) {
        if (this.fuel <= 0) {
            return;
        }
        if (this.x >= 0) {
            this.x -= 1;
            this.y = terrainHeight[this.x];
            this.fuel -= 1;
        }
    }

    /**
     * If the tank still have fuels, moves the tank to the right based on the terrain height.
     *
     * @param terrainHeight the array that contains terrain heights
     */

    public void moveRight(int[] terrainHeight) {
        if (this.fuel <= 0) {
            return;
        }
        if (this.x <= terrainHeight.length){
            this.x += 1;
            this.y = terrainHeight[this.x];
            this.fuel -=1;
        }
    }

    /**
     * Enables the tank to fire a larger projectile.
     */

    public void largerProjectile() {
        this.isLargerExplosion = true;
    }

    /**
     * Checks if the tank is firing a larger projectile.
     *
     * @return true if the tank is firing a larger projectile, false otherwise
     */

    public boolean getLargerProjectile() {
        return this.isLargerExplosion;
    }

    /**
     * Moves the turret of the tank to the left.
     */

    public void moveTurretLeft() {
        rotateAngle = Math.max(rotateAngle - Math.PI / 18, -Math.PI / 2); // Adjust the rotation angle with a lower bound of -π/2
        updateTurretPosition();
    }

    /**
     * Moves the turret of the tank to the right.
     */    
    
     public void moveTurretRight() {
        rotateAngle = Math.min(rotateAngle + Math.PI / 18, Math.PI / 2); // Adjust the rotation angle with an upper bound of π/2
        updateTurretPosition();
    }

    /**
     * Updates the position of the turret based on the current rotation angle.
     */
    private void updateTurretPosition() {
        // Calculate the position of the top of the turret after rotation
        turretTopX = x + (float) Math.cos(Math.PI/2 - rotateAngle) * TURRET_LENGTH;
        turretTopY = y-15 - (float) Math.sin(Math.PI/2 - rotateAngle) * TURRET_LENGTH;
    }

    /**
     * Gets the x-coordinate of the top of the turret.
     *
     * @return x-coordinate of the turret's top
     */    
    
     public float getTurretTopX() {
        return turretTopX;
    }

    /**
     * Gets the y-coordinate of the top of the turret.
     *
     * @return y-coordinate of the turret's top
     */    
    public float getTurretTopY() {
        return turretTopY;
    }

    /**
     * Gets the current rotation angle of the turret.
     *
     * @return current rotation angle of the turret
     */

    public double getRotateAngle() {
        return this.rotateAngle;
    }

    /**
     * Gets the number of remaining parachutes the tank has.
     *
     * @return the number of parachutes
     */
    public int getParachute() {
        return this.parachute;
    }

    /**
     * Resets the number of parachutes to 3.
     */
    public void resetParachute() {
        this.parachute = 3;
    }

    /**
     * Increases the number of parachutes by the specified amount.
     *
     * @param n the number of parachutes to add
     */

    public void increaseParachute(int n) {
        this.parachute += n;
    }

    /**
     * Gets the power level of the tank.
     *
     * @return the power level of the tank
     */
    public int getPower() {
        return this.power;
    }
    
    /**
     * Minus the health of the tank by the specified amount.
     *
     * @param damage the amount of health to be losted
     */
    public void lostHealth(int damage) {
        this.health -= damage;
        if (this.health < 0) {
            this.health = 0;
        } 
        if (this.power >= this.health) {
            this.power = this.health;
        }
    }

    /**
     * Increases the health of the tank by the specified amount, but not exceeds 100.
     *
     * @param increaseHealth the amount of health to be increased
     */

    public void increaseHealth(int increaseHealth) {
        this.health += increaseHealth;
        if (this.health >= 100) {
            this.health = 100;
        }
    }

    /**
     * Resets the health of the tank to 100.
     */

    public void resetHealth() {
        this.health = 100;
    }

    /**
     * Gets the current health of the tank.
     *
     * @return current health of the tank
     */

    public int getHealth() {
        return this.health;
    }

    /**
     * Gets the color of the tank.
     *
     * @param app the application instance
     * @return an array of RGB color values
     */

    public int[] getColor(App app) {
        String fill_index = app.playerColours.getString(Character.toString(this.name));
        Random rand = new Random();
        int indexR;
        int indexG;
        int indexB;
        if (fill_index.equals("random")) {
            indexR = rand.nextInt(256);
            indexG = rand.nextInt(256);
            indexB = rand.nextInt(256);
        } else {
            indexR = Integer.parseInt(fill_index.split(",")[0]);
            indexG = Integer.parseInt(fill_index.split(",")[1]);
            indexB = Integer.parseInt(fill_index.split(",")[2]);
        }
        int[] index = new int[]{indexR, indexG, indexB};
        return index;
    }

    /**
     * Checks if the tank is dead.
     *
     * @return true if the tank's health is less than or equal to 0, false otherwise
     */

    public boolean isDead() {
        return (this.health <= 0);
    }

    /**
     * Checks if the tank has exploded.
     *
     * @return true if the tank has exploded, false otherwise
     */

    public boolean hasExploded() {
        return this.exploded;
    }
    
    /**
     * Gets the tank locations based on the level map and terrain height.
     *
     * @param levelMap A 2D array representing the level map.
     * @param terrainHeight the array that contains terrain heights
     * @return a hashmap of tank locations
     */

    public static HashMap<Character, Tank> tank_location(char[][] levelMap, int[] terrainHeight) {
        HashMap<Character, Tank> tank_map = new HashMap<>();
        ArrayList<Character> possible_name = new ArrayList<>();
        for (char c = 'A'; c <= 'I'; c++) {
            possible_name.add(c);
        }
        for (char c = '0'; c <= '9'; c++) {
            possible_name.add(c);
        }

        for (int col = 0; col < levelMap[0].length; col ++) {
            for (int row = 0; row < levelMap.length; row++) {
                if (possible_name.contains(levelMap[row][col])) {
                    char player_name = levelMap[row][col];
                    int tankColumn = 32 * col;
                    tank_map.put(player_name, new Tank(player_name, tankColumn, terrainHeight[tankColumn]));
                    break;
                }
            }
        }
        return tank_map;
    }

    /**
     * Gets the current fuel level of the tank.
     *
     * @return the current level of the tank
     */

    public int getFuel() {
        return this.fuel;
    } 
    
    /**
     * Resets the tank's attributes to the initial values.
     */

    public void reset() {
        this.fuel = 250;
        this.health = 100;
        this.power = 50;
        this.exploded = false;
    }

    
    /**
     * Increases the fuel of the tank by the specified amount.
     *
     * @param increaseFuel the amount to fuel to be increased
     */
    public void increaseFuel(int increaseFuel) {
        this.fuel += increaseFuel;
    }

    /**
     * Sets the power level of the tank.
     *
     * @param power the new power level
     */
    public void setPower(int power) {
        this.power = power;
    }

    /**
     * Increases the power level of the tank.
     */
    public void increasePower() {
        this.power += 1.2;
        if (this.power >= this.health) {
            this.power = this.health;
        }
    }

    /**
     * Decreases the power level of the tank.
     */
    public void decreasePower() {
        this.power -= 1.2;
        if (this.power <= 1) {
            this.power = 1;
        }
    }
    

    /**
     * Fires a projectile from the tank with the given power level.
     * @param powerLevel the power level of projectile
     * @return the fired projectile
     */
    public Projectile fireProjectile(int powerLevel) {
        // Calculate initial velocity based on turret vector and power level
        float angle = (float) rotateAngle;
        float magnitude = 2 * Math.min(powerLevel, health) / 100.0f * 9 + 1;

        float vx = magnitude * (float) Math.cos(Math.PI/2 - angle);
        float vy = magnitude * (float) Math.sin(Math.PI/2 - angle);

        updateTurretPosition();
        Projectile projectile = new Projectile(getName(), turretTopX, turretTopY, vx, vy, isLargerExplosion);
        return projectile;


    }

    /**
     * Checks if the tank is falling or not.
     * @param terrainHeight the array that contains terrain heights
     * @return true if the tank is falling, false otherwise
     */
    public boolean isFalling(int[] terrainHeight) {
        if (this.y < terrainHeight[this.x]) {
            return true;
        }
        return false;
    }


    /**
     * Calculates the damage from the explosion to the tank.
     * @param explosion the explosion
     * @return the damage to the tank
     */
    public int calculateDamage(Explosion explosion) {
        int maxDamage = 60;
        int explosionX = explosion.getX();
        int explosionY = explosion.getY();
        int distance = (int) Math.sqrt(Math.pow(explosionX - this.x, 2) + Math.pow(explosionY - this.y, 2));
        if (distance <= explosion.getRadius()) {
            int damage = (int) ((1 - ((float) distance/explosion.getRadius())) * maxDamage);
            return damage;
        }
        else {
            return 0;
        }
    }

    /**
     * Creates an explosion at the tank's location.
     * @param explosionRadius the radius of the explosion
     * @return the new explosion
     */

    public Explosion explode(int explosionRadius) {
        Explosion explosion = new Explosion((int) x, (int) y, explosionRadius);
        exploded = true;
        return explosion;
    }

    /**
     * Updates the tank's score based on the damage.
     * @param damage the damage dealt
     */
    public void updateScore(int damage) {
        this.score += damage;
    }
    
    /**
     * Draws the tank and its turret, and handles falling.
     * @param app the application instance
     */
    public void draw(App app) {
        
        //tank's bottom
        app.rect(x-10, y - 10, 20, 5);
        //tank's top
        app.rect(x-5, y - 15, 10, 5);

        app.strokeWeight(3);
        // app.rotate((float) rotate_angle);
        
        //tank's turret
        app.pushMatrix(); // Save the current transformation matrix
        app.translate(x, y - 15); // Translate to the turret's position
    
        app.strokeWeight(3);
        app.rotate((float) rotateAngle); // Rotate the turret
    
        // Draw the tank's turret
        app.stroke(0, 0, 0);
        app.line(0, 0, 0, -15); // Example line for turret
    
        app.popMatrix(); // Restore the original transformation matrix

        if (isFalling(app.terrainHeight)) {
            if (this.parachute > 0) {
                app.parachute.resize(40, 0);
                app.image(app.parachute, x - 20, y - 50);
                this.y += 2;
                if (this.y == app.terrainHeight[this.x] - 2) {
                    this.parachute -= 1;
                }
            }
            else {
                this.y += 4;
                this.lostHealth(4);
            }
        }
    
    
    }


    
}
