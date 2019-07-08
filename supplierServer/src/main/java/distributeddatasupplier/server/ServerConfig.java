package distributeddatasupplier.server;

import configuration.ConfigProvider;
import configuration.ConfigurationService;
import configuration.ServerConfigurationService;
import distributeddatasupplier.server.network.selectorfactory.NetworkSelectorFactory;
import distributeddatasupplier.server.network.selectorfactory.SelectorFactory;
import distributeddatasupplier.server.platform.Platform;
import distributeddatasupplier.server.platform.PlatformFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Properties;

@Configuration
@EnableAsync
public class ServerConfig {

    @Autowired
    private Platform platform;

    @Autowired
    private ServerConfigurationService serverConfigurationService;

    private final Properties properties;

    public ServerConfig() {
        properties = ConfigProvider.getProperties();
    }

    @Bean(name = "platform")
    public Platform getPlatform() {
        return PlatformFactory.buildPlatformFromConfig(properties);
    }

    @Bean(name = "serverConfigurationService")
    @DependsOn("platform")
    public ServerConfigurationService getConfigurationService() {
        return platform.getServerConfigurationService();
    }

    @Bean(name = "serverLoop")
    @DependsOn("serverConfigurationService")
    public ServerLoop getServerLoop() {
        ServerLoop serverLoopToPublish = null;
        try {
            SelectorFactory selectorFactory = new NetworkSelectorFactory(
                    serverConfigurationService.getHost(),
                    serverConfigurationService.getPort());
            final ServerLoop serverLoop = new ServerLoop(
                    platform.getHandler(),
                    selectorFactory.getSelector(),
                    selectorFactory.getServerSocket(),
                    serverConfigurationService.getMaxExecutionTime(),
                    serverConfigurationService.getDaoConfigurations().keySet()
            );
            serverLoop.start();
            System.out.println("ServerLoop created");
            return serverLoop;
        } catch (Exception ignore) {}
        return null;
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(8);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(24);
        return executor;
    }


}
