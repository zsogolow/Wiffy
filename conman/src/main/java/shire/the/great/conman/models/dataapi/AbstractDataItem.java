package shire.the.great.conman.models.dataapi;

/**
 * Created by ZachS on 4/3/2016.
 */
public abstract class AbstractDataItem implements IDataItem {
    protected DataItemType mDataItemType;

    public AbstractDataItem(DataItemType type) {
        mDataItemType = type;
    }

    public DataItemType getDataItemType() {
        return mDataItemType;
    }
}
