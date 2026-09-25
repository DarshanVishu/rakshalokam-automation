package com.rakshalokam.utils;

import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigReader.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config.properties file", e);
        }
    }

    public static String getProperty(String key) {
        // System property passed via CLI takes precedence over config.properties
        String systemVal = System.getProperty(key);
        if (systemVal != null && !systemVal.trim().isEmpty()) {
            return systemVal;
        }
        return properties.getProperty(key);
    }

    public static int getInt(String key, int defaultValue) {
        String val = getProperty(key);
        return (val != null) ? Integer.parseInt(val.trim()) : defaultValue;
    }
}