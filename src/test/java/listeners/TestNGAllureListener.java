package listeners;

import config.CapabilitiesLoader;
import core.DriverFactory;
import io.appium.java_client.AppiumDriver;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

public class TestNGAllureListener implements ITestListener {
    private static final ThreadLocal<Long> testStartTime = new ThreadLocal<>();
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public void onTestStart(ITestResult result) {
        testStartTime.set(System.currentTimeMillis());
        System.out.println("🚀 Starting test: " + result.getMethod().getMethodName());

        // Add environment info
        addEnvironmentInfo(result);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("✅ Test passed: " + result.getMethod().getMethodName());

        // Take success screenshot
        takeScreenshot("✅ SUCCESS - " + result.getMethod().getMethodName());

        // Clean up
        testStartTime.remove();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("❌ Test failed: " + result.getMethod().getMethodName());

        // Take failure screenshot
        takeScreenshot("❌ FAILURE - " + result.getMethod().getMethodName());

        // Capture device logs
        captureDeviceLogs("Device Logs - " + result.getMethod().getMethodName());

        // Add failure details
        addFailureInfo(result);

        // Clean up
        testStartTime.remove();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("⏭️ Test skipped: " + result.getMethod().getMethodName());
        testStartTime.remove();
    }

    private void takeScreenshot(String name) {
        try {
            AppiumDriver driver = DriverFactory.getDriver();
            if (driver != null) {
                byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                String attachmentName = name + " [" + timestamp + "]";

                Allure.addAttachment(attachmentName, "image/png",
                        new ByteArrayInputStream(screenshot), ".png");

                System.out.println("📸 Screenshot captured: " + attachmentName);
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to take screenshot: " + e.getMessage());
        }
    }

    private void captureDeviceLogs(String logName) {
        try {
            AppiumDriver driver = DriverFactory.getDriver();
            if (driver == null) return;

            Set<String> availableLogTypes = driver.manage().logs().getAvailableLogTypes();
            System.out.println("📋 Available log types: " + availableLogTypes);

            // Priority order for log types
            String[] preferredLogTypes = {"logcat", "syslog", "driver", "client"};
            String chosenLogType = null;

            for (String logType : preferredLogTypes) {
                if (availableLogTypes.contains(logType)) {
                    chosenLogType = logType;
                    break;
                }
            }

            if (chosenLogType == null) {
                System.out.println("⚠️ No suitable log type found");
                return;
            }

            LogEntries entries = driver.manage().logs().get(chosenLogType);
            StringBuilder sb = new StringBuilder();
            long since = Optional.ofNullable(testStartTime.get()).orElse(0L);
            int logCount = 0;

            for (LogEntry entry : entries) {
                if (entry.getTimestamp() >= since) {
                    sb.append(SDF.format(new Date(entry.getTimestamp())))
                            .append(" [").append(entry.getLevel()).append("] ")
                            .append(entry.getMessage()).append('\n');
                    logCount++;
                }
            }

            if (sb.length() > 0) {
                String finalLogName = logName + " (" + logCount + " entries)";
                byte[] logBytes = sb.toString().getBytes(StandardCharsets.UTF_8);

                Allure.addAttachment(finalLogName, "text/plain",
                        new ByteArrayInputStream(logBytes), ".txt");

                System.out.println("📋 Device logs captured: " + finalLogName);
            }

        } catch (Exception e) {
            System.err.println("❌ Failed to capture device logs: " + e.getMessage());
        }
    }

    private void addEnvironmentInfo(ITestResult result) {
        try {
            String environmentInfo = "Test: " + result.getMethod().getMethodName() + "\n" +
                    "Class: " + result.getTestClass().getName() + "\n" +
                    "Platform: " + CapabilitiesLoader.getPLATFORM() + "\n" +
                    "Device: " + System.getProperty("deviceName")  + "\n" +
                    "App: SauceLabs Demo App\n" +
                    "Started: " + SDF.format(new Date());

            Allure.addAttachment("Environment Info", "text/plain", environmentInfo);
        } catch (Exception e) {
            System.err.println("Failed to add environment info: " + e.getMessage());
        }
    }

    private void addFailureInfo(ITestResult result) {
        try {
            StringBuilder failureInfo = new StringBuilder();
            failureInfo.append("Test: ").append(result.getMethod().getMethodName()).append("\n");
            failureInfo.append("Class: ").append(result.getTestClass().getName()).append("\n");
            failureInfo.append("Status: FAILED\n");
            failureInfo.append("Failed at: ").append(SDF.format(new Date())).append("\n");

            if (result.getThrowable() != null) {
                failureInfo.append("Error: ").append(result.getThrowable().getMessage()).append("\n");
                failureInfo.append("Exception: ").append(result.getThrowable().getClass().getSimpleName()).append("\n");
            }

            Allure.addAttachment("Failure Details", "text/plain", failureInfo.toString());
        } catch (Exception e) {
            System.err.println("Failed to add failure info: " + e.getMessage());
        }
    }
}