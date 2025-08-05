package com.framework.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private static final Properties prop = new Properties();
    private static boolean loaded = false;

    private ConfigReader() {
    }

    private static synchronized void initProperties() {
        if (loaded) return;

        try (FileInputStream fis = new FileInputStream("resources/config.properties")) {
            prop.load(fis);

            // Merge system properties (CLI) — override loaded values
            for (String key : prop.stringPropertyNames()) {
                String sysVal = System.getProperty(key);
                if (sysVal != null && !sysVal.isEmpty()) {
                    prop.setProperty(key, sysVal);
                }
            }

            // Also load system-only keys not in file
            for (Object keyObj : System.getProperties().keySet()) {
                String key = keyObj.toString();
                if (!prop.containsKey(key)) {
                    prop.setProperty(key, System.getProperty(key));
                }
            }

            loaded = true;
        } catch (IOException e) {
            System.err.println("Unable to load config.properties from resources folder.");
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        if (!loaded) {
            initProperties();
        }
        return prop.getProperty(key);
    }
}
