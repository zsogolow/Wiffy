package shire.the.great.wiffy.wear.services;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

import shire.the.great.wearman.common.WearConstants;
import shire.the.great.wearman.common.WearableApiHelper;

import shire.the.great.wearman.models.parcelables.NetworkStateChange;
import shire.the.great.wearman.models.parcelables.SupplicantStateChange;
import shire.the.great.wearman.models.parcelables.WifiStateChange;
import shire.the.great.conman.wifi.ConnMan;

/**
 * Service running on the phone that gets initiated by the Wearable.
 * <p/>
 * Created by ZachS on 4/2/2016.
 */
public class PhoneWearableListenerService extends WearableListenerService {
    private static final String LOGTAG = "PhoneWearLS";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOGTAG, "onCreate");
        AlwaysActiveSingleton.getInstance(getBaseContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOGTAG, "onDestroy");
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);

        Log.d(LOGTAG, messageEvent.getPath());
        if (messageEvent.getPath().equals(WearConstants.CONNECT)) {
            syncData();
        }

        if (messageEvent.getPath().equals(WearConstants.DISCONNECT)) {
            AlwaysActiveSingleton.getInstance(getBaseContext()).destroyInstance(getBaseContext());
        }

        if (messageEvent.getPath().equals(WearConstants.TOGGLE_WIFI_ON)) {
            toggleWifi(true);

        }
        if (messageEvent.getPath().equals(WearConstants.TOGGLE_WIFI_OFF)) {
            toggleWifi(false);
        }
    }

    private void syncData() {
        // The wearable has requested updated connection data. collect and send data in a
        // separate thread:
        new Thread() {
            public void run() {
                Log.d(LOGTAG, "onMessageReceived: Thread " + this.getName() + " started!");

                // create and connect the googleApiClient:
                GoogleApiClient googleApiClient = WearableApiHelper
                        .createAndConnectGoogleApiClient(PhoneWearableListenerService.this, 1000);
                if (googleApiClient == null) {
                    Log.e(LOGTAG, "onMessageReceived (Thread=" + this.getName() + "): Can't connect the google api client! stop.");
                    return;
                }

                // Enumerate nodes to find wearable node:
                Node wearableNode = WearableApiHelper.getOpponentNode(googleApiClient, 1000);
                if (wearableNode == null) {
                    Log.e(LOGTAG, "onMessageReceived (Thread=" + this.getName() + "): Can't get the wearable node! stop.");
                    googleApiClient.disconnect();
                    return;
                }
                NetworkStateChange connState = new ConnMan(getBaseContext()).getCurrentNetworkState();
                WifiStateChange wifiState = new ConnMan(getBaseContext()).getWifiState();
                SupplicantStateChange supplicantStateChange = new ConnMan(getBaseContext()).getSupplicantState();
                WearableApiHelper.updateConnectionData(googleApiClient, connState);
                WearableApiHelper.updateConnectionData(googleApiClient, wifiState);
                WearableApiHelper.updateConnectionData(googleApiClient, supplicantStateChange);

                // disconnect the api client:
                googleApiClient.disconnect();

                Log.d(LOGTAG, "onMessageReceived: Thread " + this.getName() + " stopped!");
            }
        }.start();
    }

    private void toggleWifi(final boolean toggle) {
        // The wearable has requested to toggle the wifi. collect and send data in a
        // separate thread:
        new Thread() {
            public void run() {
                Log.d(LOGTAG, "onMessageReceived: Thread " + this.getName() + " started!");

                // create and connect the googleApiClient:
                GoogleApiClient googleApiClient = WearableApiHelper
                        .createAndConnectGoogleApiClient(PhoneWearableListenerService.this, 1000);
                if (googleApiClient == null) {
                    Log.e(LOGTAG, "onMessageReceived (Thread=" + this.getName() + "): Can't connect the google api client! stop.");
                    return;
                }

                // Enumerate nodes to find wearable node:
                Node wearableNode = WearableApiHelper.getOpponentNode(googleApiClient, 1000);
                if (wearableNode == null) {
                    Log.e(LOGTAG, "onMessageReceived (Thread=" + this.getName() + "): Can't get the wearable node! stop.");
                    googleApiClient.disconnect();
                    return;
                }

                if (toggle) {
                    // turns off the wifi switch
                    new ConnMan(getBaseContext()).turnOnWifi();
                } else {
                    // turns off the wifi switch
                    new ConnMan(getBaseContext()).turnOffWifi();
                }
                // disconnect the api client:
                googleApiClient.disconnect();

                Log.d(LOGTAG, "onMessageReceived: Thread " + this.getName() + " stopped!");
            }
        }.start();
    }

    /**
     * Gets called after a wearable node connects to the phone. Will synchronize the
     * shared preferences to the new node.
     *
     * @param peer node ID of the connected wearable
     */
    @Override
    public void onPeerConnected(Node peer) {
        Log.d(LOGTAG, "onPeerConnected: update preferences...");

        // create and connect the googleApiClient:
        GoogleApiClient googleApiClient = WearableApiHelper
                .createAndConnectGoogleApiClient(PhoneWearableListenerService.this, 1000);
        if (googleApiClient == null) {
            Log.e(LOGTAG, "onPeerConnected: Can't connect the google api client! stop.");
            return;
        }
        // disconnect the api client:
        googleApiClient.disconnect();

        Intent connectedIntent = new Intent();
        connectedIntent.setAction(WearConstants.WEARABLE_CONNECTED);
        connectedIntent.putExtra(WearConstants.NODE_NAME, peer.getDisplayName());
        LocalBroadcastManager.getInstance(this).sendBroadcast(connectedIntent);
    }

    @Override
    public void onPeerDisconnected(Node peer) {
        Log.d(LOGTAG, "onPeerDisconnected: update preferences...");

        Intent connectedIntent = new Intent();
        connectedIntent.setAction(WearConstants.WEARABLE_DISCONNECTED);
        connectedIntent.putExtra(WearConstants.NODE_NAME, peer.getDisplayName());
        LocalBroadcastManager.getInstance(this).sendBroadcast(connectedIntent);
    }
}
