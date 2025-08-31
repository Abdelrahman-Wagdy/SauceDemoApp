package utils;

import io.appium.java_client.AppiumDriver;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.openqa.selenium.OutputType;

import java.io.ByteArrayInputStream;

public class ActionHelper {
    private final AppiumDriver driver;

    public ActionHelper(AppiumDriver driver) {
        this.driver = driver;
    }

    @Step("{actionDescription}")
    public void perform(String actionDescription, Runnable action) {
        action.run();
        attachScreenshot(actionDescription);
    }

    private void attachScreenshot(String name) {
        byte[] screenshot = driver.getScreenshotAs(OutputType.BYTES);
        // Attach as “image/png” so Allure will render it inline
        Allure.addAttachment(name, new ByteArrayInputStream(screenshot));
    }
}
