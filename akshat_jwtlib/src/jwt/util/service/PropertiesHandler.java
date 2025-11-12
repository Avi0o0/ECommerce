package jwt.util.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesHandler {

	private static final Properties properties = new Properties();

    static {
        try (InputStream inputStream = PropertiesHandler.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {

            if (inputStream == null) {
                throw new RuntimeException("config.properties not found in classpath!");
            }

            properties.load(inputStream);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    private PropertiesHandler() {
    	
    }
}
