package shire.the.great.wiffy.services;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Channel;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;

import shire.the.great.wearman.common.WearConstants;
import shire.the.great.wearman.common.WearableApiHelper;

/**
 * Created by ZachS on 4/4/2016.
 */
public class WatchWearableListenerService extends WearableListenerService {
    private static final String LOGTAG = "WatchWearLS";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOGTAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOGTAG, "onDestroy");
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        Log.d(LOGTAG, "onMessageReceived");
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(LOGTAG, "onDataChanged");
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_DELETED) {
            } else if (event.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo(WearConstants.NETWORK_CONNECTION_DATA) == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    String netName = dataMap.getString(WearConstants.NETWORK_NAME);
                    String netSubName = dataMap.getString(WearConstants.NETWORK_SUB_NAME);
                    String netStatus = dataMap.getString(WearConstants.NETWORK_STATE);
                    String extraInfo = dataMap.getString(WearConstants.NETWORK_EXTRA_INFO);
                    int updateId = dataMap.getInt(WearConstants.UPDATE_ID);
                    Intent intent = new Intent();
                    intent.setAction(WearConstants.NETWORK_CONNECTION_DATA);
                    intent.putExtra(WearConstants.NETWORK_NAME, netName);
                    intent.putExtra(WearConstants.NETWORK_SUB_NAME, netSubName);
                    intent.putExtra(WearConstants.NETWORK_STATE, netStatus);
                    intent.putExtra(WearConstants.NETWORK_EXTRA_INFO, extraInfo);
                    intent.putExtra(WearConstants.UPDATE_ID, updateId);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                } else if (item.getUri().getPath().compareTo(WearConstants.WIFI_STATE_DATA) == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    int wifiState = dataMap.getInt(WearConstants.WIFI_STATE);
                    int updateId = dataMap.getInt(WearConstants.UPDATE_ID);
                    boolean state = wifiState == (WifiManager.WIFI_STATE_ENABLED | WifiManager.WIFI_STATE_ENABLING);
                    Intent intent = new Intent();
                    intent.setAction(WearConstants.WIFI_STATE_DATA);
                    intent.putExtra(WearConstants.WIFI_STATE, state);
                    intent.putExtra(WearConstants.UPDATE_ID, updateId);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                } else if (item.getUri().getPath().compareTo(WearConstants.SUPPLICANT_STATE_DATA) == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    String suppState = dataMap.getString(WearConstants.SUPPLICANT_STATE);
                    int updateId = dataMap.getInt(WearConstants.UPDATE_ID);
                    Intent intent = new Intent();
                    intent.setAction(WearConstants.SUPPLICANT_STATE_DATA);
                    intent.putExtra(WearConstants.SUPPLICANT_STATE, suppState);
                    intent.putExtra(WearConstants.UPDATE_ID, updateId);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                }
            }
        }
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
                .createAndConnectGoogleApiClient(WatchWearableListenerService.this, 1000);
        if (googleApiClient == null) {
            Log.e(LOGTAG, "onPeerConnected: Can't connect the google api client! stop.");
            return;
        }

        // HERE
        Intent connectedIntent = new Intent();
        connectedIntent.setAction(WearConstants.WEARABLE_CONNECTED);
        connectedIntent.putExtra(WearConstants.NODE_NAME, peer.getDisplayName());
        LocalBroadcastManager.getInstance(this).sendBroadcast(connectedIntent);

        // disconnect the api client:
        googleApiClient.disconnect();
    }

    @Override
    public void onPeerDisconnected(Node peer) {
        Log.d(LOGTAG, "onPeerDiconnected: update preferences...");

        Intent connectedIntent = new Intent();
        connectedIntent.setAction(WearConstants.WEARABLE_DISCONNECTED);
        connectedIntent.putExtra(WearConstants.NODE_NAME, peer.getDisplayName());
        LocalBroadcastManager.getInstance(this).sendBroadcast(connectedIntent);
    }

    @Override
    public void onConnectedNodes(List<Node> connectedNodes) {
        super.onConnectedNodes(connectedNodes);
        Log.d(LOGTAG, "onConnectedNodes");
        boolean isConnected = false;
        for (Node node : connectedNodes) {
            if (node.isNearby()) {
                isConnected = true;
            }
        }

        Intent connectedIntent = new Intent();
        if (isConnected) {
            connectedIntent.setAction(WearConstants.WEARABLE_CONNECTED);
        } else {
            connectedIntent.setAction(WearConstants.WEARABLE_DISCONNECTED);
        }

        LocalBroadcastManager.getInstance(this).sendBroadcast(connectedIntent);
    }

    @Override
    public void onCapabilityChanged(CapabilityInfo capabilityInfo) {
        super.onCapabilityChanged(capabilityInfo);
        Log.d(LOGTAG, "onCapabilityChanged");
    }

    @Override
    public void onChannelOpened(Channel channel) {
        super.onChannelOpened(channel);
        Log.d(LOGTAG, "onChannelOpened");
    }

    @Override
    public void onChannelClosed(Channel channel, int closeReason, int appSpecificErrorCode) {
        super.onChannelClosed(channel, closeReason, appSpecificErrorCode);
        Log.d(LOGTAG, "onChannelClosed");
    }

    @Override
    public void onInputClosed(Channel channel, int closeReason, int appSpecificErrorCode) {
        super.onInputClosed(channel, closeReason, appSpecificErrorCode);
        Log.d(LOGTAG, "onInputClosed");
    }

    @Override
    public void onOutputClosed(Channel channel, int closeReason, int appSpecificErrorCode) {
        super.onOutputClosed(channel, closeReason, appSpecificErrorCode);
        Log.d(LOGTAG, "onOutputClosed");
    }
}
