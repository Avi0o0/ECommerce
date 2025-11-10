package jwt.util.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesHandler {

	private PropertiesHandler() {
	}

	public static String getProperty(String propName) {
		Properties props = new Properties();
		try (FileInputStream fis = new FileInputStream("config.properties")) {
			props.load(fis);
			return props.getProperty(propName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
