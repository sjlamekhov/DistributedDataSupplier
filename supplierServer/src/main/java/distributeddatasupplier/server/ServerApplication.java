package distributeddatasupplier.server;

import dao.DaoFactory;
import dao.ResultDao;
import dao.TaskDao;
import distributeddatasupplier.server.network.handlers.SimpleHandler;
import distributeddatasupplier.server.network.messageTransceiver.NetworkTransceiver;
import distributeddatasupplier.server.network.selectorfactory.NetworkSelectorFactory;
import distributeddatasupplier.server.network.selectorfactory.SelectorFactory;
import distributeddatasupplier.server.services.ResultService;
import configuration.ServerConfigurationService;
import marshallers.TaskUriMarshaller;
import objects.TaskUri;
import persistence.InMemoryPersistence;
import persistence.tasks.InMemoryTaskPersistence;
import persistence.tasks.TaskPersistenceLayer;
import distributeddatasupplier.server.services.TaskService;
import distributeddatasupplier.server.suppliers.TaskSupplier;
import marshallers.MessageMarshaller;
import marshallers.ResultMarshaller;
import objects.Task;
import marshallers.IdOnlyTaskMarshaller;

import java.io.IOException;
import java.util.Collections;

public class ServerApplication {

    private static final String tenantId = "tenantId";

    //demo
    public static void main(String[] args) throws IOException {
        System.out.println("supplierServer started");
        ServerConfigurationService configurationService = ServerConfigurationService.buildServerConfiguration();
        System.out.println(String.format("host=%s, port=%s",
                configurationService.getHost(), configurationService.getPort()));
        TaskSupplier taskSupplier = new TaskSupplier(new TaskService(DaoFactory.buildTaskDao(configurationService)));
        ResultService resultService = new ResultService(new ResultDao(new InMemoryPersistence<>()));
        MessageMarshaller messageMarshaller = new MessageMarshaller(
                new IdOnlyTaskMarshaller(), new ResultMarshaller(tenantId, new TaskUriMarshaller()));
        for (int i = 0; i < 32; i++) {
            taskSupplier.addTask(new Task(new TaskUri(tenantId), Collections.emptyMap()));
        }
        System.out.println("configurationService.getMaxExecutionTime():\t"
                + configurationService.getMaxExecutionTime());
        SelectorFactory selectorFactory = new NetworkSelectorFactory(
                configurationService.getHost(),
                configurationService.getPort());
        ServerLoop serverLoop = new ServerLoop(
                new SimpleHandler(
                        Collections.singleton(tenantId),
                        taskSupplier,
                        resultService,
                        new NetworkTransceiver(),
                        messageMarshaller),
                selectorFactory.getSelector(),
                selectorFactory.getServerSocket(),
                configurationService.getMaxExecutionTime(),
                configurationService.getDaoConfigurations().keySet()
        );
        //TODO: start in separate thread
        serverLoop.start();
        System.out.println("supplierServer finished");
    }

}
