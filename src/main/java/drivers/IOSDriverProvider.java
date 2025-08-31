package drivers;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.URL;
import java.time.Duration;

public class IOSDriverProvider implements DriverProvider {

    @Override
    public AppiumDriver create(URL serverUrl, DesiredCapabilities capabilities) {
        try {
            System.out.println("Creating iOS driver with URL: " + serverUrl);
            System.out.println("Capabilities: " + capabilities);

            IOSDriver driver = new IOSDriver(serverUrl, capabilities);

            // Set implicit wait
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));

            System.out.println("iOS driver created successfully");
            return driver;

        } catch (Exception e) {
            System.err.println("Failed to create iOS driver: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create iOS driver", e);
        }
    }
}
