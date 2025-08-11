package Tanks;

import org.checkerframework.checker.units.qual.A;
import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.awt.*;


import java.io.*;
import java.util.*;

/**
 * The main class for the Tank game. This class extends PApplet and handles the setup, drawing, and user input for the game.
 */

public class App extends PApplet {

    public static final int CELLSIZE = 32; //8;
    public static final int CELLHEIGHT = 32;

    public static final int CELLAVG = 32;
    public static final int TOPBAR = 0;
    public static int WIDTH = 864; //CELLSIZE*BOARD_WIDTH;
    public static int HEIGHT = 640; //BOARD_HEIGHT*CELLSIZE+TOPBAR;
    public static final int BOARD_WIDTH = 28;
    public static final int BOARD_HEIGHT = 20;

    public static final int FPS = 30;

    String configPath;
    JSONObject json;
    JSONArray levels;
    JSONObject currentLevel;
    JSONObject playerColours;
    int levelIndex = 0;
    String layout;
    String background;
    String trees;
    String foreground_colour;
    PImage treeImage;
    PImage backgroundImage;
    PImage windLeft;
    PImage windRight;
    PImage fuelImage;
    PImage parachute;
    int[] terrainHeight;
    HashMap<Character, Tank> tankHashmap;
    static Random random = new Random();
    Terrain map;
    ArrayList<Tank> tankList;
    int currentPlayerTurn = 0;

    ArrayList<Character> playerList;
    char currentPlayer;
    ArrayList<Projectile> projectileList;
    Projectile current_projectile;
    ArrayList<Explosion> explosionList;
    Explosion explosion;
    Wind wind;

    int currentScoreIndex = 0;
    int lastScoreTime = 0;
    int levelEndTime = 0;
    int arrowStartTime = 0;
    boolean gameOver = false;
    boolean levelEnd = false;
    boolean displayArrow = true;


    public App() {
        this.configPath = "config.json";
    }

    /**
     * Initialise the setting of the window size.
     */
	@Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player and map elements.
     */
	@Override
    public void setup() {
        frameRate(FPS);
		//See PApplet javadoc:
		json = loadJSONObject(configPath);
        levels = json.getJSONArray("levels");
        playerColours = json.getJSONObject("player_colours");
        currentLevel = levels.getJSONObject(levelIndex);
        layout = currentLevel.getString("layout");
        background = currentLevel.getString("background");
        foreground_colour = currentLevel.getString("foreground-colour");
        trees = currentLevel.getString("trees");
        
        map = new Terrain(layout, foreground_colour);
        if (trees != null) {
            this.treeImage = this.loadImage("src/main/resources/Tanks/" + trees);
        }
        else {
            this.treeImage = null;
        }
        this.backgroundImage = this.loadImage("src/main/resources/Tanks/" + background);
        this.windLeft = this.loadImage("src/main/resources/Tanks/wind-1.png");
        this.windRight = this.loadImage("src/main/resources/Tanks/wind.png");
        this.fuelImage = this.loadImage("src/main/resources/Tanks/fuel.png");
        this.parachute = this.loadImage("src/main/resources/Tanks/parachute.png");
        char[][] levelMap = loadLevel(layout);      
        terrainHeight = map.movingAverage(map.movingAverage(map.getHeight(levelMap)));
        tankHashmap = Tank.tank_location(levelMap, terrainHeight);
        playerList = new ArrayList<Character>(tankHashmap.keySet());
        projectileList = new ArrayList<>();
        explosionList = new ArrayList<>();
        currentPlayer = playerList.get(currentPlayerTurn);
        wind = new Wind();
        tankList = new ArrayList<>();
        for (HashMap.Entry<Character, Tank> tank_value : tankHashmap.entrySet()) {
            Tank tank = tank_value.getValue();
            tankList.add(tank);
        }

    }

    /**
     * Switch to the next player's turn.
     * @return The character representing the next player.
     */


    public char switchTurn() {
        currentPlayerTurn = (currentPlayerTurn + 1) % tankHashmap.size();
        char nextPlayer = playerList.get(currentPlayerTurn);
        while (tankHashmap.get(nextPlayer).isDead()) {
            currentPlayerTurn = (currentPlayerTurn + 1) % tankHashmap.size();
            nextPlayer = playerList.get(currentPlayerTurn);
        }
        displayArrow = true;
        arrowStartTime = millis();
        return nextPlayer;
    }

    /**
     * Load a level from a file.
     * @param filename The name of the file to load.
     * @return A 2D array representing the level map.
     */

    public char[][] loadLevel(String filename) {
        try {
            File f = new File(filename);
            Scanner scan = new Scanner(f);
            char[][] levelMap;
            int row = 0;
            levelMap = new char[BOARD_HEIGHT][BOARD_WIDTH];
            while (scan.hasNextLine() && row < BOARD_HEIGHT) {
                String line = scan.nextLine(); 
                for (int col = 0; col < BOARD_WIDTH; col ++) {
                    try {
                        
                        levelMap[row][col] = line.charAt(col);
                       
                    }
                    catch (StringIndexOutOfBoundsException e) {
                        levelMap[row][col] = ' ';
                    }
                }
                row += 1;
                
            }
            return levelMap;
        }
        catch(FileNotFoundException e) {
            System.out.println("File not Found");
            return null;
        }
    }

    /**
     * Switch to the next level.
     * @param levelIndex The index of the next level.
     */

    public void switchLevel(int levelIndex) {
        
        if (levelIndex >= 3) {
            this.gameOver = true;
            return;
        }
        for (Tank tank : tankList) { 
            tank.reset(); 
            if (levelIndex == 0) {
                tank.resetScore();
                tank.resetParachute();
            }
        }

        currentLevel = levels.getJSONObject(levelIndex);
        layout = currentLevel.getString("layout");
        background = currentLevel.getString("background");
        foreground_colour = currentLevel.getString("foreground-colour");
        trees = currentLevel.getString("trees");
        
        map = new Terrain(layout, foreground_colour);
        
        if (trees != null) {
            this.treeImage = this.loadImage("src/main/resources/Tanks/" + trees);
        }
        else {
            this.treeImage = null;
        }        
        
        this.backgroundImage = this.loadImage("src/main/resources/Tanks/" + background);
        

        char[][] levelMap = loadLevel(layout);      
        terrainHeight = map.movingAverage(map.movingAverage(map.getHeight(levelMap)));
        HashMap<Character, Tank> nextTankHashmap = Tank.tank_location(levelMap, terrainHeight);
        for (Character c : nextTankHashmap.keySet()) {
            Tank newTank = nextTankHashmap.get(c);
            for (Tank tank : tankList) {
                if (tank.getName() == c){
                    tank.setLocation(newTank.getX(), newTank.getY());
                }
            }
        }
        
        projectileList.clear();
        explosionList.clear();
    }

    /**
     * Sort the players based on their scores.
     * @return A list of players sorted by their scores.
     */

    public ArrayList<Tank> sortedScore() {
        ArrayList<Tank> sortedScore = new ArrayList<>(tankList);
        for (int i = 0; i < sortedScore.size() - 1; i++) {
            for (int j = i + 1; j < sortedScore.size(); j++) {
                Tank player1 = sortedScore.get(i);
                Tank player2 = sortedScore.get(j);
                int score1 = player1.getScore();
                int score2 = player2.getScore();
                if (score1 < score2) {
                    sortedScore.set(i, player2);
                    sortedScore.set(j, player1);
                }
            }
        }
                
        return sortedScore;
    }
    
        


    /**
     * Receive key pressed signal from the keyboard.
     * @param event The key event.
     */
	@Override
    public void keyPressed(KeyEvent event){

        Tank tank = tankHashmap.get(currentPlayer);
        int pressedKey = event.getKeyCode();
        if (!gameOver) {
            if (pressedKey == LEFT && !tank.isFalling(terrainHeight)) {
                tank.moveLeft(terrainHeight);

            }

            else if (pressedKey == RIGHT && !tank.isFalling(terrainHeight)) {
                tank.moveRight(terrainHeight);

            }
            else if (pressedKey == UP && !tank.isFalling(terrainHeight)) {
                tank.moveTurretLeft();
                
            }
            else if (pressedKey == DOWN && !tank.isFalling(terrainHeight)) {
                tank.moveTurretRight();
            }
            else if (pressedKey == 'W') {
                tank.increasePower();
            }
            else if (pressedKey == 'S') {
                tank.decreasePower();
            }
            else if (pressedKey == 'R') {
                
                if (tank.getScore() >= 20) {
                    tank.increaseHealth(20);
                    tank.lostScore(20);
                }
            }
            else if (pressedKey == 'F') {
                if (tank.getScore() >= 10) {
                    tank.increaseFuel(200);
                    tank.lostScore(10);
                }
            }
            else if (pressedKey == 'P') {
                if (tank.getScore() >= 15) {
                    tank.increaseParachute(1);
                    tank.lostScore(15);
                }
            }
            else if (pressedKey == 'X') {
                if (tank.getScore() >= 20) {
                    tank.largerProjectile();
                    tank.lostScore(20);
                }
            }

            

            if (pressedKey == ' ') {
                if (!tank.isDead() && !tank.isFalling(terrainHeight)) {
                    current_projectile = tank.fireProjectile(tank.getPower());
                    currentPlayer = switchTurn();
                    wind.update();
                    projectileList.add(current_projectile);
                }
                if (levelEnd) {
                    levelIndex += 1;
                    switchLevel(levelIndex);
                    levelEnd = false;
                }
            }
        }
        else {
            if (pressedKey == 'R') {
                gameOver = false;
                levelEnd = false;
                levelIndex = 0;
                switchLevel(levelIndex);
            }
        }


        
    }

    

    /**
     * Receive key released signal from the keyboard.
     */
	@Override
    public void keyReleased(){
        
    }

     /**
     * Receive mouse pressed signal from the mouse.
     * @param e The mouse event.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        //TODO - powerups, like repair and extra fuel and teleport
    }

    /**
     * Receive mouse released signal from the mouse.
     * @param e The mouse event.
     */
    @Override
    public void mouseReleased(MouseEvent e) {

    }
    

    /**
     * Draw all elements in the game by current frame.
     */
	@Override
    public void draw() {
        
        map.draw(this);
        
        for (Tank tank : tankList) {
            int[] fill_index = tank.getColor(this);
            if (!tank.isDead()) {
                int index_1 = fill_index[0];
                int index_2 = fill_index[1];
                int index_3 = fill_index[2];
                fill(index_1, index_2, index_3);
                stroke(index_1, index_2, index_3);
                tank.draw(this);
            }
        }
        
        for (int i = 0; i < projectileList.size(); i++) {
            Projectile current_projectile = projectileList.get(i);
            if (current_projectile != null) {
                current_projectile.update(wind.getSpeed());
                current_projectile.draw(this);
                
                if (!current_projectile.isOutOfMap()) {
                    if (current_projectile.checkTerrainCollision(terrainHeight)) {
                        if (current_projectile.isLargerExplosion()) {
                            explosion = new Explosion(current_projectile.getX(), current_projectile.getY(), 60);
                        }
                        else {
                            explosion = new Explosion(current_projectile.getX(), current_projectile.getY(), 30);
                        }
                        terrainHeight = explosion.terrainExplosion(terrainHeight);
                        for (Tank tank : tankList){
                            int damage = tank.calculateDamage(explosion);
                            if (damage > 0) {
                                tank.lostHealth(damage);
                                char firedTankName = current_projectile.getName();
                                Tank firedTank = tankHashmap.get(firedTankName);
                                if (tank != firedTank) {
                                    firedTank.updateScore(damage);
                                }
                            }
                        }
                        
                        explosionList.add(explosion);
                        projectileList.remove(i);
                        i--; 
                    }
            }
                else if (current_projectile.isOutOfMap()) {
                    projectileList.remove(i);
                }
            }

        }

        for (Tank tank : tankList) {
            if (tank.isDead() && !tank.hasExploded()) {
                Explosion tankExplosion = tank.explode(15);
                explosionList.add(tankExplosion);
                
            }
            else if (tank.getY() >= HEIGHT && !tank.hasExploded()) {
                Explosion tankExplosion = tank.explode(30);
                explosionList.add(tankExplosion);
                tank.lostHealth(tank.getHealth());
                
            }
        }

        Iterator<Explosion> explosionIterator = explosionList.iterator();
        while (explosionIterator.hasNext()) {
            Explosion explosion = explosionIterator.next();
            explosion.update();
            explosion.draw(this);
                    
            if (explosion.getProgress() >= 1.0f) {
                explosionIterator.remove(); 
            }
        }
        

        

        wind.draw(this);

        Tank currentTank = tankHashmap.get(currentPlayer);
        if (currentTank != null & !currentTank.isDead()) {
            fuelImage.resize(30, 0);
            image(fuelImage, 130, 10);
            fill(0, 0, 0);
            textSize(16.0f);
            text(currentTank.getFuel(), 170, 35);
            text("Player " + currentPlayer + "'s turn", 10, 35);
            parachute.resize(40, 0);
            image(parachute, 125, 45);
            text(currentTank.getParachute(), 170, 65);
            text("Power: " + currentTank.getPower(), 240, 70);
            text("Health: ", 240, 35);
            text(currentTank.getHealth(), 520, 35);
            int[] colourIndex = currentTank.getColor(this);
            fill(colourIndex[0], colourIndex[1], colourIndex[2]);
            noStroke();
            rect(300, 15, currentTank.getHealth() * 2, 30);
            strokeWeight(4);
            stroke(0, 0, 0);
            noFill();
            rect(300, 15, 200, 30);
            strokeWeight(4);
            stroke(128, 128, 128); 
            rect(300, 15, currentTank.getPower() * 2, 30);
            strokeWeight(2);
            stroke(0, 0, 0);
            line(300 + 2 * currentTank.getPower(), 10, 300 + 2 * currentTank.getPower(), 50);
            if (displayArrow) {
                if (millis() - arrowStartTime < 2000) {
                    int x = currentTank.getX();
                    int y = currentTank.getY();
                    stroke(0);
                    line(x, y - 130, x, y - 90);
                    fill(0);
                    triangle(x, y - 80, x - 5, y - 90, x + 5, y - 90);
                }
                else {
                    displayArrow = false;
                }
            }
        }
        else {
            currentPlayer = switchTurn();
        }

        fill(0, 0, 0);
        text("POWERUPS", 10, 130);
        text("R - Repair kit (-20)", 10, 160);
        text("F - Additional fuel (-10)", 10, 190);
        text("P - Additional parachute (-15)", 10, 220);
        text("X - Larger projectile (-20)", 10, 250);

        noFill();
        stroke(0, 0, 0);
        rect(7, 110,250, 30);
        rect(7, 140, 250, 120);

        int deadCount = 0;
        for (Tank tank : tankList) {
            if (tank.isDead()) {
                deadCount += 1;
            }
        }
        if (deadCount >= tankList.size() - 1  && !levelEnd) {
            levelEnd = true;
            levelEndTime = millis();

        }

        if (levelEnd && millis() - levelEndTime >= 1000) {
            levelIndex += 1;
            switchLevel(levelIndex);
            levelEnd = false;
        }
        
        //----------------------------------
        //display scoreboard:
        //----------------------------------
        //TODO
        noFill();
        stroke(0, 0, 0);
        rect(WIDTH - 155, 70, 140, 30);
        rect(WIDTH - 155, 100, 140, 30 * playerList.size());
        text("Scores", WIDTH - 150, 90);
        for (char player : playerList) {
            Tank t = tankHashmap.get(player);
            int[] colourIndex = t.getColor(this);
            fill(colourIndex[0], colourIndex[1], colourIndex[2]);
            text("Player " + player, WIDTH - 150, 30 * (player - 'A' + 4));
            text(t.getScore(), WIDTH - 50, 30 * (player - 'A' + 4));
        }   


        if (gameOver) {
            ArrayList<Tank> sortedScore = sortedScore();
            stroke(0, 0, 0);
            fill(255, 102, 178);
            rect(300, 150, 300, 50);
            rect(300, 200, 300, 50 * sortedScore.size());  
            textSize(24.0f); 
            fill(0, 0, 0);  
            text("FINAL SCORES", 310, 180);   
            text("PRESS R TO RESTART THE GAME", 280, 250 + 50 * sortedScore.size());
            Tank winner = sortedScore.get(0);
            int[] winnerColor = winner.getColor(this);
            fill(winnerColor[0], winnerColor[1], winnerColor[2]);
            text("PLAYER " + winner.getName() + " WINS!", 310, 120);
            for (int i = 0; i < currentScoreIndex; i++){
                Tank tank = sortedScore.get(i);
                char name = tank.getName();
                int score = tank.getScore();
                int[] tankColor = tank.getColor(this);
                fill(tankColor[0], tankColor[1], tankColor[2]);
                text("Player " + name, 310, 230 + 50 * i);
                text(score, 525, 230 + 50 * i);
            }

            
            if (millis() - lastScoreTime >= 700 && currentScoreIndex < sortedScore.size()) {
                currentScoreIndex++;
                lastScoreTime = millis();
            }
        }

    }


    public static void main(String[] args) {
        PApplet.main("Tanks.App");
    }

}
