package distributeddatasupplier.server.platform;

import configuration.ServerConfigurationService;
import distributeddatasupplier.server.network.handlers.Handler;
import distributeddatasupplier.server.services.ResultService;
import distributeddatasupplier.server.services.TaskService;
import distributeddatasupplier.server.suppliers.TaskSupplier;
import marshallers.Marshaller;
import messaging.Message;
import objects.Result;
import objects.Task;

public class Platform {

    private final Marshaller<Task> taskMarshaller;
    private final Marshaller<Result> resultMarshaller;
    private final Marshaller<Message> messageMarshaller;
    private final ResultService resultService;
    private final TaskService taskService;
    private final TaskSupplier taskSupplier;
    private final ServerConfigurationService serverConfigurationService;
    private final Handler handler;

    private Platform(Marshaller<Task> taskMarshaller, Marshaller<Result> resultMarshaller, Marshaller<Message> messageMarshaller,
                     ResultService resultService, TaskService taskService, TaskSupplier taskSupplier,
                     ServerConfigurationService serverConfigurationService, Handler handler) {
        this.taskMarshaller = taskMarshaller;
        this.resultMarshaller = resultMarshaller;
        this.messageMarshaller = messageMarshaller;
        this.resultService = resultService;
        this.taskService = taskService;
        this.taskSupplier = taskSupplier;
        this.serverConfigurationService = serverConfigurationService;
        this.handler = handler;
    }

    public Marshaller<Task> getTaskMarshaller() {
        return taskMarshaller;
    }

    public Marshaller<Result> getResultMarshaller() {
        return resultMarshaller;
    }

    public Marshaller<Message> getMessageMarshaller() {
        return messageMarshaller;
    }

    public ResultService getResultService() {
        return resultService;
    }

    public TaskService getTaskService() {
        return taskService;
    }

    public TaskSupplier getTaskSupplier() {
        return taskSupplier;
    }

    public ServerConfigurationService getServerConfigurationService() {
        return serverConfigurationService;
    }

    public Handler getHandler() {
        return handler;
    }

    public static class Builder {

        private Marshaller<Task> taskMarshaller;
        private Marshaller<Result> resultMarshaller;
        private Marshaller<Message> messageMarshaller;
        private ResultService resultService;
        private TaskService taskService;
        private TaskSupplier taskSupplier;
        private ServerConfigurationService serverConfigurationService;
        private Handler handler;

        protected Builder() {
        }

        public Builder setTaskMarshaller(Marshaller<Task> taskMarshaller) {
            this.taskMarshaller = taskMarshaller;
            return this;
        }

        public Builder setResultMarshaller(Marshaller<Result> resultMarshaller) {
            this.resultMarshaller = resultMarshaller;
            return this;
        }

        public Builder setMessageMarshaller(Marshaller<Message> messageMarshaller) {
            this.messageMarshaller = messageMarshaller;
            return this;
        }

        public Builder setResultService(ResultService resultService) {
            this.resultService = resultService;
            return this;
        }

        public Builder setTaskService(TaskService taskService) {
            this.taskService = taskService;
            return this;
        }

        public Builder setTaskSupplier(TaskSupplier taskSupplier) {
            this.taskSupplier = taskSupplier;
            return this;
        }

        public Builder setServerConfigurationService(ServerConfigurationService serverConfigurationService) {
            this.serverConfigurationService = serverConfigurationService;
            return this;
        }

        public Builder setHandler(Handler handler) {
            this.handler = handler;
            return this;
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public Platform build() {
            return new Platform(taskMarshaller, resultMarshaller, messageMarshaller,
                    resultService, taskService, taskSupplier,
                    serverConfigurationService,
                    handler);
        }
    }

}
