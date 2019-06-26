package distributeddatasupplier.server.network.handlers;

import dao.CompositeTaskDao;
import dao.ResultDao;
import dao.TaskDao;
import distributeddatasupplier.server.services.ResultService;
import marshallers.TaskUriMarshaller;
import objects.*;
import persistence.InMemoryPersistence;
import persistence.tasks.InMemoryTaskPersistence;
import persistence.tasks.TaskPersistenceLayer;
import distributeddatasupplier.server.services.TaskService;
import distributeddatasupplier.server.suppliers.TaskSupplier;
import marshallers.IdOnlyTaskMarshaller;
import marshallers.MessageMarshaller;
import marshallers.ResultMarshaller;
import messaging.FlowControl;
import messaging.Message;
import mock.MockSelectorFactory;
import mocks.TranceiverMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

public class SimpleHandlerTest {

    protected static final String tenantId = "testTenantId";

    private MockSelectorFactory mockSelectorFactory = new MockSelectorFactory();
    private SimpleHandler simpleHandler = null;
    private MessageMarshaller messageMarshaller;

    private TaskService taskService;
    private TaskSupplier taskSupplier;

    private InMemoryPersistence<ResultUri, Result> resultPersistence;
    private ResultService resultService;

    private ArrayDeque<String> sendedMessages = new ArrayDeque<>();
    private ArrayDeque<String> receivedMessages = new ArrayDeque<>();

    private final static int TASK_NUMBER = 4;
    private final static String TASK_PREFIX = "TASK_ID_";

    private static List<Task> getTestTasks() {
        List<Task> result = new ArrayList<>();
        for (int i = 0; i < TASK_NUMBER; i++) {
            result.add(new Task(new TaskUri(TASK_PREFIX + i, tenantId), Collections.emptyMap()));
        }
        return result;
    }

    private static void fillTestData(TaskService taskService) {
        getTestTasks().forEach(taskService::add);
    }

    @Test
    public void handleReadable() throws IOException {
        Assert.assertFalse(receivedMessages.isEmpty());
        while (!receivedMessages.isEmpty()) {
            simpleHandler.handleReadable(
                    tenantId,
                    mockSelectorFactory.getSelector(),
                    mockSelectorFactory.getServerSocket(),
                    MockSelectorFactory.getReadableKey()
            );
        }
        Assert.assertTrue(receivedMessages.isEmpty());
        Assert.assertEquals(TASK_NUMBER, getSizeOfIterator(resultPersistence.getUriIterator()));
    }

    @Test
    public void handleWritable() throws IOException {
        Assert.assertTrue(!taskService.isEmpty(tenantId, TaskStatus.NOT_STARTED));
        while (!taskSupplier.isEmpty(tenantId)) {
            simpleHandler.handleWritable(
                    tenantId,
                    mockSelectorFactory.getSelector(),
                    mockSelectorFactory.getServerSocket(),
                    MockSelectorFactory.getReadableKey()
            );
        }
        Assert.assertTrue(taskService.isEmpty(tenantId, TaskStatus.NOT_STARTED));
        Assert.assertEquals(TASK_NUMBER, sendedMessages.size());
    }

    @Before
    public void init() {
        TaskPersistenceLayer taskPersistenceLayer = new InMemoryTaskPersistence();
        CompositeTaskDao compositeTaskDao = new CompositeTaskDao();
        compositeTaskDao.addDao(tenantId, new TaskDao(taskPersistenceLayer));
        taskService = new TaskService(compositeTaskDao);
        fillTestData(taskService);
        resultPersistence = new InMemoryPersistence<>();
        resultService = new ResultService(new ResultDao(resultPersistence));
        taskSupplier = new TaskSupplier(taskService);
        messageMarshaller = new MessageMarshaller(new IdOnlyTaskMarshaller(),
                new ResultMarshaller(tenantId, new TaskUriMarshaller()));
        fillTestMessages(sendedMessages, receivedMessages);
        simpleHandler = new SimpleHandler(
                taskSupplier,
                resultService,
                new TranceiverMock(messageMarshaller, sendedMessages, receivedMessages),
                messageMarshaller);
    }

    private void fillTestMessages(ArrayDeque<String> sendedMessages, ArrayDeque<String> receivedMessages) {
        sendedMessages.clear();
        receivedMessages.clear();
        getTestTasks().stream()
                .map(t -> messageMarshaller.marshall(new Message(new Result(new ResultUri(tenantId), t.getUri(), Collections.EMPTY_MAP), FlowControl.GETNEXTTASK)))
                .forEach(receivedMessages::add);
    }

    private int getSizeOfIterator(Iterator<?> iterator) {
        Assert.assertNotNull(iterator);
        int count = 0;
        while (iterator.hasNext()) {
            iterator.next();
            count++;
        }
        return count;
    }
}