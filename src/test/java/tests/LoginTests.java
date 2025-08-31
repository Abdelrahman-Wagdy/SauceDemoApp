package tests;

import config.CapabilitiesLoader;
import core.AppiumServerManager;
import core.DriverFactory;
import io.appium.java_client.AppiumDriver;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import loginpage.LoginServices;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.*;
import utils.ActionHelper;

public class LoginTests {
    LoginServices loginServices;
    AppiumDriver driver;
    ActionHelper a;

    @BeforeClass
    @Parameters({"platform", "deviceName", "deviceIndex"})
    public void setUp(
            @Optional("android") String platform,
            @Optional("") String deviceName,
            @Optional("0") String deviceIndex) throws Exception {
        System.setProperty("platform", platform);
        int index = (deviceIndex == null || deviceIndex.isBlank()) ? 0 : Integer.parseInt(deviceIndex);
        DesiredCapabilities caps = CapabilitiesLoader.fromJson(
                "src/test/resources/config.json", platform, deviceName, index);

        driver = DriverFactory.createDriver(platform, caps);
        a = new ActionHelper(driver);
        loginServices = new LoginServices();

        if (!deviceIndex.isBlank())
            System.setProperty("deviceName", deviceName);

        System.setProperty("deviceIndex", deviceIndex);
    }

    @AfterClass
    public void tearDown() {
        driver.quit();
        AppiumServerManager.stopServer();
    }
    @Severity(SeverityLevel.CRITICAL)
    @Test
    public void test_login_with_valid_credentials() {
        loginServices.enterLoginDetails("standard_user", "secret_sauce");
        a.perform("Validate that the user logs in successfully",
                ()-> Assert.assertTrue(loginServices.userLoggedIn(), "user didn't login"));
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test
    public void test_login_with_invalid_username_credentials() {
        loginServices.enterLoginDetails("standArd_user", "secret_sauce");
        a.perform("Validate that the user doesn't log in with invalid username",
                ()-> Assert.assertEquals(loginServices.errorTextMessage(),
                        "Username and password do match any user in this service."));
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test
    public void test_login_with_invalid_password_credentials() {
        loginServices.enterLoginDetails("standard_user", "secretsauce");
        a.perform("Validate that the user doesn't log in with invalid password",
                ()-> Assert.assertEquals(loginServices.errorTextMessage(),
                        "Username and password do not match any user in this service."));
    }

    @Severity(SeverityLevel.NORMAL)
    @Test
    public void test_login_with_locked_out_user() {
        loginServices.enterLoginDetails("locked_out_user", "secret_sauce");
        a.perform("Validate that the user account is blocked and an error message is displayed",
                ()-> Assert.assertEquals(loginServices.errorTextMessage(),
                        "Sorry, this user has been locked out."));
    }
}
