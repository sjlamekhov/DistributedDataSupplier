package server;

import distributeddatasupplier.server.ServerLoop;
import distributeddatasupplier.server.network.handlers.SimpleSysoutHandler;
import distributeddatasupplier.server.network.selectorfactory.SelectorFactory;
import distributeddatasupplier.server.suppliers.InmemoryTaskSupplier;
import mock.MockSelectorFactory;
import org.junit.Test;
import tasks.Task;
import tasks.marshallers.IdOnlyTaskMarshaller;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

public class ServerTest {

    @Test
    public void test() throws IOException {
        InmemoryTaskSupplier taskSupplier = new InmemoryTaskSupplier();
        for (int i = 0; i < 8; i++) {
            taskSupplier.addTask(new Task(UUID.randomUUID().toString(), Collections.emptyMap()));
        }
        SelectorFactory selectorFactory = new MockSelectorFactory();
        ServerLoop serverLoop = new ServerLoop(
                new SimpleSysoutHandler(
                        taskSupplier, new IdOnlyTaskMarshaller()
                ),
                selectorFactory.getSelector(),
                selectorFactory.getServerSocket(),
                1000
        );
        serverLoop.start();
    }

}
