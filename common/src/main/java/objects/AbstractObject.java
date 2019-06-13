package objects;

public abstract class AbstractObject {

    protected final AbstractObjectUri objectUri;
    protected Long createTimestamp = new Long(0L);
    protected Long updateTimestamp = new Long(0L);

    public AbstractObject(AbstractObjectUri objectUri) {
        this.objectUri = objectUri;
    }

    public AbstractObjectUri getUri() {
        return objectUri;
    }

    public void setCreateTimestamp(long createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public Long getCreateTimestamp() {
        return createTimestamp;
    }

    public void setUpdateTimestamp(long updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }

    public Long getUpdateTimestamp() {
        return updateTimestamp;
    }

}
