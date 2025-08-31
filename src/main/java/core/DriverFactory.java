package core;

import drivers.AndroidDriverProvider;
import drivers.DriverProvider;
import drivers.IOSDriverProvider;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;

public class DriverFactory {
    private static final ThreadLocal<AppiumDriver> driverThreadLocal = new ThreadLocal<>();

    public static AppiumDriver createDriver(String platform, DesiredCapabilities caps) {
        try {
            // Start Appium server
            AppiumServerManager.startServer();

            // Get server URL
            URL serverUrl = new URL(AppiumServerManager.getServerUrl());
            System.out.println("Appium server URL: " + serverUrl);

            // Create driver
            DriverProvider provider = getProvider(platform.toLowerCase());
            AppiumDriver driver = provider.create(serverUrl, caps);

            driverThreadLocal.set(driver);
            System.out.println("Driver created successfully for platform: " + platform);
            return driver;

        } catch (MalformedURLException e) {
            System.err.println("Invalid Appium Server URL: " + e.getMessage());
            throw new RuntimeException("Invalid Appium Server URL", e);
        } catch (Exception e) {
            System.err.println("Failed to create driver: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create driver", e);
        }
    }

    private static DriverProvider getProvider(String platform) {
        return switch (platform.toLowerCase()) {
            case "android" -> new AndroidDriverProvider();
            case "ios" -> new IOSDriverProvider();
            default -> throw new IllegalArgumentException("Unsupported platform: " + platform);
        };
    }

    public static AppiumDriver getDriver() {
        return driverThreadLocal.get();
    }

    public static void quitDriver() {
        AppiumDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                driver.quit();
                System.out.println("Driver quit successfully");
            } catch (Exception e) {
                System.err.println("Error quitting driver: " + e.getMessage());
            } finally {
                driverThreadLocal.remove();
            }
        }
    }
}