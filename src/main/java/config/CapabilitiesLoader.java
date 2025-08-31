package config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.util.List;
import java.util.Map;

public class CapabilitiesLoader {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static String PLATFORM;

    @SuppressWarnings("unchecked")
    public static DesiredCapabilities fromJson(String jsonPath,
                                               String platform,           // "android" or "ios"
                                               String deviceNameOrNull,   // optional: pick by deviceName
                                               int indexIfNull)           // or pick by index (0-based)
            throws Exception {

        PLATFORM = platform;

        // JSON shape: { "android": [ {..}, {..} ], "ios": [ {..} ] }
        Map<String, List<Map<String,Object>>> root =
                MAPPER.readValue(new File(jsonPath),
                        new TypeReference<Map<String, List<Map<String, Object>>>>() {});

        List<Map<String,Object>> list = root.get(platform.toLowerCase());
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("No capabilities for platform: " + platform);
        }

        Map<String,Object> picked;
        if (deviceNameOrNull != null && !deviceNameOrNull.isBlank()) {
            picked = list.stream()
                    .filter(m -> deviceNameOrNull.equals(String.valueOf(m.get("deviceName"))))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            "No device with deviceName=" + deviceNameOrNull + " for " + platform));
        } else {
            if (indexIfNull < 0 || indexIfNull >= list.size()) {
                throw new IndexOutOfBoundsException(
                        "Index " + indexIfNull + " out of range for platform " + platform +
                                " (size=" + list.size() + ")");
            }
            picked = list.get(indexIfNull);
        }

        return new DesiredCapabilities(picked); // feed the map directly
    }

    public static String getPLATFORM(){
        return PLATFORM;
    }
}

