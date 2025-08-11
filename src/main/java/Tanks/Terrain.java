package Tanks;

import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.FileNotFoundException;

/**
 * Represents the terrain in the game. The terrain consists of the game map loaded from a file, including grounds or trees.
 */
public class Terrain {

    private String layout;
    private String foregroundColour;
    private char[][] levelMap;

    /**
     * Create a Terrain object with the specified layout and foreground color.
     *
     * @param layout the file path to the layout file containing the game map
     * @param foregroundColour the color of the foreground elements of the terrain
     */

    public Terrain(String layout, String foregroundColour) {
        this.layout = layout;
        this.foregroundColour = foregroundColour;
        this.levelMap = loadLevel(layout);
    }

    /**
     * Loads the game map from the a layout file.
     *
     * @param layout the file path to the layout file
     * @return a 2D character array representing the game map
     */

    public char[][] loadLevel(String layout) {
        try {
            File f = new File(layout);
            Scanner scan = new Scanner(f);
            char[][] levelMap;
            int row = 0;
            levelMap = new char[20][28];
            while (scan.hasNextLine() && row < 20) {
                String line = scan.nextLine(); 
                for (int col = 0; col < 28; col ++) {
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

    /**
     * Retrieves the RGB color values of the terrain's foreground elements.
     * @return an integer array containing the RGB color values
     */

    public int[] getColor() {
        int indexR = Integer.parseInt(foregroundColour.split(",")[0]);
        int indexG = Integer.parseInt(foregroundColour.split(",")[1]);
        int indexB = Integer.parseInt(foregroundColour.split(",")[2]);
        int[] index = new int[]{indexR, indexG, indexB};
        return index;
    }

    /**
     * Calculates the height of the terrain based on the game map.
     *
     * @param levelMap  a 2D character array representing the game map 
     * @return the array that contains terrain heights
     */

    public int[] getHeight(char[][] levelMap) {
        int totalCol = levelMap[0].length;
        int totalRow = levelMap.length;
        int[] height = new int[896];


        for (int col = 0; col < totalCol; col++) {
            for (int row = 0; row < totalRow; row ++) {
                if (levelMap[row][col] == 'X') {
                    for (int i = 0; i < 32; i++) {
                        height[32 * col + i] = 32 * row;
                    }
                }
            }
        }

        return height;
    }
    
    /**
     * Calculate the moving average of terrain heights.
     *
     * @param height an array containing the height of the terrain
     * @return an array containing terrain heights after smoothing
     */
    public int[] movingAverage(int[] height) {
        int[] movingAverage = new int[height.length];
        int movingSum = 0;

        for (int i = 0; i < 32; i++) {
            movingSum += height[i];
        }

        movingAverage[0] = (int)movingSum/32;

        for (int i = 0; i < (height.length - 32); i ++) {
            movingSum += (height[i + 32] - height[i]);
            movingAverage[i + 1] = (int)movingSum/32;
        } 
        
        for (int j = height.length - 32; j < height.length; j++) {
            movingAverage[j] = (int)movingSum/32;
        }


        return movingAverage;
    }

    /**
     * Finding the x-coordinates of trees.
     *
     * @param levelMap a 2D character array representing the game map
     * @return a list that contains coordinate of trees
     */
    public ArrayList<Integer> treeColumn(char[][] levelMap) {

        int totalCol = levelMap[0].length;
        int totalRow = levelMap.length;       
        ArrayList<Integer> treeColumn = new ArrayList<>(); 
        for (int col = 0; col < totalCol; col++) {
            for (int row = 0; row < totalRow; row++) {
                if (levelMap[row][col] ==  'T') {
                    treeColumn.add(32 * col);
                }
            }
        }
        
        return treeColumn;
    }


    /**
     * Draws the terrain.
     *
     * @param app the application instance
     */
    public void draw(App app) {
        
        ArrayList<Integer> treeColumn = treeColumn(levelMap);
        app.image(app.backgroundImage, 0, 0);

        for (int col = 0; col < App.WIDTH; col++) {
                int height = app.terrainHeight[col];
                app.stroke(getColor()[0], getColor()[1], getColor()[2]);
                if (height < App.HEIGHT) {    
                    app.line(col, height, col, App.HEIGHT);  
                }          
        }
        
        for (int i = 0; i < treeColumn.size(); i++) {
            int tree_col = treeColumn.get(i);
            int tree_row = app.terrainHeight[tree_col];
            if (app.treeImage != null) {
                app.treeImage.resize(0, 32);
                app.image(app.treeImage, tree_col - app.treeImage.width/2, tree_row - app.treeImage.height);
            }
        }
         
        }
    

    
}
