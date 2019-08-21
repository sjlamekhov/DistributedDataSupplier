package distributeddatasupplier.server.consumers;

import objects.Result;
import rest.RestClient;

import java.util.function.Consumer;

public class RestApiConsumer implements Consumer<Result> {

    private final ResultTemplater resultTemplater;
    private final RestClient restClient;
    private final String url;

    public RestApiConsumer(ResultTemplater resultTemplater, RestClient restClient, String url) {
        this.resultTemplater = resultTemplater;
        this.restClient = restClient;
        this.url = url;
    }

    @Override
    public void accept(Result result) {
        restClient.sendPostRequest(url, resultTemplater.apply(result));
    }
}
