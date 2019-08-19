package marshallers;

import objects.ResultUri;

public class ResultUriMarshaller implements Marshaller<ResultUri> {

    private static final String SEPARATOR = "@=@";

    @Override
    public String marshall(ResultUri object) {
        return object.getId() + SEPARATOR + object.getTenantId();
    }

    @Override
    public ResultUri unmarshall(String string) {
        String[] splitted = string.split(SEPARATOR);
        return new ResultUri(splitted[0], splitted[1]);
    }
}
