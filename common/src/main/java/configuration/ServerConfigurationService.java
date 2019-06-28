package configuration;

import java.util.*;

import static configuration.ConfigurationConstants.DAO_PREFIX;
import static configuration.ConfigurationConstants.DAO_TYPE;
import static configuration.ConfigurationConstants.DAO_TYPE_INMEMORY;

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
            String daoType = serverConfigurationService.properties.getProperty(String.format("%s.%s.%s", DAO_PREFIX, tenantId, DAO_TYPE));
            if (Objects.equals(daoType, DAO_TYPE_INMEMORY)) {
                Map<String, String> config = new HashMap<>();
                config.put(DAO_TYPE, DAO_TYPE_INMEMORY);
                serverConfigurationService.daoConfigurations.put(tenantId, new DaoConfiguration(config));
            }
        }
        return serverConfigurationService;
    }

}
