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

    public int getNumberOfAttemptsToGetNewTask() {
        return Integer.parseInt(properties.getProperty("newTaskAttempts", "8"));
    }

    public long getNewTaskAttemptPause() {
        return Long.parseLong(properties.getProperty("newTaskAttemptPause", "10000"));
    }

    public int getNumberOfCyclesToProcess() {
        return Integer.parseInt(properties.getProperty("numberOfCyclesToProcess", "128"));
    }
}
