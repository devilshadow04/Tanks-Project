package Tanks;

import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import  java.util.ArrayList;

public class Terrain {

    private String layout;
    private ArrayList<Integer> treeColumn;
    private String foreground_colour;
    private char[][] levelMap;

    // public Terrain(String filename) {
    //     this.filename = filename;
    // }

    public Terrain(String layout, String foreground_colour) {
        this.layout = layout;
        this.foreground_colour = foreground_colour;
        this.levelMap = loadLevel(layout);
    }

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


    public int[] getColor() {
        int indexR = Integer.parseInt(foreground_colour.split(",")[0]);
        int indexG = Integer.parseInt(foreground_colour.split(",")[1]);
        int indexB = Integer.parseInt(foreground_colour.split(",")[2]);
        int[] index = new int[]{indexR, indexG, indexB};
        return index;
    }

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
    
    public int[] moving_average(int[] height) {
        int[] moving_average = new int[height.length];
        int moving_sum = 0;

        for (int i = 0; i < 32; i++) {
            moving_sum += height[i];
        }

        moving_average[0] = (int)moving_sum/32;

        for (int i = 0; i < (height.length - 32); i ++) {
            moving_sum += (height[i + 32] - height[i]);
            moving_average[i + 1] = (int)moving_sum/32;
        } 
        
        for (int j = height.length - 32; j < height.length; j++) {
            moving_average[j] = (int)moving_sum/32;
        }


        return moving_average;
    }

    public ArrayList<Integer> tree_column(char[][] levelMap) {

        int totalCol = levelMap[0].length;
        int totalRow = levelMap.length;       
        ArrayList<Integer> tree_column = new ArrayList<>(); 
        for (int col = 0; col < totalCol; col++) {
            for (int row = 0; row < totalRow; row++) {
                if (levelMap[row][col] ==  'T') {
                    tree_column.add(32 * col);
                }
            }
        }
        
        return tree_column;
    }



    public void draw(App app) {
        
        ArrayList<Integer> tree_column = tree_column(levelMap);
        app.image(app.backgroundImage, 0, 0);

        for (int col = 0; col < app.WIDTH; col++) {
                int height = app.terrainHeight[col];
                app.stroke(getColor()[0], getColor()[1], getColor()[2]);    
                app.line(col, height, col, app.HEIGHT);            
        }
        
        for (int i = 0; i < tree_column.size(); i++) {
            int tree_col = tree_column.get(i);
            int tree_row = app.terrainHeight[tree_col];
            if (app.treeImage != null) {
                app.treeImage.resize(0, 32);
                app.image(app.treeImage, tree_col - app.treeImage.width/2, tree_row - app.treeImage.height);
            }
        }
         
        }
    

    
}
