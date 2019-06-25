package server;

import dao.TaskDao;
import distributeddatasupplier.server.ServerLoop;
import distributeddatasupplier.server.network.selectorfactory.SelectorFactory;
import persistence.tasks.InMemoryTaskPersistence;
import persistence.tasks.TaskPersistenceLayer;
import distributeddatasupplier.server.services.TaskService;
import distributeddatasupplier.server.suppliers.TaskSupplier;
import mock.MockSelectorFactory;
import mocks.DumpableHandler;
import marshallers.MessageMarshaller;
import marshallers.ResultMarshaller;
import org.junit.Test;
import objects.Task;
import marshallers.IdOnlyTaskMarshaller;

import java.io.IOException;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ServerTest {

    private static MessageMarshaller getMessageMarshaller() {
        return new MessageMarshaller(new IdOnlyTaskMarshaller(), new ResultMarshaller());
    }

    private static DumpableHandler getHandler() {
        TaskPersistenceLayer taskPersistenceLayer = new InMemoryTaskPersistence();
        TaskDao taskDao = new TaskDao(taskPersistenceLayer);
        TaskSupplier taskSupplier = new TaskSupplier(new TaskService(taskDao));
        taskSupplier.addTask(new Task("MOCKEDTASKID", Collections.emptyMap()));
        return new DumpableHandler(
                taskSupplier, getMessageMarshaller()
        );
    }

    @Test(timeout=8000)
    public void testServerLoop() throws IOException {
        MessageMarshaller messageMarshaller = getMessageMarshaller();
        DumpableHandler dumpableHandler = getHandler();
        SelectorFactory selectorFactory = new MockSelectorFactory();
        ServerLoop serverLoop = new ServerLoop(
                dumpableHandler,
                selectorFactory.getSelector(),
                selectorFactory.getServerSocket(),
                1000
        );
        serverLoop.start();
        assertEquals(1, dumpableHandler.getMessagesFromClient().size());
        assertTrue(dumpableHandler.getMessagesFromClient().stream()
                .allMatch(i -> i.equals("MOCKEDCLIENTRESPONSE")));
        assertEquals(1, dumpableHandler.getMessagesFromServer().size());
        assertTrue(dumpableHandler.getMessagesFromServer().stream()
                .map(messageMarshaller::unmarshall)
                .allMatch(i -> i.getTask().getUri().getId().equals("MOCKEDTASKID")));
    }

}
