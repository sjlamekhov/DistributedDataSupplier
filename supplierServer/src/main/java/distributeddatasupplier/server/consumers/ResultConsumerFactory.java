package distributeddatasupplier.server.consumers;

import configuration.ServerConfigurationService;
import objects.Result;
import rest.RestClient;
import rest.RestTemplateClient;

import java.util.function.Consumer;

public class ResultConsumerFactory {

    public static Consumer<Result> buildConsumer(ServerConfigurationService serverConfigurationService) {
        String resultConsumerFromConfig = serverConfigurationService.getResultConsumerType();
        ResultConsumerType resultConsumerType = ResultConsumerType.valueOf(resultConsumerFromConfig);
        if (resultConsumerType == ResultConsumerType.REST_API_CONSUMER) {
            String url = serverConfigurationService.getResultConsumerUrl();
            String template = serverConfigurationService.getResultConsumerTemplate();
            RestClient restClient = new RestTemplateClient();
            ResultTemplater resultTemplater = new ResultTemplater(template);
            return new RestApiConsumer(resultTemplater, restClient, url);
        } else {
            return r -> {};
        }
    }

}
