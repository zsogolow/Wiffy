package shire.the.great.conman.wifi;

import android.net.wifi.ScanResult;

import java.util.List;

import shire.the.great.conman.models.parcelables.ConnectionChange;
import shire.the.great.conman.models.parcelables.NetworkStateChange;
import shire.the.great.conman.models.parcelables.SupplicantStateChange;
import shire.the.great.conman.models.parcelables.WifiStateChange;

/**
 * Created by ZachS on 4/2/2016.
 */
public interface IWifiMan {
    boolean turnOnWifi();

    boolean turnOffWifi();

    boolean isWifiEnabled();

    boolean isWifiConnected();

    boolean startScan();

    List<ScanResult> getWifiScanResults();

    WifiStateChange getWifiState();

    ConnectionChange getConnectionData();

    NetworkStateChange getCurrentNetworkState();

    SupplicantStateChange getSupplicantState();
}
