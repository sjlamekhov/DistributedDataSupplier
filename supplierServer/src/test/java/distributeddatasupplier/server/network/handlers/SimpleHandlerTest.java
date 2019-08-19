package distributeddatasupplier.server.network.handlers;

import dao.CompositeTaskDao;
import dao.ResultDao;
import dao.TaskDao;
import distributeddatasupplier.server.services.ResultService;
import distributeddatasupplier.server.services.status.ServerStatusService;
import marshallers.*;
import mocks.ResultConsumerMock;
import objects.*;
import persistence.InMemoryPersistence;
import persistence.converters.ResultConverter;
import persistence.converters.TaskConverter;
import persistence.tasks.InMemoryTaskPersistence;
import persistence.tasks.TaskPersistenceLayer;
import distributeddatasupplier.server.services.TaskService;
import distributeddatasupplier.server.suppliers.TaskSupplier;
import messaging.FlowControl;
import messaging.Message;
import mocks.MockSelectorFactory;
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
    private ResultConsumerMock resultConsumer;
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
                    mockSelectorFactory.getSelector(),
                    mockSelectorFactory.getServerSocket(),
                    MockSelectorFactory.getReadableKey()
            );
        }
        Assert.assertTrue(receivedMessages.isEmpty());
        List<Result> acceptedResults = resultConsumer.getAcceptedResults();
        Assert.assertEquals(TASK_NUMBER, acceptedResults.size());
    }

    @Test
    public void handleWritable() throws IOException {
        Assert.assertTrue(!taskService.isEmpty(tenantId, TaskStatus.NOT_STARTED));
        while (!taskSupplier.isEmpty(tenantId)) {
            simpleHandler.handleWritable(
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
        TaskPersistenceLayer taskPersistenceLayer = new InMemoryTaskPersistence(new TaskConverter(tenantId));
        CompositeTaskDao compositeTaskDao = new CompositeTaskDao();
        compositeTaskDao.addDao(tenantId, new TaskDao(taskPersistenceLayer));
        taskService = new TaskService(compositeTaskDao);
        fillTestData(taskService);
        resultPersistence = new InMemoryPersistence<>(new ResultConverter(tenantId));
        resultService = new ResultService(new ResultDao(resultPersistence));
        resultConsumer = new ResultConsumerMock();
        taskSupplier = new TaskSupplier(taskService);
        messageMarshaller = new MessageMarshaller(new IdOnlyTaskMarshaller(),
                new ResultMarshaller(new ResultUriMarshaller(), new TaskUriMarshaller()));
        fillTestMessages(sendedMessages, receivedMessages);
        ServerStatusService serverStatusService = new ServerStatusService();
        simpleHandler = new SimpleHandler(
                Collections.singleton(tenantId),
                taskSupplier,
                resultService,
                resultConsumer,
                new TranceiverMock(messageMarshaller, sendedMessages, receivedMessages),
                messageMarshaller, serverStatusService);
    }

    private void fillTestMessages(ArrayDeque<String> sentMessages, ArrayDeque<String> receivedMessages) {
        sentMessages.clear();
        receivedMessages.clear();
        getTestTasks().stream()
                .map(t -> messageMarshaller.marshall(new Message(new Result(new ResultUri(tenantId), t.getUri(), Collections.EMPTY_MAP), FlowControl.GETNEXTTASK)))
                .forEach(receivedMessages::add);
    }

}