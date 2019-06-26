package distributeddatasupplier.client.configuration;

import configuration.ConfigurationService;

public class ClientConfigurationService extends ConfigurationService {

    public String getTenantId() {
        return properties.getProperty("tenantId");
    }

}
