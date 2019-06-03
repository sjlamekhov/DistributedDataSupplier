package distributeddatasupplier.server;

import configuration.ConfigurationService;
import distributeddatasupplier.server.network.handlers.SimpleSysoutHandler;
import distributeddatasupplier.server.suppliers.InmemoryTaskSupplier;
import distributeddatasupplier.server.suppliers.TaskSupplier;
import tasks.Task;
import tasks.marshallers.IdOnlyTaskMarshaller;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

public class ServerApplication {

    //demo
    public static void main(String[] args) throws IOException {
        System.out.println("supplierServer started");
        ConfigurationService configurationService = new ConfigurationService();
        System.out.println(String.format("host=%s, port=%s",
                configurationService.getHost(), configurationService.getPort()));
        InmemoryTaskSupplier taskSupplier = new InmemoryTaskSupplier();
        for (int i = 0; i < 32; i++) {
            taskSupplier.addTask(new Task(UUID.randomUUID().toString(), Collections.emptyMap()));
        }
        ServerLoop serverLoop = new ServerLoop(
                configurationService.getHost(), configurationService.getPort(),
                new SimpleSysoutHandler(
                        taskSupplier, new IdOnlyTaskMarshaller()
                )
        );
        serverLoop.start();
        System.out.println("supplierServer finished");
    }

}
