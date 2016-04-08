package shire.the.great.conman.models.parcelables;

import android.os.Parcel;
import android.os.Parcelable;

import shire.the.great.conman.models.dataapi.AbstractDataItem;
import shire.the.great.conman.models.dataapi.DataItemType;

/**
 * Created by ZachS on 4/7/2016.
 */
public class WifiStateChange extends AbstractDataItem implements Parcelable {
    private int mPreviousState;
    private int mCurrentState;

    public WifiStateChange(int previous, int current) {
        super(DataItemType.WifiStateChange);

        this.mPreviousState = previous;
        this.mCurrentState = current;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.mCurrentState);
        out.writeInt(this.mPreviousState);
    }

    public static final Parcelable.Creator<WifiStateChange> CREATOR
            = new Parcelable.Creator<WifiStateChange>() {
        public WifiStateChange createFromParcel(Parcel in) {
            return new WifiStateChange(in);
        }

        public WifiStateChange[] newArray(int size) {
            return new WifiStateChange[size];
        }
    };

    private WifiStateChange(Parcel in) {
        super(DataItemType.WifiStateChange);
        this.mCurrentState = in.readInt();
        this.mPreviousState = in.readInt();
    }

    public int getPreviousState() {
        return mPreviousState;
    }

    public int getCurrentState() {
        return mCurrentState;
    }
}
