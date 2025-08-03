import javax.swing.*;

public class AppLauncher {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // display the weather app gui
                    new WeatherAppGui().setVisible(true);

                System.out.println(WeatherApp.getLocationData("Berlin"));
                }
            });
        }
}
