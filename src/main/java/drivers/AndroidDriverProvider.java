package drivers;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.URL;
import java.time.Duration;

public class AndroidDriverProvider implements DriverProvider {

    @Override
    public AppiumDriver create(URL serverUrl, DesiredCapabilities capabilities) {
        try {
            System.out.println("Creating Android driver with URL: " + serverUrl);
            System.out.println("Capabilities: " + capabilities);

            AndroidDriver driver = new AndroidDriver(serverUrl, capabilities);

            // Set implicit wait
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));

            System.out.println("Android driver created successfully");
            return driver;

        } catch (Exception e) {
            System.err.println("Failed to create Android driver: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create Android driver", e);
        }
    }
}
