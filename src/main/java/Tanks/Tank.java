package Tanks;

import processing.core.PImage;
import java.util.*;

public class Tank {
    private int x;
    private int y;
    private double rotate_angle;
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



    public Tank(char name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.rotate_angle = 0;
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

    public char getName() {
        return this.name;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getScore() {
        return this.score;
    }

    public void resetScore() {
        this.score = 0;
    }

    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void lostScore(int lostScore) {
        this.score -= lostScore;
    }



    public void moveLeft(int[] moving_average) {
        if (this.fuel <= 0) {
            return;
        }
        if (this.x >= 0) {
            this.x -= 1;
            this.y = moving_average[this.x];
            this.fuel -= 1;
        }
    }

    public void moveRight(int[] moving_average) {
        if (this.fuel <= 0) {
            return;
        }
        if (this.x <= moving_average.length){
            this.x += 1;
            this.y = moving_average[this.x];
            this.fuel -=1;
        }
    }

    public void largerProjectile() {
        this.isLargerExplosion = true;
    }

    public void moveTurretLeft() {
        rotate_angle = Math.max(rotate_angle - Math.PI / 18, -Math.PI / 2); // Adjust the rotation angle with a lower bound of -π/2
        updateTurretPosition();
    }

    // Method to move the tank turret right
    public void moveTurretRight() {
        rotate_angle = Math.min(rotate_angle + Math.PI / 18, Math.PI / 2); // Adjust the rotation angle with an upper bound of π/2
        updateTurretPosition();
    }

    private void updateTurretPosition() {
        // Calculate the position of the top of the turret after rotation
        turretTopX = x + (float) Math.cos(Math.PI/2 - rotate_angle) * TURRET_LENGTH;
        turretTopY = y-15 - (float) Math.sin(Math.PI/2 - rotate_angle) * TURRET_LENGTH;
    }

    // Getter methods for turret coordinates
    public float getTurretTopX() {
        return turretTopX;
    }

    public float getTurretTopY() {
        return turretTopY;
    }
    
    public int getParachute() {
        return this.parachute;
    }

    public void increaseParachute(int n) {
        this.parachute += n;
    }

    public int getPower() {
        return this.power;
    }
    
    public void lostHealth(int damage) {
        this.health -= damage;
        if (this.health < 0) {
            this.health = 0;
        } 
        if (this.power >= this.health) {
            this.power = this.health;
        }
    }

    public void increaseHealth(int increaseHealth) {
        this.health += increaseHealth;
        if (this.health >= 100) {
            this.health = 100;
        }
    }

    public void resetHealth() {
        this.health = 100;
    }


    public int getHealth() {
        return this.health;
    }

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


    public boolean isDead() {
        return (this.health <= 0);
    }


    public boolean hasExploded() {
        return this.exploded;
    }
    
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

    public int getFuel() {
        return this.fuel;
    } 
    
    public void reset() {
        this.fuel = 250;
        this.health = 100;
        this.power = 50;
        this.exploded = false;
    }

    public void increaseFuel(int increaseFuel) {
        this.fuel += increaseFuel;
    }

    public void setPower(int power) {
        this.power = power;
    }
    
    public void increasePower() {
        this.power += 1.2;
        if (this.power >= this.health) {
            this.power = this.health;
        }
    }

    public void decreasePower() {
        this.power -= 1.2;
        if (this.power <= 1) {
            this.power = 1;
        }
    }
    

 
    public Projectile fireProjectile(int powerLevel) {
        // Calculate initial velocity based on turret vector and power level
        float angle = (float) rotate_angle;
        float magnitude = Math.min(powerLevel, health) / 100.0f * 9 + 1;

        float vx = magnitude * (float) Math.cos(Math.PI/2 - angle);
        float vy = magnitude * (float) Math.sin(Math.PI/2 - angle);

        updateTurretPosition();
        Projectile projectile = new Projectile(getName(), turretTopX, turretTopY, vx, vy, isLargerExplosion);
        return projectile;


    }

    public boolean isFalling(int[] terrainHeight) {
        if (this.y < terrainHeight[this.x]) {
            return true;
        }
        return false;
    }



    public int calculateDamage(Explosion explosion) {
        int maxDamage = 60;
        int explosionX = explosion.getX();
        int explosionY = explosion.getY();
        int distance = (int) Math.sqrt(Math.pow(explosionX - this.x, 2) + Math.pow(explosionY - this.y, 2));
        if (distance <= explosion.getRadius()) {
            int damage = (int) (1.0f - (distance/explosion.getRadius())) * maxDamage;
            return damage;
        }
        else {
            return 0;
        }
    }

    public Explosion explode(int explosionRadius) {
        // Create explosion
        Explosion explosion = new Explosion((int) x, (int) y, explosionRadius);
        exploded = true;
        return explosion;
    }

    public void updateScore(int damage) {
        this.score += damage;
    }
    

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
        app.rotate((float) rotate_angle); // Rotate the turret
    
        // Draw the tank's turret
        app.line(0, 0, 0, -15); // Example line for turret
    
        app.popMatrix(); // Restore the original transformation matrix

        if (isFalling(app.terrainHeight)) {
            if (this.parachute > 0) {
                app.parachute.resize(40, 0);
                app.image(app.parachute, x - 20, y - 50);
                this.y += 2;
                if (this.y == app.terrainHeight[this.x]) {
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
