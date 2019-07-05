package distributeddatasupplier.server.services.status;

import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;

import static org.junit.Assert.*;

public class ServerStatusServiceTest {

    @Test
    public void incrementCounters() {
        ServerStatusService serverStatusService = new ServerStatusService();
        ServerStatus serverStatus = serverStatusService.getServerStatus();
        assertEquals(0, serverStatus.getNumberOfAcceptRequests());
        assertEquals(0, serverStatus.getNumberOfReadRequests());
        assertEquals(0, serverStatus.getNumberOfWriteRequests());
        serverStatusService.incrementNumberOfAcceptRequests();
        serverStatusService.incrementNumberOfReadRequests();
        serverStatusService.incrementNumberOfWriteRequests();
        serverStatus = serverStatusService.getServerStatus();
        assertEquals(1, serverStatus.getNumberOfAcceptRequests());
        assertEquals(1, serverStatus.getNumberOfReadRequests());
        assertEquals(1, serverStatus.getNumberOfWriteRequests());
    }

    @Test
    public void setNumberOfAliveClients() {
        ServerStatusService serverStatusService = new ServerStatusService();
        ServerStatus serverStatus = serverStatusService.getServerStatus();
        assertEquals(0, serverStatus.getNumberOfAliveClients());
        long newValue = RandomUtils.nextLong();
        serverStatusService.setNumberOfAliveClients(newValue);
        serverStatus = serverStatusService.getServerStatus();
        assertEquals(newValue, serverStatus.getNumberOfAliveClients());
    }

    @Test
    public void setNumberOfProcessedTasks() {
        ServerStatusService serverStatusService = new ServerStatusService();
        ServerStatus serverStatus = serverStatusService.getServerStatus();
        assertEquals(0, serverStatus.getNumberOfProcessedTasks());
        long newValue = RandomUtils.nextLong();
        serverStatusService.setNumberOfProcessedTasks(newValue);
        serverStatus = serverStatusService.getServerStatus();
        assertEquals(newValue, serverStatus.getNumberOfProcessedTasks());
    }

    @Test
    public void getServerExecutionTime() {
        long sleepTime = 10000;
        ServerStatusService serverStatusService = new ServerStatusService();
        try {Thread.sleep(sleepTime);} catch (Exception ignore) {}
        ServerStatus serverStatus = serverStatusService.getServerStatus();
        assertTrue(serverStatus.getServerExecutionTime() >= sleepTime);
    }

}