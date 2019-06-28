package distributeddatasupplier.client.configuration;

import configuration.ConfigurationService;

import java.util.Properties;

public class ClientConfigurationService extends ConfigurationService {

    public ClientConfigurationService(Properties properties) {
        super(properties);
    }

    public String getTenantId() {
        return properties.getProperty("tenantId");
    }

}
