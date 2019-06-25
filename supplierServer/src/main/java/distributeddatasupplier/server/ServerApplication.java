package distributeddatasupplier.server;

import configuration.ConfigurationService;
import dao.ResultDao;
import dao.TaskDao;
import distributeddatasupplier.server.network.handlers.SimpleHandler;
import distributeddatasupplier.server.network.messageTransceiver.NetworkTransceiver;
import distributeddatasupplier.server.network.selectorfactory.NetworkSelectorFactory;
import distributeddatasupplier.server.network.selectorfactory.SelectorFactory;
import distributeddatasupplier.server.services.ResultService;
import objects.Result;
import objects.ResultUri;
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
import java.util.UUID;

public class ServerApplication {

    //demo
    public static void main(String[] args) throws IOException {
        System.out.println("supplierServer started");
        ConfigurationService configurationService = new ConfigurationService();
        System.out.println(String.format("host=%s, port=%s",
                configurationService.getHost(), configurationService.getPort()));
        TaskPersistenceLayer taskPersistenceLayer = new InMemoryTaskPersistence();
        TaskDao taskDao = new TaskDao(taskPersistenceLayer);
        TaskSupplier taskSupplier = new TaskSupplier(new TaskService(taskDao));
        ResultService resultService = new ResultService(new ResultDao(new InMemoryPersistence<>()));
        MessageMarshaller messageMarshaller = new MessageMarshaller(
                new IdOnlyTaskMarshaller(), new ResultMarshaller());
        for (int i = 0; i < 32; i++) {
            taskSupplier.addTask(new Task(UUID.randomUUID().toString(), Collections.emptyMap()));
        }
        System.out.println("configurationService.getMaxExecutionTime():\t"
                + configurationService.getMaxExecutionTime());
        SelectorFactory selectorFactory = new NetworkSelectorFactory(
                configurationService.getHost(),
                configurationService.getPort());
        ServerLoop serverLoop = new ServerLoop(
                new SimpleHandler(taskSupplier,
                        resultService,
                        new NetworkTransceiver(),
                        messageMarshaller),
                selectorFactory.getSelector(),
                selectorFactory.getServerSocket(),
                configurationService.getMaxExecutionTime()
        );
        //TODO: start in separate thread
        serverLoop.start();
        System.out.println("supplierServer finished");
    }

}
