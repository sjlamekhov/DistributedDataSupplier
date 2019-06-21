package distributeddatasupplier.server;

import configuration.ConfigurationService;
import distributeddatasupplier.server.network.handlers.SimpleSysoutHandler;
import distributeddatasupplier.server.network.messageTransceiver.NetworkTransceiver;
import distributeddatasupplier.server.network.selectorfactory.NetworkSelectorFactory;
import distributeddatasupplier.server.network.selectorfactory.SelectorFactory;
import distributeddatasupplier.server.storage.InmemoryTaskStorage;
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
        TaskSupplier taskSupplier = new TaskSupplier(new InmemoryTaskStorage());
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
                new SimpleSysoutHandler(taskSupplier, new NetworkTransceiver(), messageMarshaller),
                selectorFactory.getSelector(),
                selectorFactory.getServerSocket(),
                configurationService.getMaxExecutionTime()
        );
        serverLoop.start();
        System.out.println("supplierServer finished");
    }

}
