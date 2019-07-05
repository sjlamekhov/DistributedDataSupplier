package distributeddatasupplier.server.services.status;

public class ServerStatusService {

    private final long startTime;
    private long numberOfAcceptRequests = 0;
    private long numberOfReadRequests = 0;
    private long numberOfWriteRequests = 0;
    private long numberOfAliveClients = 0;
    private long numberOfProcessedTasks = 0;

    public ServerStatusService() {
        this.startTime = System.currentTimeMillis();
    }

    public synchronized void incrementNumberOfAcceptRequests() {
        numberOfAcceptRequests++;
    }

    public synchronized void incrementNumberOfReadRequests() {
        numberOfReadRequests++;
    }

    public synchronized void incrementNumberOfWriteRequests() {
        numberOfWriteRequests++;
    }

    public synchronized void setNumberOfAliveClients(long numberOfAliveClients) {
        this.numberOfAliveClients = numberOfAliveClients;
    }

    public synchronized void setNumberOfProcessedTasks(long numberOfProcessedTasks) {
        this.numberOfProcessedTasks = numberOfProcessedTasks;
    }

    public synchronized long getServerExecutionTime() {
        return System.currentTimeMillis() - startTime;
    }

    public synchronized ServerStatus getServerStatus() {
        return new ServerStatus.Builder().newInstance()
                .setNumberOfAcceptRequests(numberOfAcceptRequests)
                .setNumberOfReadRequests(numberOfReadRequests)
                .setNumberOfWriteRequests(numberOfWriteRequests)
                .setNumberOfAliveClients(numberOfAliveClients)
                .setNumberOfProcessedTasks(numberOfProcessedTasks)
                .setServerExecutionTime(getServerExecutionTime())
                .build();
    }

}
