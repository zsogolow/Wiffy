package shire.the.great.conman.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import shire.the.great.wearman.common.Constants;
import shire.the.great.wearman.models.parcelables.NetworkStateChange;
import shire.the.great.wearman.models.parcelables.RssiChange;
import shire.the.great.wearman.models.parcelables.ScanResultsChange;
import shire.the.great.wearman.models.parcelables.SupplicantConnectionChange;
import shire.the.great.wearman.models.parcelables.SupplicantStateChange;
import shire.the.great.wearman.models.parcelables.WifiStateChange;

/**
 * Created by ZachS on 4/8/2016.
 */
public class SystemWifiReceiver extends BroadcastReceiver {
    private static final String LOGTAG = "SystemWifiReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOGTAG, intent.getAction());

        // results
        Intent changedResultIntent = new Intent();

        switch (intent.getAction()) {
            case WifiManager.WIFI_STATE_CHANGED_ACTION:
                int previousState = intent.getIntExtra(WifiManager.EXTRA_PREVIOUS_WIFI_STATE, -1);
                int newState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
                WifiStateChange wifiStateChange = new WifiStateChange(previousState, newState);
                changedResultIntent.setAction(Constants.WIFI_STATE_CHANGED_ACTION);
                changedResultIntent.putExtra(Constants.RESULT_WIFI_STATE, wifiStateChange);
                break;

            case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                NetworkInfo activeNetwork = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                WifiInfo activeWifi = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
                String bssid = intent.getStringExtra(WifiManager.EXTRA_BSSID);
                NetworkStateChange networkStateChange = new NetworkStateChange(activeNetwork, activeWifi, activeNetwork.getExtraInfo());
                changedResultIntent.setAction(Constants.NETWORK_STATE_CHANGED_ACTION);
                changedResultIntent.putExtra(Constants.RESULT_NETWORK_STATE_CHANGED, networkStateChange);
                break;

            case WifiManager.RSSI_CHANGED_ACTION:
                int rssi = intent.getIntExtra(WifiManager.EXTRA_NEW_RSSI, 100);
                RssiChange rssiChange = new RssiChange(rssi);
                changedResultIntent.setAction(Constants.RSSI_CHANGED_ACTION);
                changedResultIntent.putExtra(Constants.RESULT_RSSI_CHANGED, rssiChange);
                break;

            case WifiManager.SUPPLICANT_STATE_CHANGED_ACTION:
                SupplicantState supplicantState = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
                int suppError = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 0);
                SupplicantStateChange stateChange = new SupplicantStateChange(supplicantState, suppError);
                changedResultIntent.setAction(Constants.SUPPLICANT_STATE_CHANGED_ACTION);
                changedResultIntent.putExtra(Constants.RESULT_SUPPLICANT_STATE_CHANGED, stateChange);
                break;

// not used //
            case WifiManager.SCAN_RESULTS_AVAILABLE_ACTION:
                boolean resultsUpdated = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
                ScanResultsChange scanResultsAvailable = new ScanResultsChange(resultsUpdated);
                changedResultIntent.setAction(Constants.WIFI_SCAN_RESULTS_AVAILABLE_ACTION);
                changedResultIntent.putExtra(Constants.RESULT_WIFI_SCAN_RESULTS, scanResultsAvailable);
                break;

            case WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION:
                boolean suppConnected = intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false);
                SupplicantConnectionChange supplicantConnectionChange = new SupplicantConnectionChange(suppConnected);
                changedResultIntent.setAction(Constants.SUPPLICANT_CONNECTION_CHANGED_ACTION);
                changedResultIntent.putExtra(Constants.RESULT_SUPPLICANT_CONNECTION_CHANGED, supplicantConnectionChange);
                break;

            case WifiManager.NETWORK_IDS_CHANGED_ACTION:
                break;

            default:
                // this should never ever ever ever happen. :)
                return;
        }

        LocalBroadcastManager.getInstance(context).sendBroadcast(changedResultIntent);
    }
}
