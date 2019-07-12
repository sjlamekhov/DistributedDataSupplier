package dao;

import configuration.DaoConfiguration;
import configuration.ServerConfigurationService;
import objects.Result;
import objects.ResultUri;
import persistence.InMemoryPersistence;
import persistence.MongoDbPersistency;
import persistence.PersistenceLayer;
import persistence.converters.ResultConverter;
import persistence.converters.TaskConverter;
import persistence.tasks.InMemoryTaskPersistence;
import persistence.tasks.MongoDbTaskPersistency;
import persistence.tasks.TaskPersistenceLayer;

import java.util.Map;
import java.util.Objects;

import static configuration.ConfigurationConstants.*;

public class DaoFactory {

    public static CompositeTaskDao buildTaskDao(ServerConfigurationService serverConfigurationService) {
        CompositeTaskDao compositeDao = new CompositeTaskDao();
        Map<String, DaoConfiguration> daoConfigurations = serverConfigurationService.getDaoConfigurations();
        for (Map.Entry<String, DaoConfiguration> daoConfigEntry : daoConfigurations.entrySet()) {
            String tenantId = daoConfigEntry.getKey();
            DaoConfiguration daoConfiguration = daoConfigEntry.getValue();
            String tableName = daoConfiguration.getValueByKeyOrDefault(TASKS_TABLENAME, "tasks");
            TaskDao taskDao = null;
            if (Objects.equals(daoConfiguration.getValueByKey(DAO_TYPE), DAO_TYPE_INMEMORY)) {
                TaskConverter taskConverter = new TaskConverter(tenantId);
                TaskPersistenceLayer taskPersistence = new InMemoryTaskPersistence(taskConverter);
                taskDao = new TaskDao(taskPersistence);
            } else if (Objects.equals(daoConfiguration.getValueByKey(DAO_TYPE), DAO_TYPE_MONGODB)) {
                TaskConverter taskConverter = new TaskConverter(tenantId);
                TaskPersistenceLayer taskPersistence = new MongoDbTaskPersistency(daoConfiguration,
                        taskConverter,
                        tenantId,
                        tableName);
                taskDao = new TaskDao(taskPersistence);
            }
            if (taskDao != null) {
                compositeDao.addDao(tenantId, taskDao);
            }
        }
        return compositeDao;
    }

    public static CompositeDao<ResultUri, Result> buildResultDao(ServerConfigurationService serverConfigurationService) {
        CompositeDao<ResultUri, Result> compositeDao = new CompositeDao<>();
        Map<String, DaoConfiguration> daoConfigurations = serverConfigurationService.getDaoConfigurations();
        for (Map.Entry<String, DaoConfiguration> daoConfigEntry : daoConfigurations.entrySet()) {
            String tenantId = daoConfigEntry.getKey();
            DaoConfiguration daoConfiguration = daoConfigEntry.getValue();
            String tableName = daoConfiguration.getValueByKeyOrDefault(RESULTS_TABLENAME, "results");
            ResultDao resultDao = null;
            if (Objects.equals(daoConfiguration.getValueByKey(DAO_TYPE), DAO_TYPE_INMEMORY)) {
                ResultConverter resultConverter = new ResultConverter(tenantId);
                PersistenceLayer<ResultUri, Result> resultPersistence = new InMemoryPersistence<>(resultConverter);
                resultDao = new ResultDao(resultPersistence);
            } else if (Objects.equals(daoConfiguration.getValueByKey(DAO_TYPE), DAO_TYPE_MONGODB)) {
                ResultConverter resultConverter = new ResultConverter(tenantId);
                MongoDbPersistency<ResultUri, Result> resultPersistence = new MongoDbPersistency<>(daoConfiguration, resultConverter, tenantId, tableName);
                resultDao = new ResultDao(resultPersistence);
            }
            if (resultDao != null) {
                compositeDao.addDao(tenantId, resultDao);
            }
        }
        return compositeDao;
    }
}
