package distributeddatasupplier.server.network.handlers;

import distributeddatasupplier.server.persistence.InMemoryTaskPersistence;
import distributeddatasupplier.server.persistence.TaskPersistenceLayer;
import distributeddatasupplier.server.storage.TaskStorageImplementation;
import distributeddatasupplier.server.storage.TaskStorage;
import distributeddatasupplier.server.suppliers.TaskSupplier;
import marshallers.IdOnlyTaskMarshaller;
import marshallers.MessageMarshaller;
import marshallers.ResultMarshaller;
import messaging.FlowControl;
import messaging.Message;
import mock.MockSelectorFactory;
import mocks.TranceiverMock;
import objects.Result;
import objects.Task;
import objects.TaskStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

public class SimpleSysoutHandlerTest {

    private MockSelectorFactory mockSelectorFactory = new MockSelectorFactory();
    private SimpleSysoutHandler simpleSysoutHandler = null;
    private TaskStorage taskStorage;
    private MessageMarshaller messageMarshaller;
    private TaskSupplier taskSupplier;
    private ArrayDeque<String> sendedMessages = new ArrayDeque<>();
    private ArrayDeque<String> receivedMessages = new ArrayDeque<>();

    private final static int TASK_NUMBER = 4;
    private final static String TASK_PREFIX = "TASK_ID_";

    private static List<Task> getTestTasks() {
        List<Task> result = new ArrayList<>();
        for (int i = 0; i < TASK_NUMBER; i++) {
            result.add(new Task(TASK_PREFIX + i, Collections.emptyMap()));
        }
        return result;
    }

    private static void fillTestData(TaskStorage taskStorage) {
        getTestTasks().forEach(taskStorage::addTask);
    }

    @Test
    public void handleReadable() throws IOException {
        Assert.assertFalse(receivedMessages.isEmpty());
        while (!receivedMessages.isEmpty()) {
            simpleSysoutHandler.handleReadable(
                    mockSelectorFactory.getSelector(),
                    mockSelectorFactory.getServerSocket(),
                    MockSelectorFactory.getReadableKey()
            );
        }
        Assert.assertTrue(receivedMessages.isEmpty());
        //TODO: implement after logic for storing results
    }

    @Test
    public void handleWritable() throws IOException {
        Assert.assertTrue(!taskStorage.isEmpty(TaskStatus.NOT_STARTED));
        while (!taskSupplier.isEmpty()) {
            simpleSysoutHandler.handleWritable(
                    mockSelectorFactory.getSelector(),
                    mockSelectorFactory.getServerSocket(),
                    MockSelectorFactory.getReadableKey()
            );
        }
        Assert.assertTrue(taskStorage.isEmpty(TaskStatus.NOT_STARTED));
        Assert.assertEquals(TASK_NUMBER, sendedMessages.size());
    }

    @Before
    public void init() {
        TaskPersistenceLayer taskPersistenceLayer = new InMemoryTaskPersistence();
        taskStorage = new TaskStorageImplementation(taskPersistenceLayer);
        fillTestData(taskStorage);
        taskSupplier = new TaskSupplier(taskStorage);
        messageMarshaller = new MessageMarshaller(new IdOnlyTaskMarshaller(), new ResultMarshaller());
        fillTestMessages(sendedMessages, receivedMessages);
        simpleSysoutHandler = new SimpleSysoutHandler(
                taskSupplier,
                new TranceiverMock(messageMarshaller, sendedMessages, receivedMessages),
                messageMarshaller);
    }

    private void fillTestMessages(ArrayDeque<String> sendedMessages, ArrayDeque<String> receivedMessages) {
        sendedMessages.clear();
        receivedMessages.clear();
        getTestTasks().stream()
                .map(t -> messageMarshaller.marshall(new Message(new Result(t.getUri(), Collections.EMPTY_MAP), FlowControl.GETNEXTTASK)))
                .forEach(receivedMessages::add);
    }
}