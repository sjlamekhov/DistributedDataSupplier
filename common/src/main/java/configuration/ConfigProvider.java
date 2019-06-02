package configuration;

import java.util.Properties;

public class ConfigProvider {

    private static final String CONFIG_FILE = "configFile";
    private static final String DEFAULT_CONFIG_FILE = "properties.properties";

    public static Properties getProperties() {
        Properties properties = new Properties();
        String configFile = System.getProperty(CONFIG_FILE);
        try {
            if (configFile != null) {
                properties = FileUtils.propertiesFromFile(configFile);
            } else {
                properties = FileUtils.propertiesFromClasspath(DEFAULT_CONFIG_FILE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }

}
