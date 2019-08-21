package distributeddatasupplier.server.consumers;

import objects.Result;

import java.util.function.Function;

public class ResultTemplater implements Function<Result, String> {

    private final String template;

    public ResultTemplater(String template) {
        this.template = template;
    }

    @Override
    public String apply(Result result) {
        return null;
    }
}
