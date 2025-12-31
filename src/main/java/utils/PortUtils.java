package utils;

import java.io.IOException;
import java.net.ServerSocket;

public class PortUtils {

    public static int getFreePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            socket.setReuseAddress(true);
            return socket.getLocalPort();
        } catch (IOException e) {
            throw new RuntimeException("Unable to find a free port", e);
        }
    }
}