package core;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import utils.PortUtils;

public class AppiumServerManager {
    private static final ThreadLocal<AppiumDriverLocalService> serviceThreadLocal =
            new ThreadLocal<>();

//    private static AppiumDriverLocalService service;

    public static void startServer() {
        int port = PortUtils.getFreePort();

        AppiumDriverLocalService service = new AppiumServiceBuilder()
                .withIPAddress("127.0.0.1")
                .usingPort(port)
                .withArgument(GeneralServerFlag.SESSION_OVERRIDE)
                .withArgument(GeneralServerFlag.LOG_LEVEL, "error")
                .withArgument(GeneralServerFlag.RELAXED_SECURITY)
                .build();

        service.start();

        if (!service.isRunning()) {
            throw new RuntimeException("Appium server failed to start");
        }

        serviceThreadLocal.set(service);
        System.out.println("Appium server started on " + service.getUrl());
    }

    public static String getServerUrl() {
        return serviceThreadLocal.get().getUrl().toString();
    }

    public static void stopServer() {
        AppiumDriverLocalService service = serviceThreadLocal.get();
        if (service != null && service.isRunning()) {
            service.stop();
            System.out.println("Appium server stopped.");
        }
    }
}
