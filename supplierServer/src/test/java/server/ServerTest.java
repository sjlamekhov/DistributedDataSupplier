package server;

import dao.CompositeTaskDao;
import dao.TaskDao;
import distributeddatasupplier.server.ServerLoop;
import distributeddatasupplier.server.network.selectorfactory.SelectorFactory;
import marshallers.*;
import objects.TaskUri;
import persistence.converters.TaskConverter;
import persistence.tasks.InMemoryTaskPersistence;
import persistence.tasks.TaskPersistenceLayer;
import distributeddatasupplier.server.services.TaskService;
import distributeddatasupplier.server.suppliers.TaskSupplier;
import mocks.MockSelectorFactory;
import mocks.DumpableHandler;
import org.junit.Test;
import objects.Task;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ServerTest {

    private static final String tenantId = "tenantId";

    private static MessageMarshaller getMessageMarshaller() {
        return new MessageMarshaller(new IdOnlyTaskMarshaller(),
                new ResultMarshaller(new ResultUriMarshaller(), new TaskUriMarshaller()));
    }

    private static DumpableHandler getHandler() {
        TaskPersistenceLayer taskPersistenceLayer = new InMemoryTaskPersistence(new TaskConverter(tenantId));
        TaskDao taskDao = new TaskDao(taskPersistenceLayer);
        CompositeTaskDao compositeTaskDao = new CompositeTaskDao();
        compositeTaskDao.addDao(tenantId, taskDao);
        TaskSupplier taskSupplier = new TaskSupplier(new TaskService(compositeTaskDao));
        taskSupplier.addTask(new Task(new TaskUri("MOCKEDTASKID", tenantId), Collections.emptyMap()));
        return new DumpableHandler(tenantId, taskSupplier, getMessageMarshaller()
        );
    }

    @Test(timeout=60000)
    public void testServerLoop() throws IOException {
        MessageMarshaller messageMarshaller = getMessageMarshaller();
        DumpableHandler dumpableHandler = getHandler();
        SelectorFactory selectorFactory = new MockSelectorFactory();
        ServerLoop serverLoop = new ServerLoop(
                dumpableHandler,
                selectorFactory.getSelector(),
                selectorFactory.getServerSocket(),
                1000,
                new HashSet<>()
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
