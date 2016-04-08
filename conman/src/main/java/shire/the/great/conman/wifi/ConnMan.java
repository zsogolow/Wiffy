package shire.the.great.conman.wifi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.List;

import shire.the.great.conman.models.parcelables.ConnectionChange;
import shire.the.great.conman.models.parcelables.NetworkStateChange;
import shire.the.great.conman.models.parcelables.SupplicantStateChange;
import shire.the.great.conman.models.parcelables.WifiStateChange;

/**
 * Created by ZachS on 4/1/2016.
 */
public class ConnMan implements IWifiMan {
    private Context mExecutionContext;

    public ConnMan(Context context) {
        this.mExecutionContext = context;
    }

    @Override
    public boolean turnOnWifi() {
        WifiManager wifiManager = (WifiManager)
                mExecutionContext.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.setWifiEnabled(true);
    }

    @Override
    public boolean turnOffWifi() {
        WifiManager wifiManager = (WifiManager)
                mExecutionContext.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.setWifiEnabled(false);
    }

    @Override
    public boolean isWifiEnabled() {
        WifiManager wifiManager = (WifiManager)
                mExecutionContext.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    @Override
    public boolean isWifiConnected() {
        WifiManager wifiManager = (WifiManager)
                mExecutionContext.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING ||
                wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED;
    }

    @Override
    public boolean startScan() {
        WifiManager wifiManager = (WifiManager)
                mExecutionContext.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.startScan();
    }

    @Override
    public List<ScanResult> getWifiScanResults() {
        WifiManager wifiManager = (WifiManager)
                mExecutionContext.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.getScanResults();
    }

    @Override
    public WifiStateChange getWifiState() {
        WifiManager wifiManager = (WifiManager)
                mExecutionContext.getSystemService(Context.WIFI_SERVICE);

        WifiStateChange wifiStateChange = new WifiStateChange(-1, wifiManager.getWifiState());
        return wifiStateChange;
    }

    @Override
    public ConnectionChange getConnectionData() {
        ConnectivityManager cm =
                (ConnectivityManager) mExecutionContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null) {
            // collect information about the network change
            NetworkInfo.State networkState = networkInfo.getState();
            NetworkInfo.DetailedState networkDetailedState = networkInfo.getDetailedState();
            int networkType = networkInfo.getType();
            String networkName = networkInfo.getTypeName();
            int networkSubType = networkInfo.getSubtype();
            String networkSubName = networkInfo.getSubtypeName();
            boolean isAvailable = networkInfo.isAvailable();
            boolean isConnected = networkInfo.isConnected();
            String activeConnName = networkInfo.getExtraInfo();

            ConnectionChange connectionChange =
                    new ConnectionChange(networkState, networkDetailedState, networkType, networkName,
                            networkSubType, networkSubName, isAvailable, isConnected, activeConnName);
            return connectionChange;
        } else {
            return new ConnectionChange(NetworkInfo.State.DISCONNECTED,
                    NetworkInfo.DetailedState.IDLE, 0, "" , 0, "", false, false, "<unknown ssid>");
        }
    }

    @Override
    public NetworkStateChange getCurrentNetworkState() {
        ConnectivityManager cm =
                (ConnectivityManager) mExecutionContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        WifiManager wifiManager = (WifiManager)
                mExecutionContext.getSystemService(Context.WIFI_SERVICE);

        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return new NetworkStateChange(networkInfo, wifiInfo, wifiInfo.getSSID());
    }

    @Override
    public SupplicantStateChange getSupplicantState() {
        WifiManager wifiManager = (WifiManager)
                mExecutionContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();

        SupplicantState state = null;
        if (info != null) {
            state = info.getSupplicantState();
        } else {
            state = SupplicantState.DISCONNECTED;
        }
        return new SupplicantStateChange(state, 0);
    }
}
