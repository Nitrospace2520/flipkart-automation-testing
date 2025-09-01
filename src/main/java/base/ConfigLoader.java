package base;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {

    private static Properties properties;

    // Load properties only once when class is loaded
    static {
        String configFilePath = System.getProperty("user.dir") + "/config/config.properties";
        try (FileInputStream fis = new FileInputStream(configFilePath)) {
            properties = new Properties();
            properties.load(fis);
//            System.out.println("Config properties loaded successfully from: " + configFilePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties file: " + configFilePath, e);
        }
    }

    // Method to fetch property values by key
    public static String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Property '" + key + "' not found in config.properties");
        }
        return value.trim();
    }

    // Optional: Get property with default value
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue).trim();
    }
}
