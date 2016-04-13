package shire.the.great.wiffy.wear.services;

import android.content.Context;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import shire.the.great.wearman.common.Constants;
import shire.the.great.wiffy.wear.receivers.AlwaysActiveConnectivityReceiver;

/**
 * Created by ZachS on 4/3/2016.
 */
public class AlwaysActiveSingleton {
    private static final String LOGTAG = "AlwaysActiveSingleton";

    private static AlwaysActiveSingleton ourInstance;
    private AlwaysActiveConnectivityReceiver mConnectivityReceiver;
    private int mUpdateId;

    public static AlwaysActiveSingleton getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new AlwaysActiveSingleton(context);
        }

        return ourInstance;
    }

    public void destroyInstance(Context context) {
        Log.d(LOGTAG, "destroying singleton");
        unregisterConnectivityReceiver(context);
        ourInstance = null;
        mConnectivityReceiver = null;
    }

    private AlwaysActiveSingleton(Context context) {
        mConnectivityReceiver = new AlwaysActiveConnectivityReceiver();
        registerConnectivityReceiver(context);
        Log.d(LOGTAG, "creating singleton");
        mUpdateId = 0;
    }

    private void registerConnectivityReceiver(Context context) {
        // AlwaysActiveConnectivityReceiver, used for custom actions
        IntentFilter mConnectivityChangedFilter =
                new IntentFilter(Constants.CONNECTIVITY_CHANGED_ACTION);
        IntentFilter mWifiStateChangedFilter =
                new IntentFilter(Constants.WIFI_STATE_CHANGED_ACTION);
        IntentFilter mRssiFilter =
                new IntentFilter(Constants.RSSI_CHANGED_ACTION);
        IntentFilter mNetworkStateFilter =
                new IntentFilter(Constants.NETWORK_STATE_CHANGED_ACTION);
        IntentFilter mSuppStateFilter =
                new IntentFilter(Constants.SUPPLICANT_STATE_CHANGED_ACTION);

        // Register the receiver's and their IntentFilter's
        LocalBroadcastManager.getInstance(context).registerReceiver(mConnectivityReceiver, mConnectivityChangedFilter);
        LocalBroadcastManager.getInstance(context).registerReceiver(mConnectivityReceiver, mWifiStateChangedFilter);
        LocalBroadcastManager.getInstance(context).registerReceiver(mConnectivityReceiver, mRssiFilter);
        LocalBroadcastManager.getInstance(context).registerReceiver(mConnectivityReceiver, mNetworkStateFilter);
        LocalBroadcastManager.getInstance(context).registerReceiver(mConnectivityReceiver, mSuppStateFilter);
    }

    private void unregisterConnectivityReceiver(Context context) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mConnectivityReceiver);
    }

    public int getUpdateId() {
        return mUpdateId;
    }

    public void incrementUpdateId() {
        mUpdateId++;
    }
}
