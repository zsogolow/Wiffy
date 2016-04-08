package shire.the.great.wiffy.wear.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;

import shire.the.great.conman.common.Constants;
import shire.the.great.conman.common.WearableApiHelper;
import shire.the.great.conman.models.parcelables.NetworkStateChange;
import shire.the.great.conman.models.parcelables.RssiChange;
import shire.the.great.conman.models.dataapi.AbstractDataItem;
import shire.the.great.conman.models.parcelables.ConnectionChange;
import shire.the.great.conman.models.parcelables.SupplicantStateChange;
import shire.the.great.conman.models.parcelables.WifiStateChange;

/**
 * Created by ZachS on 4/2/2016.
 */
public class AlwaysActiveConnectivityReceiver extends BroadcastReceiver {
    private static final String LOGTAG = "AlwaysActiveConnectivityReceiver";

    @Override
    public void onReceive(final Context context, Intent intent) {
        switch (intent.getAction()) {
            case Constants.WIFI_STATE_CHANGED_ACTION:
                WifiStateChange wifiResult =
                        intent.getParcelableExtra(Constants.RESULT_WIFI_STATE);
                postConnectionChangeAsync(context, wifiResult);
                break;

            case Constants.NETWORK_STATE_CHANGED_ACTION:
                NetworkStateChange networkStateChange =
                        intent.getParcelableExtra(Constants.RESULT_NETWORK_STATE_CHANGED);
                    postConnectionChangeAsync(context, networkStateChange);
                break;

            case Constants.RSSI_CHANGED_ACTION:
                RssiChange rssiChange =
                        intent.getParcelableExtra(Constants.RESULT_RSSI_CHANGED);
                break;

            case Constants.SUPPLICANT_STATE_CHANGED_ACTION:
                SupplicantStateChange supplicantStateChange =
                        intent.getParcelableExtra(Constants.RESULT_SUPPLICANT_STATE_CHANGED);
                postConnectionChangeAsync(context, supplicantStateChange);
                break;

            case Constants.CONNECTIVITY_CHANGED_ACTION:
                ConnectionChange connectionChange =
                        intent.getParcelableExtra(Constants.RESULT_CONNECTION_CHANGED);
                postConnectionChangeAsync(context, connectionChange);
                break;
            default:
                break;
        }
    }

    private void postConnectionChangeAsync(final Context context, final AbstractDataItem dataItem) {
        new Thread() {
            public void run() {
                Log.d("fuckyou", "onMessageReceived: Thread " + this.getName() + " started!");

                // create and connect the googleApiClient:
                GoogleApiClient googleApiClient = WearableApiHelper
                        .createAndConnectGoogleApiClient(context, 1000);
                if (googleApiClient == null) {
                    Log.e("fuckyou", "onMessageReceived (Thread=" + this.getName() + "): Can't connect the google api client! stop.");
                    return;
                }

                // Enumerate nodes to find wearable node:
                Node wearableNode = WearableApiHelper.getOpponentNode(googleApiClient, 1000);
                if (wearableNode == null) {
                    Log.e("fuckyou", "onMessageReceived (Thread=" + this.getName() + "): Can't get the wearable node! stop.");
                    googleApiClient.disconnect();
                    return;
                }

                WearableApiHelper.updateConnectionData(googleApiClient, dataItem);

                // disconnect the api client:
                googleApiClient.disconnect();

                Log.d("fuckyou", "onMessageReceived: Thread " + this.getName() + " stopped!");
            }
        }.start();
    }
}