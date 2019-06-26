package configuration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DaoConfiguration {

    public static final DaoConfiguration compositeConfiguration = new DaoConfiguration(Collections.emptyMap());
    public static final DaoConfiguration emptyConfiguration = new DaoConfiguration(Collections.emptyMap());

    //configuration key to configuration value
    private final Map<String, String> configuration;

    public DaoConfiguration(Map<String, String> configuration) {
        this.configuration = new HashMap<>();
        this.configuration.putAll(configuration);
    }

    public String getValueByKey(String key) {
        return configuration.get(key);
    }

    public String getValueByKeyOrDefault(String key, String defaultValue) {
        return configuration.getOrDefault(key, defaultValue);
    }

}