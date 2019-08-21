package configuration;

import persistence.converters.ResultConverter;

import java.util.*;

import static configuration.ConfigurationConstants.*;

public class ServerConfigurationService extends ConfigurationService {

    private static final String PROPERTY_SEPARATOR = ".";
    private static final String TENANTS = "tenants";
    private static final String TENANTS_SEPARATOR = ",";

    private final Map<String, DaoConfiguration> daoConfigurations;

    private ServerConfigurationService(Properties properties) {
        super(properties);
        this.daoConfigurations = new HashMap<>();
    }

    public Map<String, DaoConfiguration> getDaoConfigurations() {
        return Collections.unmodifiableMap(daoConfigurations);
    }

    public Set<String> getTenantIds() {
        return daoConfigurations.keySet();
    }

    public static ServerConfigurationService buildServerConfiguration(Properties properties) {
        ServerConfigurationService serverConfigurationService = new ServerConfigurationService(properties);
        Set<String> tenantIds = new HashSet<>(Arrays.asList(serverConfigurationService
                .properties.getProperty(TENANTS, TENANTS_SEPARATOR).split(",")));
        for (String tenantId : tenantIds) {
            Map<String, String> config = new HashMap<>();
            String daoType = serverConfigurationService.properties.getProperty(String.format("%s.%s.%s", DAO_PREFIX, tenantId, DAO_TYPE));
            if (Objects.equals(daoType, DAO_TYPE_INMEMORY)) {
                config.put(DAO_TYPE, DAO_TYPE_INMEMORY);
            } else if (Objects.equals(daoType, DAO_TYPE_MONGODB)) {
                config.put(DAO_TYPE, DAO_TYPE_MONGODB);
            }
            String daoHostProperty = String.format("%s.%s.%s", DAO_PREFIX, tenantId, DAO_CONFIG_HOST);
            config.put(DAO_CONFIG_HOST, serverConfigurationService.properties.getProperty(daoHostProperty, "localhost:27017"));
            serverConfigurationService.daoConfigurations.put(tenantId, new DaoConfiguration(config));
        }
        return serverConfigurationService;
    }

    public boolean isDemoMode() {
        return properties.containsKey(DEMO_MODE);
    }

    public String getResultConsumerType() {
        return properties.getProperty(RESULT_CONSUMER_TYPE, RESULT_CONSUMER_NONE);
    }

    public String getResultConsumerUrl() {
        return properties.getProperty(RESULT_CONSUMER_URL, "http://localhost:80");
    }

    public String getResultConsumerTemplate() {
        return properties.getProperty(RESULT_CONSUMER_TEMPLATE, "{}");
    }
}
