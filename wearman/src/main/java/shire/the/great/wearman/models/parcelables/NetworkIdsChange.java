package shire.the.great.wearman.models.parcelables;

import android.os.Parcel;
import android.os.Parcelable;

import shire.the.great.wearman.models.dataapi.AbstractDataItem;
import shire.the.great.wearman.models.dataapi.DataItemType;

/**
 * Created by ZachS on 4/5/2016.
 */
public class NetworkIdsChange extends AbstractDataItem implements Parcelable {
//    private int mCurrentState;

    public NetworkIdsChange(int previous, int current) {
        super(DataItemType.NetworkIdsChange);
//        this.mCurrentState = current;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
//        out.writeInt(this.mCurrentState);
    }

    public static final Parcelable.Creator<NetworkIdsChange> CREATOR
            = new Parcelable.Creator<NetworkIdsChange>() {
        public NetworkIdsChange createFromParcel(Parcel in) {
            return new NetworkIdsChange(in);
        }

        public NetworkIdsChange[] newArray(int size) {
            return new NetworkIdsChange[size];
        }
    };

    private NetworkIdsChange(Parcel in) {
        super(DataItemType.NetworkIdsChange);
//        this.mCurrentState = in.readInt();
    }
}
