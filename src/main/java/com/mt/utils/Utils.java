package com.mt.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class Utils {

    private static Properties properties = new Properties();

    private static Logger log = Logger.getLogger(Utils.class.getCanonicalName());

    static {
        String configFileName = System.getProperty("application.properties");

        if (configFileName == null) {
            configFileName = "application.properties";
        }
        loadConfig(configFileName);
    }

    private static void loadConfig(String fileName) {
        if (fileName == null) {
            log.warning("loadConfig: config file name cannot be null");
        } else {
            try {
                log.info("loadConfig(): Loading config file: " + fileName );
                final InputStream fis = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
                properties.load(fis);

            } catch (FileNotFoundException fne) {
                log.severe("loadConfig(): file name not found " + fileName + fne.getLocalizedMessage());
            } catch (IOException ioe) {
                log.severe("loadConfig(): error when reading the config " + fileName + ioe.getLocalizedMessage());
            }
        }

    }

    public static String getStringProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            value = System.getProperty(key);
        }
        return value;
    }

}
