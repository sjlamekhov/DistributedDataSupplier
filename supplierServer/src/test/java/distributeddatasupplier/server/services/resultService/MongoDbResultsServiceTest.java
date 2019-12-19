package distributeddatasupplier.server.services.resultService;

import configuration.DaoConfiguration;
import configuration.FileUtils;
import configuration.ServerConfigurationService;
import dao.CompositeResultDao;
import dao.DaoFactory;
import distributeddatasupplier.server.services.ResultService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import utils.MongoDbUtils;

import java.util.Properties;

import static configuration.ConfigurationConstants.TASKS_TABLENAME;

public class MongoDbResultsServiceTest extends AbstractResultServiceTest {

    private static Logger logger = LogManager.getLogger(MongoDbResultsServiceTest.class);

    private DaoConfiguration daoConfiguration;
    private String tableName;

    @Override
    protected ResultService getResultService() {
        Properties properties = FileUtils.propertiesFromResource("mongodb_multitable.properties");
        ServerConfigurationService configurationService = ServerConfigurationService.buildServerConfiguration(properties);
        daoConfiguration = configurationService.getDaoConfigurations().get(tenantId);
        tableName = daoConfiguration.getValueByKeyOrDefault(TASKS_TABLENAME, "tasks");
        CompositeResultDao resultDao = DaoFactory.buildResultDao(configurationService);
        return new ResultService(resultDao);
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
