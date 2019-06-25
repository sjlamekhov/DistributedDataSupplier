package dao;

import objects.Result;
import objects.ResultUri;
import persistence.PersistenceLayer;

public class ResultDao extends AbstractDao<ResultUri, Result> {

    public ResultDao(PersistenceLayer<ResultUri, Result> persistenceLayer) {
        super(persistenceLayer);
    }

}
