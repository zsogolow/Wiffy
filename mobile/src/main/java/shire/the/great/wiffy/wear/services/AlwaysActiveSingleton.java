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

    private static AlwaysActiveSingleton ourInstance = new AlwaysActiveSingleton();
    private static Context mContext;
    private static AlwaysActiveConnectivityReceiver mConnectivityReceiver;

    public int mUpdateId;

    public static AlwaysActiveSingleton getInstance(Context context) {
        mContext = context;

        if (ourInstance == null) {
            ourInstance = new AlwaysActiveSingleton();
        }

        return ourInstance;
    }

    public static void destroyInstance() {
        unregisterConnectivityReceiver();
        ourInstance = null;
    }

    private AlwaysActiveSingleton() {
        mConnectivityReceiver = new AlwaysActiveConnectivityReceiver();
        registerConnectivityReceiver();
        Log.d(LOGTAG, "creating singleton");
        mUpdateId = 0;
    }

    private static void registerConnectivityReceiver() {
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
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mConnectivityReceiver, mConnectivityChangedFilter);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mConnectivityReceiver, mWifiStateChangedFilter);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mConnectivityReceiver, mRssiFilter);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mConnectivityReceiver, mNetworkStateFilter);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mConnectivityReceiver, mSuppStateFilter);
    }

    private static void unregisterConnectivityReceiver() {
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mConnectivityReceiver);
    }
}
