import javax.swing.*;


public class WeatherAppGui extends JFrame {
    public WeatherAppGui() {
        //Set up our gui and add a title
        super("Weather App");

        // configure gui to end the program's process once it has been closed
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // set the size of the gui window in pixels
        setSize(450, 650);

        // load the gui at the centre of the screen
        setLocationRelativeTo(null);

        // make our layout manager null to manually position our components within the gui
        setLayout(null);

        // prevent resizing of the gui window
        setResizable(false);
    }
}
