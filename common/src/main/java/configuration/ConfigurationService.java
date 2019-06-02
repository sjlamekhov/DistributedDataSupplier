package configuration;

import java.util.Properties;

public class ConfigurationService {

    protected final Properties properties;

    public ConfigurationService() {
        this.properties = ConfigProvider.getProperties();
    }

    public String getHost() {
        return properties.getProperty("host", "localhost");
    }

    public int getPort() {
        return Integer.parseInt(properties.getProperty("port", "5050"));
    }

}
