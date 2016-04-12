package shire.the.great.wearman.models.parcelables;

import android.os.Parcel;
import android.os.Parcelable;

import shire.the.great.wearman.models.dataapi.AbstractDataItem;
import shire.the.great.wearman.models.dataapi.DataItemType;

/**
 * Created by ZachS on 4/5/2016.
 */
public class RssiChange extends AbstractDataItem implements Parcelable {
    private int mRssiLevel;

    public RssiChange(int rssiLevel) {
        super(DataItemType.RssiChange);
        this.mRssiLevel = rssiLevel;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.mRssiLevel);
    }

    public static final Parcelable.Creator<RssiChange> CREATOR
            = new Parcelable.Creator<RssiChange>() {
        public RssiChange createFromParcel(Parcel in) {
            return new RssiChange(in);
        }

        public RssiChange[] newArray(int size) {
            return new RssiChange[size];
        }
    };

    private RssiChange(Parcel in) {
        super(DataItemType.RssiChange);
        this.mRssiLevel = in.readInt();
    }

    public int getRssiLevel() {
        return mRssiLevel;
    }
}
