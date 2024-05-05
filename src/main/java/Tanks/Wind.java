package Tanks;
import java.util.Random;

public class Wind {
    private int windSpeed;
    private Random random = new Random();

    public Wind() {
        windSpeed = random.nextInt(71) - 35;
    }

    public void update() {
        windSpeed += random.nextInt(11) - 5;
        if (windSpeed > 35) {
            windSpeed = 35;
        }
        else if (windSpeed < -35) {
            windSpeed = -35;
        }
    }

    public int getSpeed() {
        return windSpeed;
    }

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
