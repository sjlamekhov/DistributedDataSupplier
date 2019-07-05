package distributeddatasupplier.server.services.status;

public class ServerStatus {

    private final long numberOfAcceptRequests;
    private final long numberOfReadRequests;
    private final long numberOfWriteRequests;
    private final long numberOfProcessedTasks;
    private final long numberOfAliveClients;

    public long getNumberOfAcceptRequests() {
        return numberOfAcceptRequests;
    }

    public long getNumberOfReadRequests() {
        return numberOfReadRequests;
    }

    public long getNumberOfWriteRequests() {
        return numberOfWriteRequests;
    }

    public long getNumberOfProcessedTasks() {
        return numberOfProcessedTasks;
    }

    public long getNumberOfAliveClients() {
        return numberOfAliveClients;
    }

    public long getServerExecutionTime() {
        return serverExecutionTime;
    }

    private final long serverExecutionTime;

    public ServerStatus(long numberOfAcceptRequests,
                        long numberOfReadRequests,
                        long numberOfWriteRequests,
                        long numberOfProcessedTasks,
                        long numberOfAliveClients,
                        long serverExecutionTime) {
        this.numberOfAcceptRequests = numberOfAcceptRequests;
        this.numberOfReadRequests = numberOfReadRequests;
        this.numberOfWriteRequests = numberOfWriteRequests;
        this.numberOfProcessedTasks = numberOfProcessedTasks;
        this.numberOfAliveClients = numberOfAliveClients;
        this.serverExecutionTime = serverExecutionTime;
    }

    public static class Builder {

        private long numberOfAcceptRequests;
        private long numberOfReadRequests;
        private long numberOfWriteRequests;
        private long numberOfProcessedTasks;
        private long numberOfAliveClients;
        private long serverExecutionTime;

        public Builder() {
        }

        public Builder setNumberOfAcceptRequests(long numberOfAcceptRequests) {
            this.numberOfAcceptRequests = numberOfAcceptRequests;
            return this;
        }

        public Builder setNumberOfReadRequests(long numberOfReadRequests) {
            this.numberOfReadRequests = numberOfReadRequests;
            return this;
        }

        public Builder setNumberOfWriteRequests(long numberOfWriteRequests) {
            this.numberOfWriteRequests = numberOfWriteRequests;
            return this;
        }

        public Builder setNumberOfProcessedTasks(long numberOfProcessedTasks) {
            this.numberOfProcessedTasks = numberOfProcessedTasks;
            return this;
        }

        public Builder setNumberOfAliveClients(long numberOfAliveClients) {
            this.numberOfAliveClients = numberOfAliveClients;
            return this;
        }

        public Builder setServerExecutionTime(long serverExecutionTime) {
            this.serverExecutionTime = serverExecutionTime;
            return this;
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public ServerStatus build() {
            return new ServerStatus(numberOfAcceptRequests,
                    numberOfReadRequests,
                    numberOfWriteRequests,
                    numberOfProcessedTasks,
                    numberOfAliveClients,
                    serverExecutionTime
            );
        }

    }

}
