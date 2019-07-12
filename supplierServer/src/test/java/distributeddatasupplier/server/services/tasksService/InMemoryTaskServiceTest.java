package distributeddatasupplier.server.services.tasksService;

import configuration.FileUtils;
import configuration.ServerConfigurationService;
import dao.CompositeTaskDao;
import dao.DaoFactory;
import distributeddatasupplier.server.services.TaskService;

import java.util.Properties;

public class InMemoryTaskServiceTest extends AbstractTaskServiceTest {

    @Override
    protected TaskService getTaskService() {
        Properties properties = FileUtils.propertiesFromResource("inmemory_multitable.properties");
        ServerConfigurationService configurationService = ServerConfigurationService.buildServerConfiguration(properties);
        CompositeTaskDao taskDao = DaoFactory.buildTaskDao(configurationService);
        return new TaskService(taskDao);
    }

}
