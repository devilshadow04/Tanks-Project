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
    public JSONObject player_colours;
    public PImage tree;
    public PImage background;
    public char[][] pixelMap;
    public int[] moving_average;
    public HashMap<Character, Tank> tank_map;
    public static Random random = new Random();
    public Terrain map;
    private Tank tank;
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
        player_colours = json.getJSONObject("player_colours");
        this.tree = this.loadImage("src/main/resources/Tanks/tree2.png");
        this.background = this.loadImage("src/main/resources/Tanks/snow.png");
		//loadImage(this.getClass().getResource(filename).getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
        map = new Terrain();
        String filename = "level1.txt";
        char[][] levelMap = loadLevel(filename);      
        pixelMap = pixelMap(levelMap); 
        moving_average = map.moving_average(map.moving_average(map.getHeight(pixelMap)));
        tank_map = Tank.tank_location(pixelMap, moving_average);
        tank = tank_map.get('A');
        
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

    // extends levelMap from 28x20 to 896x20 (each column is one pixel)
    public char[][] pixelMap(char[][] levelMap) {
        pixelMap = new char[BOARD_HEIGHT][CELLSIZE * BOARD_WIDTH];
        for (int row = 0; row < BOARD_HEIGHT; row ++) {
            for (int col = 0; col < BOARD_WIDTH; col ++) {
                if (levelMap[row][col] == 'X') {
                    for (int j = 0; j < 32; j++){
                        pixelMap[row][32*col + j] = levelMap[row][col];
                    }
                    if (levelMap[row - 1][col] != 'X' && levelMap[row - 1][col] != ' ') {
                        pixelMap[row - 1][32 * col] = levelMap[row - 1][col];
                    }
                }
            }
        }
        return pixelMap;
    }



    /**
     * Receive key pressed signal from the keyboard.
     */
	@Override
    public void keyPressed(KeyEvent event){
        if (event.getKeyCode() == UP) {
            int[] coordinate = tank.moveLeft(moving_average);
            int new_x = coordinate[0];
            int new_y = coordinate[1];
            tank_map.put('A', new Tank(new_x, new_y));
        }

        else if (event.getKeyCode() == DOWN) {
            tank.moveRight(moving_average);
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

        map.draw(pixelMap, this);

        tank_map = Tank.tank_location(pixelMap, moving_average);
        for (HashMap.Entry<Character, Tank> tank_value : tank_map.entrySet()) {
            String player_name = Character.toString(tank_value.getKey());
            Tank tank = tank_value.getValue();
            String fill_index = player_colours.getString(player_name);
            int index_1 = Integer.parseInt(fill_index.split(",")[0]);
            int index_2 = Integer.parseInt(fill_index.split(",")[1]);
            int index_3 = Integer.parseInt(fill_index.split(",")[2]);
            fill(index_1, index_2, index_3);
            stroke(index_1, index_2, index_3);
            tank.draw(this);
        }
        // stroke(0);
        // strokeWeight(2);
        // noFill();
        // curveVertex(50, 500);
        // curveVertex(50, 500);
        // curveVertex(150, 450);
        // curveVertex(50, 500);



        
        //----------------------------------
        //display scoreboard:
        //----------------------------------
        //TODO
        
		//----------------------------------
        //----------------------------------

        //TODO: Check user action
    }


    public static void main(String[] args) {
        PApplet.main("Tanks.App");
    }

}
