package distributeddatasupplier.server.services;

import dao.AbstractDao;
import dao.ResultDao;
import objects.Result;
import objects.ResultUri;

public class ResultService {

    private final AbstractDao<ResultUri, Result> resultDao;

    public ResultService(AbstractDao<ResultUri, Result> resultDao) {
        this.resultDao = resultDao;
    }

    public void add(Result result) {
        resultDao.add(result);
    }

    public Result getByUri(ResultUri resultUri) {
        return (Result) resultDao.getByUri(resultUri);
    }

}
