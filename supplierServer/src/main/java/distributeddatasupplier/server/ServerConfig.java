package distributeddatasupplier.server;

import configuration.ConfigProvider;
import configuration.ServerConfigurationService;
import distributeddatasupplier.server.network.selectorfactory.NetworkSelectorFactory;
import distributeddatasupplier.server.network.selectorfactory.SelectorFactory;
import distributeddatasupplier.server.platform.Platform;
import distributeddatasupplier.server.platform.PlatformFactory;
import distributeddatasupplier.server.services.ResultService;
import distributeddatasupplier.server.services.TaskService;
import distributeddatasupplier.server.services.status.ServerStatusService;
import objects.Task;
import objects.TaskUri;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.UUID;

@Configuration
@EnableAsync
public class ServerConfig {

    @Autowired
    private Platform platform;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private ServerConfigurationService serverConfigurationService;

    private final Properties properties;

    public ServerConfig() {
        properties = ConfigProvider.getProperties();
    }

    @Bean(name = "platform")
    @DependsOn("taskExecutor")
    public Platform getPlatform() {
        return PlatformFactory.buildPlatformFromConfig(properties);
    }

    @Bean(name = "serverConfigurationService")
    @DependsOn("platform")
    public ServerConfigurationService getConfigurationService() {
        return platform.getServerConfigurationService();
    }

    @Bean(name = "serverStatusService")
    @DependsOn("platform")
    public ServerStatusService getServerStatusService() {
        return platform.getServerStatusService();
    }

    @Bean(name = "resultService")
    @DependsOn("platform")
    public ResultService getResultService() {
        return platform.getResultService();
    }

    @Bean(name = "taskService")
    @DependsOn("platform")
    public TaskService getTaskService() {
        return platform.getTaskService();
    }

    @Bean(name = "serverLoop")
    @DependsOn("serverConfigurationService")
    public ServerLoop getServerLoop() {
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
            taskExecutor.execute(() -> {
                try {
                    serverLoop.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            //DEMO MODE
            TaskService taskService = platform.getTaskService();
            for (int i = 0; i < 16; i++) {
                Task task = new Task(new TaskUri(UUID.randomUUID().toString(), "tenantId"), new HashMap<>());
                System.out.println("taskId:\t" + task.getUri());
                taskService.add(task);
                System.out.println("taskId:\t" + task.getUri() + "added");
            }
            //
            return serverLoop;
        } catch (Exception ignore) {}
        return null;
    }

    @Bean(name = "taskExecutor")
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(8);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(24);
        return executor;
    }

}
