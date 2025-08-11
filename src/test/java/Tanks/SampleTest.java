package Tanks;


import processing.core.PApplet;
import processing.core.PConstants;
import processing.event.KeyEvent;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

public class SampleTest {


    // Test loading a level map from the specified file path

    @Test
    public void testLoadLevel() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        char[][] levelMap = app.loadLevel("level1.txt");
        assertNotNull(levelMap);
        assertEquals('A', levelMap[11][2]);
        assertEquals(' ', levelMap[0][0]);
        app.noLoop();
    }

    // Test loading a level map file that does not exist
    @Test
    public void testLoadNonExistentLevel() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        char[][] levelMap = app.loadLevel("non-existent.txt");
        assertNull(levelMap);
        app.noLoop();
    }
    

    // Test switching between players' turns correctly
    @Test
    public void testSwitchTurn() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        char currentPlayer = app.currentPlayer;
        char nextPlayer = app.switchTurn();
        assertNotEquals(currentPlayer, nextPlayer);
        app.noLoop();

    }

    // Test switching turn when a player is dead
    @Test
    public void testSwitchTurnWhenAPlayerIsDead() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        Tank tankA = app.tankHashmap.get('A');
        Tank tankB = app.tankHashmap.get('B');
        tankB.lostHealth(100);
        
        char nextPlayer = app.switchTurn();
        assertNotEquals(nextPlayer, 'B');
        app.noLoop();

    }

    // Test tank cannot move when falling
    @Test
    public void testMovingWhenFalling() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        Tank tank = app.tankHashmap.get('A');
        int initialX = tank.getX();
        for (int i = tank.getX() - 4; i <= tank.getX() + 4; i++) {
            app.terrainHeight[i] = App.HEIGHT;
        }
        
        KeyEvent leftKey = new KeyEvent(null, 0, 0, 0, ' ', 37);
        app.keyPressed(leftKey);
        assertEquals(initialX, tank.getX());

        KeyEvent rightKey = new KeyEvent(null, 0, 0, 0, ' ', 39);
        app.keyPressed(rightKey);
        assertEquals(initialX, tank.getX());
        
        KeyEvent upKey = new KeyEvent(null, 0, 0, 0, ' ', 38);
        app.keyPressed(upKey);
        assertEquals(tank.getRotateAngle(), 0);

        KeyEvent downKey = new KeyEvent(null, 0, 0, 0, ' ', 40);
        app.keyPressed(downKey);
        assertEquals(tank.getRotateAngle(), 0);

        app.noLoop();
    }

    // Test moving tank left
    @Test
    public void testMoveLeft() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        Tank tank = app.tankHashmap.get('A');
        int initialX = tank.getX();
        tank.moveLeft(app.terrainHeight);
        assertEquals(initialX - 1, tank.getX());
        app.noLoop();
    }

    // Test cannot move left if out of fuel
    @Test
    public void testMoveLeftOutOfFuel() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        Tank tank = app.tankHashmap.get('A');
        tank.increaseFuel(-250);
        int initialX = tank.getX();
        tank.moveLeft(app.terrainHeight);
        assertEquals(initialX, tank.getX());
        app.noLoop();
    }

    // Test moving tank right
    @Test
    public void testMoveRight() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        Tank tank = app.tankHashmap.get('A');
        int initialX = tank.getX();
        tank.moveRight(app.terrainHeight);
        assertEquals(initialX + 1, tank.getX());
        app.noLoop();
    }

    // Test cannot move right if out of fuel
    @Test
    public void testMoveRightOutOfFuel() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        Tank tank = app.tankHashmap.get('A');
        tank.increaseFuel(-250);
        int initialX = tank.getX();
        tank.moveRight(app.terrainHeight);
        assertEquals(initialX, tank.getX());
        app.noLoop();
    }

    // Test rotating turret left
    @Test
    public void testMoveTurretLeft() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        Tank tank = app.tankHashmap.get('A');
        double initialAngle = tank.getRotateAngle();
        tank.moveTurretLeft();
        assertTrue(tank.getRotateAngle() < initialAngle);
        app.noLoop();
    }

    // Test rotating turret right
    @Test
    public void testMoveTurretRight() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        Tank tank = app.tankHashmap.get('A');
        double initialAngle = tank.getRotateAngle();
        tank.moveTurretRight();
        assertTrue(tank.getRotateAngle() > initialAngle);
        app.noLoop();
    }


    // Test increasing power
    @Test
    public void testIncreasePower() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        Tank tank = app.tankHashmap.get('A');
        int initialPower = tank.getPower();
        tank.increasePower();
        assertTrue(tank.getPower() > initialPower);
        app.noLoop();
    }

    // Test power increase has an upper bound
    @Test
    public void testIncreasePowerwithBoundary() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        Tank tank = app.tankHashmap.get('A');
        tank.setPower(tank.getHealth());
        tank.increasePower();
        assertTrue(tank.getPower() == tank.getHealth());
        app.noLoop();
    }

    // Test power decrease has a lower bound
    @Test
    public void testDecreasePowerwithBoundary() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        Tank tank = app.tankHashmap.get('A');
        tank.setPower(1);
        tank.decreasePower();
        assertTrue(tank.getPower() == 1);
        app.noLoop();
    }

    // Test health decrease has a lower bound of 0
    @Test
    public void testLostHealthWithBoundary() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        Tank tank = app.tankHashmap.get('A');
        tank.lostHealth(200);
        assertTrue(tank.getHealth() == 0);
        app.noLoop();
    }

    
    // Test resetting health
    @Test
    public void resetHealth() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        Tank tank = app.tankHashmap.get('A');
        tank.lostHealth(50);
        tank.resetHealth();
        assertTrue(tank.getHealth() == 100);
        app.noLoop();
    }

    // Test decreasing power
    @Test
    public void testDecreasePower() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        Tank tank = app.tankHashmap.get('A');
        int initialPower = tank.getPower();
        tank.decreasePower();
        assertTrue(tank.getPower() < initialPower);
        app.noLoop();
    }

    // Test firing a projectile
    @Test
    public void testFireProjectile() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        Tank tank = app.tankHashmap.get('A');
        Projectile projectile = tank.fireProjectile(50);
        assertNotNull(projectile);
        assertEquals('A', projectile.getName());
        app.noLoop();
    }

    // Test updating projectile position
    @Test
    public void testUpdateProjectile() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        Tank tank = app.tankHashmap.get('A');
        Projectile projectile = tank.fireProjectile(50);
        float initialX = projectile.getX();
        float initialY = projectile.getY();
        projectile.update(0);
        assertEquals(initialX, projectile.getX());
        assertNotEquals(initialY, projectile.getY());
        for (int i = 0; i < App.FPS*3; i++) {
            projectile.draw(app);
        }
        app.noLoop();
    }

    // Test projectile terrain collision
    @Test
    public void testCheckTerrainCollision() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        Tank tank = app.tankHashmap.get('A');
        Projectile projectile = tank.fireProjectile(50);        
        assertFalse(projectile.checkTerrainCollision(app.terrainHeight));
        app.noLoop();
    }   


    // Test detecting out of bounds projectiles
    @Test
    public void testProjectileOutOfMap() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        app.terrainHeight[0] = App.HEIGHT;
        Projectile p1 = new Projectile('A', 0, 5, 0, 0, false);   
        Projectile p2 = new Projectile('A', 5, 640, 0, 0, false);      
        Projectile p3 = new Projectile('A', 50, 50, 0, 0, false);
        app.draw();      
        assertTrue(p1.isOutOfMap());
        assertTrue(p2.isOutOfMap());
        assertFalse(p3.isOutOfMap());
        app.noLoop();
    }

    // Test projectile collision with terrain at ground
    @Test
    public void testCheckTerrainCollisionAtGround() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        int x = 100;
        int y = app.terrainHeight[x];
        Projectile projectile = new Projectile('A', x, y, 0, -10, false);
        assertTrue(projectile.checkTerrainCollision(app.terrainHeight));
        app.noLoop();
    }

    // Test terrain explosion effect
    @Test
    public void testTerrainExplosion() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        int[] initialTerrainHeight = app.terrainHeight.clone();
        Explosion explosion = new Explosion(363, app.terrainHeight[363], 30);
        for (int i = 0; i < App.FPS*3; i++) {
            explosion.update();
            explosion.draw(app);
        }
        int[] modifiedTerrainHeight = explosion.terrainExplosion(app.terrainHeight);
        assertNotEquals(initialTerrainHeight, modifiedTerrainHeight);
        app.noLoop();
    }

    // Test explosion shrinks over time
    @Test
    public void testExplosionProgress() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        Explosion explosion = new Explosion(50, 50, 30);
        for (int i = 0; i < App.FPS*3; i++) {
            explosion.update();
        }
        assertEquals(explosion.getProgress(), 1);
        app.noLoop();
    }


    // Test calculating damage
    @Test
    public void testCalculateDamage() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        Tank tank = app.tankHashmap.get('A');
        Explosion explosion = new Explosion(tank.getX(), tank.getY(), 30);
        int damage = tank.calculateDamage(explosion);
        assertTrue(damage > 0);
        app.noLoop();
    }

    // Test no damage if out of bounds
    @Test
    public void testCalculateDamageOutOfBounds() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        Tank tank = app.tankHashmap.get('A');
        Explosion explosion = new Explosion(0, 0, 30);
        int damage = tank.calculateDamage(explosion);
        assertTrue(damage == 0);
        app.noLoop();
    }

    // Test get color for a tank
    @Test
    public void testTankColor() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();

        Tank tank = new Tank('G', 10, app.terrainHeight[10]);
        int[] tankColor = tank.getColor(app);
        assertTrue(tankColor.length == 3);
        assertTrue(tankColor[0] <= 256 && tankColor[0] >= 0);
        assertTrue(tankColor[1] <= 256 && tankColor[1] >= 0);
        assertTrue(tankColor[2] <= 256 && tankColor[2] >= 0);
    }

    // Test tank explosion when dead
    @Test
    public void testTankExplosion() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        Tank tank = app.tankHashmap.get('A');
        tank.lostHealth(100);
        Explosion explosion = tank.explode(15);
        assertNotNull(explosion);
        assertTrue(tank.hasExploded());
        app.noLoop();
    }

    // Test tank explosion if falling out of bounds
    @Test
    public void testTankExplosionWhenOutOfBounds() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        app.delay(1000);
        // Set up a tank
        Tank tank = app.tankHashmap.get('A');
        tank.setY(App.HEIGHT - 2);
        app.terrainHeight[tank.getX()] = App.HEIGHT;
        app.delay(1000);
        assertTrue(tank.hasExploded());
        app.noLoop();
    }

    // Test health increase has an upper bound
    @Test
    public void testIncreaseHealthOutOfBounds() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        Tank tank = app.tankHashmap.get('A');
        tank.increaseHealth(100);
        assertEquals(tank.getHealth(), 100);
        app.noLoop();

    }

    // Test key not supported has no effect
    @Test
    public void testKeyNotSupported() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        // Set up a tank
        Tank tank = app.tankHashmap.get('A');
        int tankX = tank.getX();
        int tankY = tank.getY();

        // Simulate left key press
        KeyEvent j = new KeyEvent(null, 0, 0, 0, 'j', 74);

        app.keyPressed(j);


        // Assert tank position not changed
        assertEquals(tank.getX(), tankX);
        assertEquals(tank.getY(), tankY);
        app.noLoop();
    }

    // Test right key moves tank left
    @Test
    public void testKeyMoveLeft() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        Tank tank = app.tankHashmap.get('A');
        int tankX = tank.getX();
        KeyEvent leftKey = new KeyEvent(null, 0, 0, 0, ' ', 37);
        app.keyPressed(leftKey);
        assertEquals(tank.getX(), tankX - 1);
        assertEquals(tank.getY(), app.terrainHeight[tank.getX()]);
        app.noLoop();
    }

    // Test right key moves tank right
    @Test
    public void testKeyMoveRight() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        Tank tank = app.tankHashmap.get('A');
        int tankX = tank.getX();
        KeyEvent rightKey = new KeyEvent(null, 0, 0, 0, ' ', 39);

        app.keyPressed(rightKey);

        assertEquals(tank.getX(), tankX + 1);
        assertEquals(tank.getY(), app.terrainHeight[tank.getX()]);
        app.noLoop();
    }

    // Test space key fires projectile
    @Test
    public void testKeyFire() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        app.projectileList = new ArrayList<>();

        KeyEvent space = new KeyEvent(null, 0, 0, 0, ' ', 32);
        app.keyPressed(space);

        // Assert projectile fired  
        assertEquals(app.projectileList.size(), 1);
        app.noLoop();
    }

    // Test r key resets health
    @Test
    public void testKeyIncreaseHealth() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        Tank tank = app.tankHashmap.get('A');
        tank.updateScore(50);
        tank.lostHealth(40);

        KeyEvent r = new KeyEvent(null, 0, 0, 0, 'r', 82);
        app.keyPressed(r);

        assertEquals(tank.getHealth(), 80);
        assertEquals(tank.getScore(), 30);
        app.noLoop();

    }

    // Test f key increases fuel
    @Test
    public void testKeyIncreaseFuel() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        Tank tank = app.tankHashmap.get('A');
        tank.updateScore(50);

        KeyEvent f = new KeyEvent(null, 0, 0, 0, 'f', 70);
        app.keyPressed(f);
        assertEquals(tank.getFuel(), 450);
        assertEquals(tank.getScore(), 40);
        app.noLoop();
    }

    // Test keys increase power
    @Test
    public void testKeyIncreasePower() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        Tank tank = app.tankHashmap.get('A');


        KeyEvent w = new KeyEvent(null, 0, 0, 0, 'w', 87);
        app.keyPressed(w);

        assertEquals(tank.getPower(), 51);
        app.noLoop();
    }

     // Test keys decrease power
    @Test
    public void testKeyDecreasePower() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        Tank tank = app.tankHashmap.get('A');


        KeyEvent s = new KeyEvent(null, 0, 0, 0, 's', 83);

        app.keyPressed(s);

        assertEquals(tank.getPower(), 48);
        app.noLoop();
    }

    // Test keys rotate turret left
    @Test
    public void testKeyMoveTurretLeft() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();

        Tank tank = app.tankHashmap.get('A');

        KeyEvent upKey = new KeyEvent(null, 0, 0, 0, ' ', 38);


        app.keyPressed(upKey);
        app.delay(1000);

        assertEquals(tank.getRotateAngle(), (double) -Math.PI/18);
        app.noLoop();

    }

    // Test keys rotate turret right
    @Test
    public void testKeyMoveTurretRight() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();

        Tank tank = app.tankHashmap.get('A');


        KeyEvent downKey = new KeyEvent(null, 0, 0, 0, ' ', 40);


        app.keyPressed(downKey);
        app.delay(1000);

        assertEquals(tank.getRotateAngle(), (double) Math.PI/18);
        app.noLoop();
    }

    //Test key uses parachute
    @Test
    public void testKeyParachute() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();

        Tank tank = app.tankHashmap.get('A');

        tank.updateScore(15);

        KeyEvent p = new KeyEvent(null, 0, 0, 0, 'p', 80);


        app.keyPressed(p);

        assertEquals(tank.getScore(), 0);
        assertEquals(tank.getParachute(), 4);
        app.noLoop();

    }

    //Test key after level ends loads next
    @Test
    public void testKeyLevelEnd() {

        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();

        app.levelEnd = true;

        
        KeyEvent space = new KeyEvent(null, 0, 0, 0, ' ', 32);

        app.keyPressed(space);


        assertEquals(app.levelIndex, 1);
        assertFalse(app.levelEnd);
        app.noLoop();
    }

    // Test r key restarts from begining on game over
    @Test
    public void testKeyRestartGame() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();

        app.gameOver = true;

        KeyEvent r = new KeyEvent(null, 0, 0, 0, 'r', 82);
        app.keyPressed(r);
        


        assertEquals(app.levelIndex, 0);
        assertFalse(app.gameOver);
        assertFalse(app.levelEnd);
        app.noLoop();
    }

    // Test x key enables large explosion

    @Test
    public void testKeyLargerExplosion() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();

        Tank tank = app.tankHashmap.get('A');

        tank.updateScore(20);

        KeyEvent x = new KeyEvent(null, 0, 0, 0, 'x', 88);


        app.keyPressed(x);

        Projectile p = tank.fireProjectile(50);

        assertEquals(tank.getScore(), 0);
        assertTrue(tank.getLargerProjectile());
        assertTrue(p.isLargerExplosion());
        app.noLoop();
    }

    // Test all tanks dead triggers level end
    @Test
    public void testLevelEnd() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();

        Tank tankA = app.tankHashmap.get('A');
        Tank tankB = app.tankHashmap.get('B');
        Tank tankC = app.tankHashmap.get('C');
        tankA.lostHealth(100);
        tankB.lostHealth(100);
        tankC.lostHealth(100);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        assertTrue(app.levelEnd);


    }

    // Test wait then auto progresses to next level
    @Test
    public void testTimeLevelEnd() {

        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        int currentLevelIndex = app.levelIndex;
        app.levelEnd = true;
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int nextLevelIndex = app.levelIndex;

        assertEquals(nextLevelIndex - currentLevelIndex, 1);
        assertFalse(app.levelEnd);
        app.noLoop();
    }

    // Test score updates correctly
    @Test
    public void testUpdateScore() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        Tank tank = app.tankHashmap.get('A');
        int initialScore = tank.getScore();
        tank.updateScore(10);
        assertEquals(initialScore + 10, tank.getScore());
        app.noLoop();
    }

    // Test moving average calculation
    @Test
    public void testMovingAverage() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        Terrain terrain = new Terrain("src/main/resources/Tanks/level1.txt", "255,255,255");
        int[] height = new int[896];
        for (int i = 0; i < 896; i++) {
            height[i] = i % 32;
        }
        int[] movingAverage = terrain.movingAverage(height);
        for (int i = 0; i < 896; i++) {
            assertEquals(15, movingAverage[i]);
        }
        app.noLoop();
    }

    // Test switching levels by index
    @Test
    public void testSwitchLevel() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        app.switchLevel(1);
        assertNotEquals(app.currentLevel, app.levels.getJSONObject(0));
        app.noLoop();
    }

    // Test turret position
    @Test
    public void testTankTurretPosition() {

        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        Tank tank = app.tankHashmap.get('A');
        float turretX = tank.getTurretTopX();
        float turretY = tank.getTurretTopY();
        
        assertTrue(turretX == tank.getX());
        assertTrue(turretY == tank.getY() - 30);

    }

    // Test falling lost parachute
    @Test
    public void testTankFallingWithParachute() {

        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        Tank tank = app.tankHashmap.get('A');
        for (int i = tank.getX() - 4; i <= tank.getX() + 4; i++){
            app.terrainHeight[i] += 8;
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(tank.getParachute() < 3);
        app.noLoop();

    }

    // Test tank falling without parachute

    @Test
    public void testTankFallingWithoutParachute() {

        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        Tank tank = app.tankHashmap.get('A');
        tank.increaseParachute(-3);
        app.terrainHeight[tank.getX()] += 2;
        app.delay(1000);
        assertTrue(tank.getHealth() < 100);
        app.noLoop();

    }

    // Test invalid level index causes game over
    @Test
    public void testSwitchLevelInvalidIndex() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        app.switchLevel(5);
        assertTrue(app.gameOver);
    }

    // Test sorting scores
    @Test
    public void testSortedScore() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        Tank tank1 = new Tank('A', 0, 0);
        Tank tank2 = new Tank('B', 0, 0);
        Tank tank3 = new Tank('C', 0, 0);
        tank1.updateScore(100);
        tank2.updateScore(50);
        tank3.updateScore(80);
        app.tankList.clear();
        app.tankList.add(tank1);
        app.tankList.add(tank2);
        app.tankList.add(tank3);
        ArrayList<Tank> sortedScore = app.sortedScore();
        assertEquals(tank1, sortedScore.get(0));
        assertEquals(tank3, sortedScore.get(1));
        assertEquals(tank2, sortedScore.get(2));
        app.noLoop();
    }

    // Test initial wind speed in range
    @Test
    public void testInitialWindSpeedRange() {
        Wind wind = new Wind();
        int windSpeed = wind.getSpeed();
        assertTrue(windSpeed >= -35 && windSpeed <= 35);
    }

    // Test update wind speed stays in valid range
    @Test
    public void testUpdateWindSpeedWithinRange() {
        Wind wind = new Wind();
        int initialWindSpeed = wind.getSpeed();
        wind.update();
        int windSpeed = wind.getSpeed();
        assertTrue(Math.abs(windSpeed - initialWindSpeed) <= 5);
    }


}
