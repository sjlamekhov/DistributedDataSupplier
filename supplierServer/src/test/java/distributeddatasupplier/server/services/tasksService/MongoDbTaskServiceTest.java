package distributeddatasupplier.server.services.tasksService;

import configuration.DaoConfiguration;
import configuration.FileUtils;
import configuration.ServerConfigurationService;
import dao.CompositeTaskDao;
import dao.DaoFactory;
import distributeddatasupplier.server.services.TaskService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import utils.MongoDbUtils;

import java.util.Properties;

import static configuration.ConfigurationConstants.TASKS_TABLENAME;

public class MongoDbTaskServiceTest extends AbstractTaskServiceTest {

    private static Logger logger = LogManager.getLogger(MongoDbTaskServiceTest.class);

    private DaoConfiguration daoConfiguration;
    private String tableName;

    @Override
    protected TaskService getTaskService() {
        Properties properties = FileUtils.propertiesFromResource("mongodb_multitable.properties");
        ServerConfigurationService configurationService = ServerConfigurationService.buildServerConfiguration(properties);
        daoConfiguration = configurationService.getDaoConfigurations().get(tenantId);
        tableName = daoConfiguration.getValueByKeyOrDefault(TASKS_TABLENAME, "tasks");
        CompositeTaskDao taskDao = DaoFactory.buildTaskDao(configurationService);
        return new TaskService(taskDao);
    }

    @Before
    @After
    public void beforeAndAfter() {
        logger.info("tenantId=" + tenantId);
        logger.info("tableName=" + tableName);
        logger.info("daoConfiguration=" + daoConfiguration);
        MongoDbUtils.dropCollection(tenantId, tableName, daoConfiguration);
    }
}
