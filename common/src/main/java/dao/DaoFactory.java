package dao;

import configuration.ConfigurationService;
import configuration.DaoConfiguration;
import configuration.ServerConfigurationService;
import objects.Task;
import objects.TaskUri;
import persistence.InMemoryPersistence;
import persistence.PersistenceLayer;
import persistence.tasks.InMemoryTaskPersistence;
import persistence.tasks.TaskPersistenceLayer;

import java.util.Map;
import java.util.Objects;

import static configuration.ConfigurationConstants.DAO_TYPE;
import static configuration.ConfigurationConstants.DAO_TYPE_INMEMORY;

public class DaoFactory {

    public static CompositeTaskDao buildTaskDao(ServerConfigurationService serverConfigurationService) {
        CompositeTaskDao compositeDao = new CompositeTaskDao();
        Map<String, DaoConfiguration> daoConfigurations = serverConfigurationService.getDaoConfigurations();
        for (Map.Entry<String, DaoConfiguration> daoConfigEntry : daoConfigurations.entrySet()) {
            String tenantId = daoConfigEntry.getKey();
            DaoConfiguration daoConfiguration = daoConfigEntry.getValue();
            TaskDao taskDao = null;
            if (Objects.equals(daoConfiguration.getValueByKey(DAO_TYPE), DAO_TYPE_INMEMORY)) {
                TaskPersistenceLayer taskPersistence = new InMemoryTaskPersistence();
                taskDao = new TaskDao(taskPersistence);
            }
            if (taskDao != null) {
                compositeDao.addDao(tenantId, taskDao);
            }
        }
        return compositeDao;
    }

}
