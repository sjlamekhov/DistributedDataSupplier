package distributeddatasupplier.client.configuration;

import java.util.Properties;

import configuration.ConfigurationService;

import static configuration.ConfigurationConstants.*;

public class ClientConfigurationService extends ConfigurationService {

    public ClientConfigurationService(Properties properties) {
        super(properties);
    }

    public String getTenantId() {
        return properties.getProperty(TENANT_ID);
    }

    public int getNumberOfAttemptsToGetNewTask() {
        return Integer.parseInt(properties.getProperty(NEW_TASK_ATTEMPTS, "8"));
    }

    public long getNewTaskAttemptPause() {
        return Long.parseLong(properties.getProperty(NEW_TASK_ATTEMPT_PAUSE, "10000"));
    }

    public int getNumberOfCyclesToProcess() {
        return Integer.parseInt(properties.getProperty(NUMBER_OF_CYCLES_TO_PROCESS, "128"));
    }
}
