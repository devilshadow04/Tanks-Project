package Tanks;

import org.checkerframework.checker.units.qual.A;
import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import java.io.*;
import java.util.*;
import java.util.List;

public class App extends PApplet {

    public static final int CELLSIZE = 32; //8;
    public static final int CELLHEIGHT = 32;

    public static final int CELLAVG = 32;
    public static final int TOPBAR = 0;
    public static int WIDTH = 864; //CELLSIZE*BOARD_WIDTH;
    public static int HEIGHT = 640; //BOARD_HEIGHT*CELLSIZE+TOPBAR;
    public static final int BOARD_WIDTH = 28;
    public static final int BOARD_HEIGHT = 20;

    public static final int INITIAL_PARACHUTES = 1;

    public static final int FPS = 30;

    public String configPath;
    public JSONObject json;
    public JSONArray levels;
    public JSONObject currentLevel;
    public JSONObject playerColours;
    public int levelIndex = 0;
    public String layout;
    public String background;
    public String trees;
    public String foreground_colour;
    public PImage treeImage;
    public PImage backgroundImage;
    public PImage windLeft;
    public PImage windRight;
    public PImage fuelImage;
    public PImage parachute;
    public int[] terrainHeight;
    public HashMap<Character, Tank> tank_hashmap;
    public static Random random = new Random();
    public Terrain map;
    private ArrayList<Tank> tankList;
    private int currentPlayerTurn = 0;

    ArrayList<Character> playerList;
    private char currentPlayer;
    private ArrayList<Projectile> projectileList;
    private Projectile current_projectile;
    private ArrayList<Explosion> explosionList;
    private Explosion explosion;
    private Wind wind;

    int currentScoreIndex = 0;
    int lastScoreTime = 0;
    boolean gameOver = false;



	// Feel free to add any additional methods or attributes you want. Please put classes in different files.

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
		//loadImage(this.getClass().getResource(filename).getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
        char[][] levelMap = loadLevel(layout);      
        terrainHeight = map.moving_average(map.moving_average(map.getHeight(levelMap)));
        tank_hashmap = Tank.tank_location(levelMap, terrainHeight);
        playerList = new ArrayList<Character>(tank_hashmap.keySet());
        projectileList = new ArrayList<>();
        explosionList = new ArrayList<>();
        currentPlayer = playerList.get(currentPlayerTurn);
        wind = new Wind();
        tankList = new ArrayList<>();
        for (HashMap.Entry<Character, Tank> tank_value : tank_hashmap.entrySet()) {
            Tank tank = tank_value.getValue();
            tankList.add(tank);
        }
    }

    public char switchTurn() {
        currentPlayerTurn = (currentPlayerTurn + 1) % tank_hashmap.size();
        char nextPlayer = playerList.get(currentPlayerTurn);
        while (tank_hashmap.get(nextPlayer).isDead()) {
            currentPlayerTurn = (currentPlayerTurn + 1) % tank_hashmap.size();
            nextPlayer = playerList.get(currentPlayerTurn);
        }
        return nextPlayer;
    }

    //read level file
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
                        if (levelMap[row][col] == 'X') {
                            continue;
                        }
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



    public void switchLevel(int levelIndex) {
        
        
        if (levelIndex >= 3) {
            this.gameOver = true;
            return;
        }
        for (Tank tank : tankList) { 
            tank.reset(); 
            if (levelIndex == 0) {
                tank.resetScore();
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
        terrainHeight = map.moving_average(map.moving_average(map.getHeight(levelMap)));
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


    public ArrayList<Tank> sortedScore() {
        ArrayList<Tank> sortedScore = new ArrayList<>(tankList);
        // Sort the players based on their scores using a simple sorting algorithm
        for (int i = 0; i < sortedScore.size() - 1; i++) {
            for (int j = i + 1; j < sortedScore.size(); j++) {
                Tank player1 = sortedScore.get(i);
                Tank player2 = sortedScore.get(j);
                int score1 = player1.getScore();
                int score2 = player2.getScore();
                if (score1 < score2) {
                    // Swap players if player1's score is less than player2's score
                    sortedScore.set(i, player2);
                    sortedScore.set(j, player1);
                }
            }
        }
                
        return sortedScore;
    }
    
        


    /**
     * Receive key pressed signal from the keyboard.
     */
	@Override
    public void keyPressed(KeyEvent event){

        Tank tank = tank_hashmap.get(currentPlayer);
        if (!gameOver) {
            if (event.getKeyCode() == UP && !tank.isFalling(terrainHeight)) {
                tank.moveLeft(terrainHeight);
                if (tank.getY() >= HEIGHT && !tank.isDead()) {
                    Explosion tankExplosion = tank.explode(30);
                    explosionList.add(tankExplosion);
                    tank.lostHealth(tank.getHealth());
                }
            }

            else if (event.getKeyCode() == DOWN && !tank.isFalling(terrainHeight)) {
                tank.moveRight(terrainHeight);
                if (tank.getY() >= HEIGHT && !tank.isDead()) {
                    Explosion tankExplosion = tank.explode(30);
                    explosionList.add(tankExplosion);
                    tank.lostHealth(tank.getHealth());
                }
            }
            else if (event.getKeyCode() == LEFT && !tank.isFalling(terrainHeight)) {
                tank.moveTurretLeft();
                
            }
            else if (event.getKeyCode() == RIGHT && !tank.isFalling(terrainHeight)) {
                tank.moveTurretRight();
                
            }
            else if (event.getKeyCode() == 'W') {
                tank.increasePower();
            }
            else if (event.getKeyCode() == 'S') {
                tank.decreasePower();
            }
            else if (event.getKeyCode() == 'R') {
                
                if (tank.getScore() >= 20) {
                    tank.increaseHealth(20);
                    tank.lostScore(20);
                }
            }
            else if (event.getKeyCode() == 'F') {
                if (tank.getScore() >= 10) {
                    tank.increaseFuel(200);
                    tank.lostScore(10);
                }
            }
            else if (event.getKeyCode() == 'P') {
                if (tank.getScore() >= 15) {
                    tank.increaseParachute(1);
                    tank.lostScore(15);
                }
            }
            else if (event.getKeyCode() == 'X') {
                if (tank.getScore() >= 20) {
                    tank.largerProjectile();
                    tank.lostScore(20);
                }
            }

            

            if (event.getKeyCode() == ' ') {
                if (!tank.isDead()) {
                    current_projectile = tank.fireProjectile(tank.getPower());
                    currentPlayer = switchTurn();
                    wind.update();
                    projectileList.add(current_projectile);
                }
            }
        }
        else {
            if (event.getKeyCode() == 'R') {
                this.gameOver = false;
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

    @Override
    public void mousePressed(MouseEvent e) {
        //TODO - powerups, like repair and extra fuel and teleport

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }
    

    /**
     * Draw all elements in the game by current frame.
     */
	@Override
    public void draw() {
        
        //----------------------------------
        //display HUD:
        //----------------------------------
        //TODO
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
                            Tank firedTank = tank_hashmap.get(firedTankName);
                            if (tank != firedTank) {
                                firedTank.updateScore(damage);
                            }
                        }
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
                    
                    explosionList.add(explosion);
                    projectileList.remove(i);
                    i--; // Adjust index after removing the projectile
                }
            }

        }

        Iterator<Explosion> explosionIterator = explosionList.iterator();
        while (explosionIterator.hasNext()) {
            Explosion explosion = explosionIterator.next();
            explosion.update();
            explosion.draw(this);
                    
            if (explosion.getProgress() >= 1.0f) {
                explosionIterator.remove(); // Use iterator's remove method
            }
        }
        

        

        wind.draw(this);

        Tank currentTank = tank_hashmap.get(currentPlayer);
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
            stroke(0, 0, 0);
            noFill();
            rect(300, 15, 200, 30);
            fill(colourIndex[0], colourIndex[1], colourIndex[2]);
            rect(300, 15, currentTank.getHealth() * 2, 30);
            stroke(128, 128, 128); 
            rect(300, 15, currentTank.getPower() * 2, 30);
            stroke(255, 0, 0);
            line(300 + 2 * currentTank.getPower(), 15, 300 + 2 * currentTank.getPower(), 45);
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
        if (deadCount >= tankList.size() - 1) {
            levelIndex += 1;
            switchLevel(levelIndex);

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
            Tank t = tank_hashmap.get(player);
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
            text("Final Scores", 310, 180);   
            text("Press R to restart the game", 300, 250 + 50 * sortedScore.size());
            Tank winner = sortedScore.get(0);
            int[] winnerColor = winner.getColor(this);
            fill(winnerColor[0], winnerColor[1], winnerColor[2]);
            text("Player " + winner.getName() + " wins!", 310, 120);
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
        
		//----------------------------------
        //----------------------------------

        //TODO: Check user action

    }


    public static void main(String[] args) {
        PApplet.main("Tanks.App");
    }

}
