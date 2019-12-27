package dao;

import objects.Result;
import objects.ResultUri;

import java.util.*;

public class CompositeResultDao extends ResultDao {

    protected final Map<String, ResultDao> daoMapping;

    public CompositeResultDao() {
        super(null);
        this.daoMapping = new HashMap<>();
    }

    public ResultDao getDao(String tenantId) {
        return daoMapping.get(tenantId);
    }

    public void addDao(String tenantId, ResultDao dao) {
        if (daoMapping.containsKey(tenantId)) {
            throw new RuntimeException(String.format("DAO for %s tenant is already registered", tenantId));
        }
        daoMapping.put(tenantId, dao);
    }

    @Override
    public Collection<ResultUri> getObjectUris(String tenantId, int responseSizeLimit) {
        return getDaoInternal(tenantId).getObjectUris(tenantId, responseSizeLimit);
    }

    @Override
    public boolean isUriPresented(ResultUri uri) {
        String tenantId = uri.getTenantId();
        return getDaoInternal(tenantId).isUriPresented(uri);
    }

    @Override
    public Result add(Result object) {
        String tenantId = object.getUri().getTenantId();
        return getDaoInternal(tenantId).add(object);
    }

    @Override
    public Result update(Result object) {
        String tenantId = object.getUri().getTenantId();
        return getDaoInternal(tenantId).update(object);
    }

    @Override
    public Result getByUri(ResultUri uri) {
        String tenantId = uri.getTenantId();
        return getDaoInternal(tenantId).getByUri(uri);
    }

    @Override
    public Collection<Result> getByUris(Collection<ResultUri> documentUris) {
        Objects.requireNonNull(documentUris);
        if (documentUris.isEmpty()) {
            return Collections.emptySet();
        }
        String tenantId = documentUris.iterator().next().getTenantId();
        return getDaoInternal(tenantId).getByUris(documentUris);
    }

    @Override
    public void deleteObject(ResultUri uri) {
        Objects.requireNonNull(uri);
        String tenantId = uri.getTenantId();
        getDaoInternal(tenantId).deleteObject(uri);
    }

    private ResultDao getDaoInternal(String tenantId) {
        if (!daoMapping.containsKey(tenantId)) {
            throw new RuntimeException(String.format("DAO for tenant %s is not registered", tenantId));
        }
        ResultDao dao = daoMapping.get(tenantId);
        return dao;
    }
}
