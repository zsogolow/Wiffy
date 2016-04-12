package shire.the.great.wearman.models.dataapi;

/**
 * Created by ZachS on 4/3/2016.
 */
public abstract class AbstractDataItem implements IDataItem {
    protected DataItemType mDataItemType;
    protected int mUpdateId;

    public AbstractDataItem(DataItemType type) {
        mDataItemType = type;
    }

    public DataItemType getDataItemType() {
        return mDataItemType;
    }

    public void setUpdateId(int id) {
        this.mUpdateId = id;
    }

    public int getUpdateId() {
        return mUpdateId;
    }
}
