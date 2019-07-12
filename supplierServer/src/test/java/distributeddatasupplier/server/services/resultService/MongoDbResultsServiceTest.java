package distributeddatasupplier.server.services.resultService;

import configuration.DaoConfiguration;
import configuration.FileUtils;
import configuration.ServerConfigurationService;
import dao.CompositeDao;
import dao.DaoFactory;
import distributeddatasupplier.server.services.ResultService;
import objects.Result;
import objects.ResultUri;
import org.junit.After;
import org.junit.Before;
import utils.MongoDbUtils;

import java.util.Properties;

import static configuration.ConfigurationConstants.TASKS_TABLENAME;

public class MongoDbResultsServiceTest extends AbstractResultServiceTest {

    private DaoConfiguration daoConfiguration;
    private String tableName;

    @Override
    protected ResultService getResultService() {
        Properties properties = FileUtils.propertiesFromResource("mongodb_multitable.properties");
        ServerConfigurationService configurationService = ServerConfigurationService.buildServerConfiguration(properties);
        daoConfiguration = configurationService.getDaoConfigurations().get(tenantId);
        tableName = daoConfiguration.getValueByKeyOrDefault(TASKS_TABLENAME, "tasks");
        CompositeDao<ResultUri, Result> resultDao = DaoFactory.buildResultDao(configurationService);
        return new ResultService(resultDao);
    }

    @Before
    @After
    public void beforeAndAfter() {
        System.out.println("tenantId=" + tenantId);
        System.out.println("tableName=" + tableName);
        System.out.println("daoConfiguration=" + daoConfiguration);
        MongoDbUtils.dropCollection(tenantId, tableName, daoConfiguration);
    }
}
