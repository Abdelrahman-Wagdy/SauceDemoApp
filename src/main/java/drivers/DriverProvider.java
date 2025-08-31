package drivers;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.URL;

public interface DriverProvider {
    AppiumDriver create(URL serverUrl, DesiredCapabilities capabilities);
}
