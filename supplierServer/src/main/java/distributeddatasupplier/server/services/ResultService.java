package distributeddatasupplier.server.services;

import dao.ResultDao;
import objects.Result;
import objects.ResultUri;

import java.util.Collection;

public class ResultService {

    private final ResultDao resultDao;

    public ResultService(ResultDao resultDao) {
        this.resultDao = resultDao;
    }

    public Result add(Result result) {
        resultDao.add(result);
        return result;
    }

    public Result getByUri(ResultUri resultUri) {
        return (Result) resultDao.getByUri(resultUri);
    }

    public Collection<ResultUri> getObjectUris(int responseSizeLimit) {
        return resultDao.getObjectUris(responseSizeLimit);
    }
}
