package loginpage;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;

import java.util.function.Function;


public class LoginServices extends PageBase {

    public LoginServices(){
        super();
    }

    @CacheLookup
    @AndroidFindBy(accessibility = "test-Username")
    @iOSXCUITFindBy(accessibility = "test-Username")
    WebElement username;

    @CacheLookup
    @AndroidFindBy(accessibility = "test-Password")
    @iOSXCUITFindBy(accessibility = "test-Password")
    WebElement password;

    @CacheLookup
    @AndroidFindBy(accessibility = "test-LOGIN")
    @iOSXCUITFindBy(accessibility = "test-LOGIN")
    WebElement loginButton;

    @AndroidFindBy(accessibility = "test-Cart drop zone")
    @iOSXCUITFindBy(id = "test-Cart drop zone")
    WebElement menuButton;

    @AndroidFindBy(accessibility = "test-Error message")
    @iOSXCUITFindBy(accessibility = "test-Error message")
    WebElement errorMessage;


    public enum Buttons {
        LOGIN_BUTTON(page -> page.loginButton),
        MENU_BUTTON(page -> page.menuButton);

        private final Function<LoginServices, WebElement> resolver;
        Buttons(Function<LoginServices, WebElement> resolver) {
            this.resolver = resolver;
        }

        public WebElement element(LoginServices page) {
            return resolver.apply(page);
        }
    }

    public void enterLoginDetails(String username, String password){
        clear(this.username);
        sendText(this.username, username);

        clear(this.password);

        sendText(this.password, password);
        click(this.loginButton);
//        a.perform("Enter username and password: " + password, () -> sendText(this.password, password));

//        a.perform("Click on login button", () -> click(this.loginButton));
    }

    public boolean userLoggedIn(){
        return elementIsDisplayed(Buttons.MENU_BUTTON);
    }

    public String errorTextMessage(){
        waitForVisibility(errorMessage);
        WebElement errorText;

        if(driver instanceof io.appium.java_client.android.AndroidDriver){
            errorText = errorMessage.findElement(By.xpath(".//android.widget.TextView"));
        }else{
            errorText = errorMessage.findElement(By.xpath("//XCUIElementTypeStaticText"));
        }
        return errorText.getText();
    }

    public boolean loginPageDisplayed(){
        return elementIsDisplayed(Buttons.LOGIN_BUTTON);
    }

    private boolean elementIsDisplayed(Buttons button){
        boolean visible = false;
        try{
            visible = button.element(this).isDisplayed();
        } catch (Exception e) {
            System.out.println("Menu Button isn't displayed");
        }
        return visible;
    }
}
