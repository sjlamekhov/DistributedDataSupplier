package dao;

import objects.*;

import java.util.*;

public class CompositeTaskDao extends TaskDao {

    protected final Map<String,TaskDao> daoMapping;

    public CompositeTaskDao() {
        super(null);
        this.daoMapping = new HashMap<>();
    }

    public void addDao(String tenantId, TaskDao dao) {
        if (daoMapping.containsKey(tenantId)) {
            throw new RuntimeException(String.format("DAO for %s tenant is already registered", tenantId));
        }
        daoMapping.put(tenantId, dao);
    }

    @Override
    public boolean isUriPresented(TaskUri uri) {
        String tenantId = uri.getTenantId();
        return getDaoInternal(tenantId).isUriPresented(uri);
    }

    @Override
    public Task add(Task object) {
        String tenantId = object.getUri().getTenantId();
        return getDaoInternal(tenantId).add(object);
    }

    @Override
    public Task update(Task object) {
        String tenantId = object.getUri().getTenantId();
        return getDaoInternal(tenantId).update(object);
    }

    @Override
    public Task getByUri(TaskUri uri) {
        String tenantId = uri.getTenantId();
        return getDaoInternal(tenantId).getByUri(uri);
    }

    @Override
    public Collection<Task> getByUris(Collection<TaskUri> documentUris) {
        Objects.requireNonNull(documentUris);
        if (documentUris.isEmpty()) {
            return Collections.emptySet();
        }
        String tenantId = documentUris.iterator().next().getTenantId();
        return getDaoInternal(tenantId).getByUris(documentUris);
    }

    @Override
    public void deleteObject(TaskUri uri) {
        Objects.requireNonNull(uri);
        String tenantId = uri.getTenantId();
        getDaoInternal(tenantId).deleteObject(uri);
    }

    @Override
    public boolean hasTasksWithStatus(String tenantId, TaskStatus taskStatus) {
        return getDaoInternal(tenantId).hasTasksWithStatus(tenantId, taskStatus);
    }

    @Override
    public Collection<Task> getTasksByStatus(String tenantId, TaskStatus taskStatus, int limit) {
        return getDaoInternal(tenantId).getTasksByStatus(tenantId, taskStatus, limit);
    }

    @Override
    public void setTaskStatus(TaskUri taskUri, TaskStatus taskStatus) {
        getDaoInternal(Objects.requireNonNull(taskUri).getTenantId()).setTaskStatus(taskUri, taskStatus);
    }

    private TaskDao getDaoInternal(String tenantId) {
        if (!daoMapping.containsKey(tenantId)) {
            throw new RuntimeException(String.format("DAO for tenant %s is not registered", tenantId));
        }
        return daoMapping.get(tenantId);
    }
}