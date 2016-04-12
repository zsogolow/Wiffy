package shire.the.great.wearman.models.parcelables;

import android.net.wifi.SupplicantState;
import android.os.Parcel;
import android.os.Parcelable;

import shire.the.great.wearman.models.dataapi.AbstractDataItem;
import shire.the.great.wearman.models.dataapi.DataItemType;

/**
 * Created by ZachS on 4/7/2016.
 */
public class SupplicantStateChange extends AbstractDataItem implements Parcelable {
    private SupplicantState mSupplicantState;
    private int mSupplicantError;

    public SupplicantStateChange(SupplicantState supplicantState, int suppError) {
        super(DataItemType.SupplicantStateChange);

        this.mSupplicantState = supplicantState;
        this.mSupplicantError = suppError;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeValue(this.mSupplicantState);
        out.writeInt(this.mSupplicantError);
    }

    public static final Parcelable.Creator<SupplicantStateChange> CREATOR
            = new Parcelable.Creator<SupplicantStateChange>() {
        public SupplicantStateChange createFromParcel(Parcel in) {
            return new SupplicantStateChange(in);
        }

        public SupplicantStateChange[] newArray(int size) {
            return new SupplicantStateChange[size];
        }
    };

    private SupplicantStateChange(Parcel in) {
        super(DataItemType.SupplicantStateChange);
        this.mSupplicantState = (SupplicantState) in.readValue(SupplicantState.class.getClassLoader());
        this.mSupplicantError = in.readInt();
    }

    public SupplicantState getSupplicantState() {
        return mSupplicantState;
    }

    public int getSupplicantError() {
        return mSupplicantError;
    }
}
