package mocks;

import objects.Result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class ResultConsumerMock implements Consumer<Result> {

    private final List<Result> acceptedResults = new ArrayList<>();

    @Override
    public void accept(Result result) {
        acceptedResults.add(result);
    }

    public List<Result> getAcceptedResults() {
        return Collections.unmodifiableList(acceptedResults);
    }

}
