package distributeddatasupplier.server.platform;

import configuration.ServerConfigurationService;
import dao.*;
import distributeddatasupplier.server.network.handlers.Handler;
import distributeddatasupplier.server.network.handlers.SimpleHandler;
import distributeddatasupplier.server.network.messageTransceiver.NetworkTransceiver;
import distributeddatasupplier.server.network.messageTransceiver.Transceiver;
import distributeddatasupplier.server.services.ResultService;
import distributeddatasupplier.server.services.TaskService;
import distributeddatasupplier.server.suppliers.TaskSupplier;
import marshallers.*;
import messaging.Message;
import objects.Result;
import objects.ResultUri;
import objects.Task;
import objects.TaskUri;

import java.util.Properties;

//TODO: implement and integrate
public class PlatformFactory {

    public static Platform buildPlatformFromConfig(Properties properties) {
        ServerConfigurationService serverConfigurationService = ServerConfigurationService.buildServerConfiguration(properties);

        TaskDao compositeTaskDao = DaoFactory.buildTaskDao(serverConfigurationService);
        TaskService taskService = new TaskService(compositeTaskDao);

        CompositeDao<ResultUri, Result> resultDao = DaoFactory.buildResultDao(serverConfigurationService);
        ResultService resultService = new ResultService(resultDao);

        TaskSupplier taskSupplier = new TaskSupplier(taskService);

        Transceiver transceiver = new NetworkTransceiver();

        Marshaller<Task> taskMarshaller = new IdOnlyTaskMarshaller();
        Marshaller<TaskUri> taskUriMarshaller = new TaskUriMarshaller();
        Marshaller<Result> resultMarshaller = new ResultMarshaller("", taskUriMarshaller);
        Marshaller<Message> messageMarshaller = new MessageMarshaller(taskMarshaller, resultMarshaller);

        Handler handler = new SimpleHandler(
                serverConfigurationService.getTenantIds(),
                taskSupplier,
                resultService,
                transceiver,
                messageMarshaller
                );

        return Platform.Builder
                .newInstance()
                .setTaskService(taskService)
                .setResultService(resultService)
                .setHandler(handler)
                .setResultMarshaller(resultMarshaller)
                .setTaskMarshaller(taskMarshaller)
                .setServerConfigurationService(serverConfigurationService)
                .setTaskSupplier(taskSupplier)
                .build();
    }

}
