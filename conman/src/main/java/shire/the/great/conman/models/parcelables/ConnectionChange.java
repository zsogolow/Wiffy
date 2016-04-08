package shire.the.great.conman.models.parcelables;

import android.net.NetworkInfo;
import android.os.Parcel;
import android.os.Parcelable;

import shire.the.great.conman.models.dataapi.AbstractDataItem;
import shire.the.great.conman.models.dataapi.DataItemType;

/**
 * Created by ZachS on 4/6/2016.
 */
public class ConnectionChange extends AbstractDataItem implements Parcelable {
    private NetworkInfo.State mNetworkState;
    private NetworkInfo.DetailedState mNetworkDetailedState;
    private int mNetworkType;
    private String mNetworkName;
    private int mNetworkSubType;
    private String mNetworkSubName;
    private boolean mIsAvailable;
    private boolean mIsConnected;
    private String mExtraInfo;

    public ConnectionChange(NetworkInfo.State state, NetworkInfo.DetailedState detailedState,
                            int networkType, String networkName, int networkSubType, String networkSubName,
                            boolean isAvailable, boolean isConnected, String activeConnName) {
        super(DataItemType.ConnectionChange);
        this.mNetworkState = state;
        this.mNetworkDetailedState = detailedState;
        this.mNetworkType = networkType;
        this.mNetworkName = networkName;
        this.mNetworkSubType = networkSubType;
        this.mNetworkSubName = networkSubName;
        this.mIsAvailable = isAvailable;
        this.mIsConnected = isConnected;
        this.mExtraInfo = activeConnName;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeValue(this.mNetworkState);
        out.writeValue(this.mNetworkDetailedState);
        out.writeInt(this.mNetworkType);
        out.writeString(this.mNetworkName);
        out.writeInt(this.mNetworkSubType);
        out.writeString(this.mNetworkSubName);
        out.writeValue(this.mIsAvailable);
        out.writeValue(this.mIsConnected);
        out.writeString(this.mExtraInfo);
    }

    public static final Parcelable.Creator<ConnectionChange> CREATOR
            = new Parcelable.Creator<ConnectionChange>() {
        public ConnectionChange createFromParcel(Parcel in) {
            return new ConnectionChange(in);
        }

        public ConnectionChange[] newArray(int size) {
            return new ConnectionChange[size];
        }
    };

    private ConnectionChange(Parcel in) {
        super(DataItemType.ConnectionChange);
        this.mNetworkState = (NetworkInfo.State) in.readValue(NetworkInfo.State.class.getClassLoader());
        this.mNetworkDetailedState = (NetworkInfo.DetailedState) in.readValue(NetworkInfo.DetailedState.class.getClassLoader());
        this.mNetworkType = in.readInt();
        this.mNetworkName = in.readString();
        this.mNetworkSubType = in.readInt();
        this.mNetworkSubName = in.readString();
        this.mIsAvailable = (boolean) in.readValue(Boolean.class.getClassLoader());
        this.mIsConnected = (boolean) in.readValue(Boolean.class.getClassLoader());
        this.mExtraInfo = in.readString();
    }

    public NetworkInfo.State getNetworkState() {
        return mNetworkState;
    }

    public NetworkInfo.DetailedState getNetworkDetailedState() {
        return mNetworkDetailedState;
    }

    public int getNetworkType() {
        return mNetworkType;
    }

    public String getNetworkName() {
        return mNetworkName;
    }

    public int getNetworkSubType() {
        return mNetworkSubType;
    }

    public String getNetworkSubName() {
        return mNetworkSubName;
    }

    public boolean isAvailable() {
        return mIsAvailable;
    }

    public boolean isConnected() {
        return mIsConnected;
    }

    public String getExtraInfo() {
        return mExtraInfo;
    }
}
