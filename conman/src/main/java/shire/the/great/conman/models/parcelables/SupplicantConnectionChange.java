package shire.the.great.conman.models.parcelables;

import android.os.Parcel;
import android.os.Parcelable;

import shire.the.great.conman.models.dataapi.AbstractDataItem;
import shire.the.great.conman.models.dataapi.DataItemType;

/**
 * Created by ZachS on 4/5/2016.
 */
public class SupplicantConnectionChange extends AbstractDataItem implements Parcelable {
    private boolean mIsSupplicantConnected;

    public SupplicantConnectionChange(boolean current) {
        super(DataItemType.SupplicantConnectionChange);
        this.mIsSupplicantConnected = current;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeValue(this.mIsSupplicantConnected);
    }

    public static final Parcelable.Creator<SupplicantConnectionChange> CREATOR
            = new Parcelable.Creator<SupplicantConnectionChange>() {
        public SupplicantConnectionChange createFromParcel(Parcel in) {
            return new SupplicantConnectionChange(in);
        }

        public SupplicantConnectionChange[] newArray(int size) {
            return new SupplicantConnectionChange[size];
        }
    };

    private SupplicantConnectionChange(Parcel in) {
        super(DataItemType.SupplicantConnectionChange);
        this.mIsSupplicantConnected = (boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public boolean isSupplicantConnected() {
        return mIsSupplicantConnected;
    }
}
