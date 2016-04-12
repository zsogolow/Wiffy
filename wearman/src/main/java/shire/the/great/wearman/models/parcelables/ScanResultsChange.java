package shire.the.great.wearman.models.parcelables;

import android.os.Parcel;
import android.os.Parcelable;

import shire.the.great.wearman.models.dataapi.AbstractDataItem;
import shire.the.great.wearman.models.dataapi.DataItemType;

/**
 * Created by ZachS on 4/2/2016.
 */
public class ScanResultsChange extends AbstractDataItem implements Parcelable {
    private boolean mResultsAvailable;

    public ScanResultsChange(boolean resultsAvailable) {
        super(DataItemType.ScanResultsChange);
        this.mResultsAvailable = resultsAvailable;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeValue(this.mResultsAvailable);
    }

    public static final Parcelable.Creator<ScanResultsChange> CREATOR
            = new Parcelable.Creator<ScanResultsChange>() {
        public ScanResultsChange createFromParcel(Parcel in) {
            return new ScanResultsChange(in);
        }

        public ScanResultsChange[] newArray(int size) {
            return new ScanResultsChange[size];
        }
    };

    private ScanResultsChange(Parcel in) {
        super(DataItemType.ScanResultsChange);
        this.mResultsAvailable = (boolean) in.readValue(Boolean.class.getClassLoader());
    }
    public boolean isResultsAvailable() {
        return mResultsAvailable;
    }
}
