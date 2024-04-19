package Tanks;

import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import  java.util.ArrayList;

public class Terrain {

    public int[] getHeight(char[][] pixelMap) {
        int col_pixelMap = pixelMap[0].length;
        int row_pixelMap = pixelMap.length;
        int[] height = new int[896];


        for (int col = 0; col < col_pixelMap; col++) {
            for (int row = 0; row < row_pixelMap; row ++) {
                if (pixelMap[row][col] == 'X') {
                    height[col] = 32 * row;
                    break;
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

    public ArrayList<Integer> tree_column(char[][] pixelMap, App app) {

        int col_pixelMap = app.CELLSIZE * app.BOARD_WIDTH;
        int row_pixelMap = app.BOARD_HEIGHT;       
        ArrayList<Integer> tree_column = new ArrayList<>(); 
        for (int col = 0; col < col_pixelMap; col++) {
            for (int row = 0; row < row_pixelMap; row++) {
                if (pixelMap[row][col] ==  'T') {
                    tree_column.add(col);
                }
            }
        }
        return tree_column;
    }


    public void draw(char[][] pixelMap, App app) {
        
        int[] height_lst = getHeight(pixelMap);
        int[] moving_average = moving_average(moving_average(height_lst));
        ArrayList<Integer> tree_column = tree_column(pixelMap, app);
        app.image(app.background, 0, 0);

        for (int col = 0; col < app.BOARD_WIDTH * app.CELLSIZE; col++) {
            for (int row = 0; row < app.BOARD_HEIGHT; row++) {
                char character_type = pixelMap[row][col];
                
                if (character_type == 'X') {
                    int height = moving_average[col];
                    app.stroke(255, 255, 255);    
                    app.line(col, height, col, app.HEIGHT);
                    break;
                    
                }
            }
        }
        
        for (int i = 0; i < tree_column.size(); i++) {
            int tree_col = tree_column.get(i);
            int tree_row = moving_average[tree_col];
            app.tree.resize(0, 32);
            app.image(app.tree, tree_col - app.tree.width/2, tree_row - app.tree.height);
            
        }
         
        }
    

    
}
