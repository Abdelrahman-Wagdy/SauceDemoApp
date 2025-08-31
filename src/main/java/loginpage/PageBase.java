package loginpage;

import core.DriverFactory;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ActionHelper;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class PageBase {
    AppiumDriver driver;
    public static final int WAIT = 10;
    protected final ActionHelper a;

    public PageBase(){
        driver = DriverFactory.getDriver();
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
        a = new ActionHelper(driver);
    }

    public void waitForVisibility(WebElement element){
        WebDriverWait wait = new WebDriverWait(driver, Duration.of(WAIT, ChronoUnit.SECONDS));
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    public void clear(WebElement element){
        waitForVisibility(element);
        element.clear();
    }

    public void click(WebElement element){
        waitForVisibility(element);
        element.click();
    }

    public void sendText(WebElement element, String text){
        waitForVisibility(element);
        element.sendKeys(text);
    }

    public String getAttribute(WebElement element, String attribute){
        waitForVisibility(element);
        return element.getAttribute(attribute);
    }
}
