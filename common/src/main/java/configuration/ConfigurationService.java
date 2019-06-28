package configuration;

import java.util.Properties;

public class ConfigurationService {

    protected final Properties properties;

    public ConfigurationService(Properties properties) {
        this.properties = properties;
    }

    public String getHost() {
        return properties.getProperty("host", "localhost");
    }

    public int getPort() {
        return Integer.parseInt(properties.getProperty("port", "5050"));
    }

    public long getMaxExecutionTime() {
        return Long.parseLong(properties.getProperty("maxExecutionTime", "-1"));
    }

}
