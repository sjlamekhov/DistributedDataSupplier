package distributeddatasupplier.server.services;

import dao.AbstractDao;
import objects.Result;
import objects.ResultUri;

public class ResultService {

    private final AbstractDao<ResultUri, Result> resultDao;

    public ResultService(AbstractDao<ResultUri, Result> resultDao) {
        this.resultDao = resultDao;
    }

    public Result add(Result result) {
        resultDao.add(result);
        return result;
    }

    public Result getByUri(ResultUri resultUri) {
        return (Result) resultDao.getByUri(resultUri);
    }

}
