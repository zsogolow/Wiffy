package shire.the.great.conman.common;

import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.concurrent.TimeUnit;

import shire.the.great.conman.models.dataapi.AbstractDataItem;
import shire.the.great.conman.models.parcelables.ConnectionChange;
import shire.the.great.conman.models.parcelables.NetworkStateChange;
import shire.the.great.conman.models.parcelables.SupplicantStateChange;
import shire.the.great.conman.models.parcelables.WifiStateChange;

/**
 * WearableApi helper class
 * Created by ZachS on 4/2/2016.
 */
public class WearableApiHelper {

    /**
     * Will create a Google Api Client and connect it. This method will block!
     *
     * @param context application context
     * @param timeout max time this method will block before giving up
     * @return the connected api client or null on error
     */
    public static GoogleApiClient createAndConnectGoogleApiClient(Context context, int timeout) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context).addApi(Wearable.API).build();
        googleApiClient.blockingConnect(timeout, TimeUnit.MILLISECONDS);
        if (googleApiClient.isConnected())
            return googleApiClient;
        else
            return null;
    }

    /**
     * Will return the node instance of the opponent device (wearable or phone). This method will block!
     *
     * @param googleApiClient connected GoogleApiClient instance
     * @param timeout         max time this method will block before giving up
     * @return node instance or null on error
     */
    public static Node getOpponentNode(GoogleApiClient googleApiClient, int timeout) {
        NodeApi.GetConnectedNodesResult getConnectedNodesResult = Wearable.NodeApi
                .getConnectedNodes(googleApiClient).await(timeout, TimeUnit.MILLISECONDS);
        if (getConnectedNodesResult == null || !getConnectedNodesResult.getStatus().isSuccess())
            return null;

        if (!getConnectedNodesResult.getNodes().isEmpty())
            return getConnectedNodesResult.getNodes().get(0);
        else
            return null;
    }

    /**
     * Will send a message to the given recipient. This method will block!
     *
     * @param googleApiClient connected GoogleApiClient instance
     * @param nodeId          node ID of the recipient
     * @param path            message path
     * @param payload         message payload
     * @param timeout         max time this method will block before giving up
     * @return true on success and false on error.
     */
    public static boolean sendMessage(GoogleApiClient googleApiClient, String nodeId, String path, byte[] payload, int timeout) {
        MessageApi.SendMessageResult sendMessageResult = Wearable.MessageApi.sendMessage(googleApiClient,
                nodeId, path, payload).await(timeout, TimeUnit.MILLISECONDS);
        return sendMessageResult.getStatus().isSuccess();
    }

    /**
     * Will gather the connection data and put them into a DataItem to be synced to the wearable
     *
     * @param googleApiClient connected google API client
     */
    public static void updateConnectionData(GoogleApiClient googleApiClient,
                                            AbstractDataItem dataItem) {
        switch (dataItem.getDataItemType()) {
            case ConnectionChange:
                ConnectionChange connectionChange = (ConnectionChange) dataItem;
                PutDataMapRequest putDataMapReqConn = PutDataMapRequest.create(WearConstants.NETWORK_CONNECTION_DATA);
                putDataMapReqConn.getDataMap().putString(WearConstants.NETWORK_NAME, connectionChange.getNetworkName());
                putDataMapReqConn.getDataMap().putString(WearConstants.NETWORK_SUB_NAME, connectionChange.getNetworkSubName());
                putDataMapReqConn.getDataMap().putString(WearConstants.NETWORK_STATE, connectionChange.getNetworkState().toString());
                putDataMapReqConn.getDataMap().putString(WearConstants.NETWORK_EXTRA_INFO, connectionChange.getExtraInfo());
                PutDataRequest putDataReqConn = putDataMapReqConn.asPutDataRequest();
                putDataReqConn.setUrgent();
                PendingResult<DataApi.DataItemResult> pendingResult =
                        Wearable.DataApi.putDataItem(googleApiClient, putDataReqConn);
                Wearable.DataApi.deleteDataItems(googleApiClient, putDataMapReqConn.getUri());
                break;
            case WifiStateChange:
                WifiStateChange wifiStateChange = (WifiStateChange) dataItem;
                PutDataMapRequest putDataMapReqWifi = PutDataMapRequest.create(WearConstants.WIFI_STATE_DATA);
                putDataMapReqWifi.getDataMap().putInt(WearConstants.WIFI_STATE, wifiStateChange.getCurrentState());
                PutDataRequest putDataReqWifi = putDataMapReqWifi.asPutDataRequest();
                putDataMapReqWifi.setUrgent();
                PendingResult<DataApi.DataItemResult> pendingResultWifi =
                        Wearable.DataApi.putDataItem(googleApiClient, putDataReqWifi);
                Wearable.DataApi.deleteDataItems(googleApiClient, putDataMapReqWifi.getUri());
                break;

            case SupplicantStateChange:
                SupplicantStateChange supplicantStateChange = (SupplicantStateChange) dataItem;
                PutDataMapRequest putDataMapReqSupp = PutDataMapRequest.create(WearConstants.SUPPLICANT_STATE_DATA);
                putDataMapReqSupp.getDataMap().putString(WearConstants.SUPPLICANT_STATE, supplicantStateChange.getSupplicantState().toString());
                PutDataRequest putDataReqSupp = putDataMapReqSupp.asPutDataRequest();
                putDataReqSupp.setUrgent();
                PendingResult<DataApi.DataItemResult> pendingResultSupp =
                        Wearable.DataApi.putDataItem(googleApiClient, putDataReqSupp);
                Wearable.DataApi.deleteDataItems(googleApiClient, putDataMapReqSupp.getUri());
                break;
            case NetworkStateChange:
                NetworkStateChange networkStateChange = (NetworkStateChange) dataItem;
                PutDataMapRequest putDataMapReqNetChange = PutDataMapRequest.create(WearConstants.NETWORK_CONNECTION_DATA);
                putDataMapReqNetChange.getDataMap().putString(WearConstants.NETWORK_NAME, networkStateChange.getNetworkInfo().getTypeName());
                putDataMapReqNetChange.getDataMap().putString(WearConstants.NETWORK_SUB_NAME, networkStateChange.getNetworkInfo().getSubtypeName());
                putDataMapReqNetChange.getDataMap().putString(WearConstants.NETWORK_STATE, networkStateChange.getNetworkInfo().getState().toString());
                putDataMapReqNetChange.getDataMap().putString(WearConstants.NETWORK_EXTRA_INFO, networkStateChange.getSsidExtra());
                PutDataRequest putDataReqNetChange = putDataMapReqNetChange.asPutDataRequest();
                putDataReqNetChange.setUrgent();
                PendingResult<DataApi.DataItemResult> pendingResultNetChange =
                        Wearable.DataApi.putDataItem(googleApiClient, putDataReqNetChange);
                Wearable.DataApi.deleteDataItems(googleApiClient, putDataMapReqNetChange.getUri());
                break;
            default:
                break;

        }
    }
}