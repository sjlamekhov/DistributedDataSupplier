package persistence.tasks;

import com.mongodb.*;
import configuration.DaoConfiguration;
import objects.AbstractObjectUri;
import objects.Task;
import objects.TaskStatus;
import objects.TaskUri;
import persistence.converters.ObjectConverter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static configuration.ConfigurationConstants.DAO_CONFIG_HOST;
import static configuration.ConfigurationConstants.DAO_DB_NAME;

public class MongoDbTaskPersistency implements TaskPersistenceLayer  {

    private final DaoConfiguration daoConfiguration;
    private final ObjectConverter<Task, BasicDBObject> converter;
    private final String tableName;
    private final String tenantId;
    private DBCollection collection;

    public MongoDbTaskPersistency(DaoConfiguration daoConfiguration,
                              ObjectConverter<Task, BasicDBObject> converter,
                              String tenantId,
                              String tableName) {
        this.daoConfiguration = daoConfiguration;
        this.converter = converter;
        this.tenantId = tenantId;
        this.tableName = tableName;
        init();
    }

    private void init() {
        String[] hostAndPortFromConfig = daoConfiguration
                .getValueByKeyOrDefault(DAO_CONFIG_HOST, "localhost:27017").split(":");
        String host = hostAndPortFromConfig[0];
        int port = Integer.parseInt(hostAndPortFromConfig[1]);
        MongoClient mongoClient = new MongoClient(host, port);
        DB database = mongoClient.getDB(daoConfiguration.getValueByKeyOrDefault(DAO_DB_NAME, "dbname"));
        this.collection = database.getCollection(tableName + "_" + tenantId);
    }

    @Override
    public Collection<Task> getTasksByStatus(TaskStatus taskStatus, int limit) {
        List<Task> result = new ArrayList<>();
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("taskStatus", taskStatus.toString());
        DBCursor cursor = collection.find(searchQuery);
        while (cursor.hasNext() && result.size() < limit) {
            result.add(converter.buildObjectFromTO((BasicDBObject) cursor.next()));
        }
        return result;
    }

    @Override
    public void setTaskStatus(TaskUri taskUri, TaskStatus taskStatus) {
        Task task = getByUri(taskUri);
        Objects.requireNonNull(task);
        if (task.getTaskStatus() != taskStatus) {
            task.setTaskStatus(taskStatus);
            update(task);
        }
    }

    @Override
    public boolean hasTasksWithStatus(TaskStatus taskStatus) {
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("taskStatus", taskStatus.toString());
        DBCursor cursor = collection.find(searchQuery);
        return cursor.hasNext();
    }

    @Override
    public boolean isUriPresented(TaskUri uri) {
        String objectId = uri.getId();
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("_id", objectId);
        DBCursor cursor = collection.find(searchQuery);
        return cursor.hasNext();
    }

    @Override
    public Task add(Task object) {
        Objects.requireNonNull(object);
        BasicDBObject dbObject = converter.buildToFromObject(object);
        collection.insert(dbObject);
        return converter.buildObjectFromTO(dbObject);
    }

    @Override
    public Task update(Task object) {
        Objects.requireNonNull(object);
        String _id = object.getUri().getId();
        BasicDBObject dbObject = converter.buildToFromObject(object);
        collection.update(
                new BasicDBObject("_id", _id),
                new BasicDBObject("$set", dbObject)
        );
        dbObject.put("_id", _id);
        return converter.buildObjectFromTO(dbObject);
    }

    @Override
    public Task getByUri(TaskUri uri) {
        String objectId = uri.getId();
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("_id", objectId);
        DBCursor cursor = collection.find(searchQuery);
        if (cursor.hasNext()) {
            BasicDBObject dbObject = (BasicDBObject) cursor.next();
            return converter.buildObjectFromTO(dbObject);
        }
        return null;
    }

    @Override
    public Collection<Task> getByUris(Collection<TaskUri> objectUris) {
        List<String> ids = objectUris.stream().map(AbstractObjectUri::getId).collect(Collectors.toList());
        BasicDBObject searchQuery = new BasicDBObject("_id", new BasicDBObject("$in", ids));
        DBCursor cursor = collection.find(searchQuery);
        List<Task> result = new ArrayList<>();
        while (cursor.hasNext()) {
            BasicDBObject dbObject = (BasicDBObject) cursor.next();
            result.add(converter.buildObjectFromTO(dbObject));
        }
        return result;
    }

    @Override
    public void deleteObject(TaskUri uri) {
        Objects.requireNonNull(uri);
        BasicDBObject deleteQuery = new BasicDBObject();
        deleteQuery.put("_id", uri.getId());
        collection.remove(deleteQuery);
    }
}
