package distributeddatasupplier.server.services.resultService;

import configuration.FileUtils;
import configuration.ServerConfigurationService;
import dao.CompositeResultDao;
import dao.DaoFactory;
import distributeddatasupplier.server.services.ResultService;

import java.util.Properties;

public class InMemoryResultServiceTest extends AbstractResultServiceTest {

    @Override
    protected ResultService getResultService() {
        Properties properties = FileUtils.propertiesFromResource("inmemory_multitable.properties");
        ServerConfigurationService configurationService = ServerConfigurationService.buildServerConfiguration(properties);
        CompositeResultDao resultDao = DaoFactory.buildResultDao(configurationService);
        return new ResultService(resultDao);
    }
}
