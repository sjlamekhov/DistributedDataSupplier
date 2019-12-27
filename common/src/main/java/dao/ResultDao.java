package dao;

import objects.Result;
import objects.ResultUri;
import persistence.PersistenceLayer;

import java.util.Collection;

public class ResultDao extends AbstractDao<ResultUri, Result> {

    public ResultDao(PersistenceLayer<ResultUri, Result> persistenceLayer) {
        super(persistenceLayer);
    }

    public Collection<ResultUri> getObjectUris(String tenantId, int responseSizeLimit) {
        return persistenceLayer.getObjectUris(responseSizeLimit);
    }

}
