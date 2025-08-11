package Tanks;
import java.util.Random;

/**
 * Represents the wind in the game.
 */

public class Wind {
    private int windSpeed;
    private Random random = new Random();

    /**
     * Create a Wind object with a random wind speed.
     */

    public Wind() {
        windSpeed = random.nextInt(71) - 35;
    }

    /**
     * Updates the wind speed by adding a random value between -5 and 5.
     */
    public void update() {
        windSpeed += random.nextInt(11) - 5;
    }

    /**
     * Gets the current wind speed.
     * @return the current wind speed
     */
    public int getSpeed() {
        return windSpeed;
    }

    /**
     * Draws the wind.
     * @param app the application instance
     */
    public void draw(App app) {
        if (windSpeed >= 0) {
            app.image(app.windRight, App.WIDTH - 130, 0);
        } 
        else {
            app.image(app.windLeft, App.WIDTH - 130, 0);
        }
        app.fill(0, 0, 0);
        app.textSize(16.0f);
        app.text(windSpeed, App.WIDTH - 60, 35);
    }
    


}
