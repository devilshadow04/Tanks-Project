package Tanks;

import processing.core.PImage;
import java.util.*;

public class Tank {
    private int x;
    private int y;
    private double rotate_angle;


    public Tank(int x, int y) {
        this.x = x;
        this.y = y;
        this.rotate_angle = Math.PI/2;
    }

    public int getX() {
        return this.x;
    }

    public int[] moveLeft(int[] moving_average) {
        int[] coordinate = new int[2];
        this.y -= 1;
        this.x = moving_average[this.y];
        coordinate[0] = this.x;
        coordinate[1] = this.y;
        return coordinate;
    }

    public void moveRight(int[] moving_average) {
        this.y += 1;
        this.x = moving_average[this.y];
    }

    

    public static HashMap<Character, Tank> tank_location(char[][] pixelMap, int[] moving_average) {
        HashMap<Character, Tank> tank_map = new HashMap<>();
        ArrayList<Character> possible_value = new ArrayList<>();
        for (char c = 'A'; c <= 'I'; c++) {
            possible_value.add(c);
        }
        for (char c = '0'; c <= '9'; c++) {
            possible_value.add(c);
        }

        for (int col = 0; col < pixelMap[0].length; col ++) {
            for (int row = 0; row < pixelMap.length; row++) {
                if (possible_value.contains(pixelMap[row][col])) {
                    char player_name = pixelMap[row][col];
                    tank_map.put(player_name, new Tank(moving_average[col], col));
                    break;
                }
            }
        }
        return tank_map;
    }


    

    public void draw(App app) {
        
        //tank's bottom
        app.rect(y-10, x - 10, 20, 5);
        //tank's top
        app.rect(y-5, x - 15, 10, 5);

        app.strokeWeight(3);
        // app.rotate((float) rotate_angle);
        
        app.line(y, x-30, y, x-15);
        app.endShape();

    }


    
}
