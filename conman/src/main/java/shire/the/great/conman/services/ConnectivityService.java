package shire.the.great.conman.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.content.LocalBroadcastManager;

import shire.the.great.conman.common.Constants;
import shire.the.great.conman.models.parcelables.NetworkStateChange;

/**
 * Created by ZachS on 4/1/2016.
 */
public class ConnectivityService extends IntentService {
    private static final String LOGTAG = "ConnectivityService";

    public ConnectivityService() {
        super("ConnectivityService");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
        String dataString = workIntent.getDataString();

        // results
        Intent resultIntent = new Intent();

        // Do work here, based on the contents of dataString
        switch (dataString) {
            case Constants.GET_CONNECTIVITY_INFO_ACTION:
                WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();

                ConnectivityManager cm =
                        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

                resultIntent.setAction(Constants.ACTION_RESULT_CONNECTIVITY_INFO);
                resultIntent.putExtra(Constants.RESULT_CONNECTIVITY_INFO, new NetworkStateChange(activeNetwork, wifiInfo, ""));
                break;

            default:
                break;
        }

        // Broadcasts the Intent to receivers in this app.
        LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent);
    }
}
