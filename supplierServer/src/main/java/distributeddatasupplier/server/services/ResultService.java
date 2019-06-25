package distributeddatasupplier.server.services;

import dao.ResultDao;
import objects.Result;
import objects.ResultUri;

public class ResultService {

    private final ResultDao resultDao;

    public ResultService(ResultDao resultDao) {
        this.resultDao = resultDao;
    }

    public void add(Result result) {
        resultDao.add(result);
    }

    public Result getByUri(ResultUri resultUri) {
        return resultDao.getByUri(resultUri);
    }

}
