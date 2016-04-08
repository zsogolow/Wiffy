package shire.the.great.conman.models.parcelables;

import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.os.Parcel;
import android.os.Parcelable;

import shire.the.great.conman.models.dataapi.AbstractDataItem;
import shire.the.great.conman.models.dataapi.DataItemType;

/**
 * Created by ZachS on 4/7/2016.
 */
public class NetworkStateChange extends AbstractDataItem implements Parcelable {
    private NetworkInfo mNetworkInfo;
    private WifiInfo mWifiInfo;
    private String mBssidExtra;

    public NetworkStateChange(NetworkInfo networkInfo, WifiInfo wifiInfo, String bssidExtra) {
        super(DataItemType.NetworkStateChange);
        this.mNetworkInfo = networkInfo;
        this.mWifiInfo = wifiInfo;
        this.mBssidExtra = bssidExtra;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(this.mNetworkInfo, flags);
        out.writeParcelable(this.mWifiInfo, flags);
        out.writeString(this.mBssidExtra);
    }

    public static final Parcelable.Creator<NetworkStateChange> CREATOR
            = new Parcelable.Creator<NetworkStateChange>() {
        public NetworkStateChange createFromParcel(Parcel in) {
            return new NetworkStateChange(in);
        }

        public NetworkStateChange[] newArray(int size) {
            return new NetworkStateChange[size];
        }
    };

    private NetworkStateChange(Parcel in) {
        super(DataItemType.NetworkStateChange);
        this.mNetworkInfo = in.readParcelable(NetworkInfo.class.getClassLoader());
        this.mWifiInfo = in.readParcelable(WifiInfo.class.getClassLoader());
        this.mBssidExtra = in.readString();
    }

    public NetworkInfo getNetworkInfo() {
        return mNetworkInfo;
    }

    public WifiInfo getWifiInfo() {
        return mWifiInfo;
    }

    public String getSsidExtra() {
        return mBssidExtra;
    }
}
