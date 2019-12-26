package configuration;

public interface ConfigurationConstants {

    String DAO_PREFIX = "dao";
    String DAO_TYPE = "type";
    String DAO_TYPE_INMEMORY = "inmemory";
    String DAO_TYPE_MONGODB = "multitableMongoDao";

    String DAO_CONFIG_HOST = "host";
    String DAO_DB_NAME = "dbname";

    String TASKS_TABLENAME = "tasks";
    String RESULTS_TABLENAME = "results";

    String RESULT_CONSUMER_TYPE ="resultConsumerType";
    String RESULT_CONSUMER_NONE ="NO_CONSUMER";
    String RESULT_CONSUMER_REST ="REST_API_CONSUMER";
    String RESULT_CONSUMER_TEMPLATE ="resultConsumerTemplate";
    String RESULT_CONSUMER_URL ="resultConsumerUrl";

    String DEMO_MODE = "demoMode";

    String TENANT_ID = "tenantId";
    String NEW_TASK_ATTEMPTS = "newTaskAttempts";
    String NEW_TASK_ATTEMPT_PAUSE = "newTaskAttemptPause";
    String NUMBER_OF_CYCLES_TO_PROCESS = "numberOfCyclesToProcess";
}
