package shire.the.great.wiffy.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import shire.the.great.conman.models.parcelables.NetworkStateChange;
import shire.the.great.conman.models.parcelables.RssiChange;
import shire.the.great.conman.models.parcelables.ScanResultsChange;
import shire.the.great.conman.models.parcelables.ConnectionChange;
import shire.the.great.conman.models.parcelables.SupplicantConnectionChange;
import shire.the.great.conman.models.parcelables.SupplicantStateChange;
import shire.the.great.conman.models.parcelables.WifiStateChange;
import shire.the.great.conman.wifi.ConnMan;
import shire.the.great.wiffy.R;
import shire.the.great.conman.common.Constants;

public class MonitoringActivity extends AppCompatActivity {
    private static final String LOGTAG = "MonitoringActivity";

    private BroadcastReceiver mConnectivityReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring);

        // create our receiver's
        mConnectivityReceiver = new MyConnectivityReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // idea is to reinitialize anything that might have changed
        // while outside of the app
        // set the initial state of the switch
        boolean isWifiEnabled = new ConnMan(this).isWifiEnabled();
        setWifiSwitch(isWifiEnabled);
        setConnectionChanging("");
        setConnectionStuff(new ConnMan(this).getCurrentNetworkState());
    }

    @Override
    protected void onStart() {
        super.onStart();
        // register the receivers
        registerConnectivityReceiver();
    }

    @Override
    protected void onStop() {
        // unregister the receivers
        unregisterConnectivityReceiver();

        super.onStop();
    }

    /**
     * Called when we receive a full ConnectionStateCurrent object with
     * all of the current Connection information.
     *
     * @param current The ConnectionStateCurrent object
     */
    private void updateMonitor(NetworkStateChange current) {
        //Log.d("updateMonitor", current.getNetworkName());
        setConnectionStuff(current);
    }

    /**
     * Called when we receive a ConnectionChange1 broadcast.
     * <p/>
     * Called when the internet connection has changed.
     *
     * @param connectionChange the ConnectionChange1 object
     */
    private void connectionChanged(ConnectionChange connectionChange) {
        Log.d("connectionChanged", connectionChange.getNetworkName() + ", " + connectionChange.getNetworkState());

        switch (connectionChange.getNetworkState()) {
            case CONNECTED:
                String connectString = "connected to " + connectionChange.getNetworkName();
                Log.d("connectionChanged", connectString);
                setConnectionChanging(connectString);
                break;
            case CONNECTING:
                String connectingString = "connecting to " + connectionChange.getNetworkName();
                Log.d("connectionChanged", connectingString);
                setConnectionChanging(connectingString);
                break;
            case DISCONNECTED:
                String disconnectString = "disconnected from " + connectionChange.getNetworkName();
                Log.d("connectionChanged", disconnectString);
                setConnectionChanging(disconnectString);
                break;
            case DISCONNECTING:
                String disconnectingString = "disconnecting from " + connectionChange.getNetworkName();
                Log.d("connectionChanged", disconnectingString);
                setConnectionChanging(disconnectingString);
                break;
            case SUSPENDED:
            case UNKNOWN:
            default:
                break;
        }
    }

    /**
     * Called when we receive a WifiStateChange1 broadcast.
     * <p/>
     * Only used to show the state of the wifi switch in the system.
     * Has nothing to do with connectivity. Just wifi state.
     *
     * @param wifiStateChange the WifiStateChange1 object
     */
    private void wifiStateChanged(WifiStateChange wifiStateChange) {
        Log.d("wifiStateChanged", wifiStateChange.getCurrentState() + "");

        // switch on the current state
        // DISABLED, DISABLING, ENABLED, ENABLING, UNKNOWN
        switch (wifiStateChange.getCurrentState()) {
            case WifiManager.WIFI_STATE_ENABLED:
            case WifiManager.WIFI_STATE_ENABLING:
                setWifiSwitch(true);
                break;
            case WifiManager.WIFI_STATE_DISABLED:
            case WifiManager.WIFI_STATE_DISABLING:
                setWifiSwitch(false);
                break;
            case WifiManager.WIFI_STATE_UNKNOWN:
            default:
                break;
        }
    }

    /**
     * Called when we receive a ScanResults Available Action.
     * Not fully implemented, in 6.0, the permissions for location
     * are different. Must use the in-app prompt.
     *
     * @param scanResultsCurrent The ScanResultsChange object, indicating
     *                           true/false if the scan was completed successfully
     */
    private void scanResultsAvailable(ScanResultsChange scanResultsCurrent) {
        if (scanResultsCurrent.isResultsAvailable()) {
            List<ScanResult> scanResults = new ConnMan(this).getWifiScanResults();
            for (int i = 0; i < scanResults.size(); i++) {
                ScanResult currResult = scanResults.get(i);
                Log.d("ScanResult", currResult.SSID);
            }
        }
    }

    private void rssiValueChanged(RssiChange rssiChange) {
        ((TextView) findViewById(R.id.wifiRssi)).setText("" + rssiChange.getRssiLevel());
    }

    private void setWifiSwitch(boolean enabled) {
        ((Switch) findViewById(R.id.wifiSwitch)).setChecked(enabled);
        ((TextView) findViewById(R.id.wifiEnabledTextView)).setText(enabled ? "enabled" : "disabled");
    }

    private void setConnectionStuff(NetworkStateChange connectionStuff) {
        ((TextView) findViewById(R.id.networkName)).setText(connectionStuff.getNetworkInfo().getTypeName());
        ((TextView) findViewById(R.id.networkSubName)).setText(connectionStuff.getNetworkInfo().getSubtypeName());

        switch (connectionStuff.getNetworkInfo().getType()) {
            case ConnectivityManager.TYPE_WIFI:
                displayWifiStats(connectionStuff);
                break;
            case ConnectivityManager.TYPE_MOBILE:
                hideWifiStats();
                break;
            default:
                break;
        }
    }

    private void setSupplicantState(SupplicantStateChange stateChange) {
        ((TextView) findViewById(R.id.wifiSupState))
                .setText(stateChange.getSupplicantState().toString());
    }

    private void setConnectionChanging(String connChangingString) {
        ((TextView) findViewById(R.id.connectionChanging)).setText(connChangingString);
    }

    private void displayWifiStats(NetworkStateChange connectionStuff) {
        findViewById(R.id.wifiStatsPanel).setVisibility(View.VISIBLE);
        WifiInfo wifiStateCurrent = connectionStuff.getWifiInfo();
        if (wifiStateCurrent != null) {
            ((TextView) findViewById(R.id.wifiSSID)).setText(wifiStateCurrent.getSSID());
            ((TextView) findViewById(R.id.wifiMacAddress)).setText(wifiStateCurrent.getMacAddress());
            ((TextView) findViewById(R.id.wifiIpAddress1)).setText(wifiStateCurrent.getIpAddress() + "");
            ((TextView) findViewById(R.id.wifiNetworkId)).setText(wifiStateCurrent.getNetworkId() + "");
            ((TextView) findViewById(R.id.wifiBSSID)).setText(wifiStateCurrent.getBSSID());
            ((TextView) findViewById(R.id.wifiDetailedState)).setText(WifiInfo.getDetailedStateOf(wifiStateCurrent.getSupplicantState()).toString());
            ((TextView) findViewById(R.id.wifiSupState)).setText(wifiStateCurrent.getSupplicantState().toString());
            ((TextView) findViewById(R.id.wifiFrequency)).setText(wifiStateCurrent.getFrequency() + "");
            ((TextView) findViewById(R.id.wifiLinkSpeed)).setText(wifiStateCurrent.getLinkSpeed() + "");
            ((TextView) findViewById(R.id.wifiRssi)).setText(wifiStateCurrent.getRssi() + "");
        }
    }

    private void hideWifiStats() {
        findViewById(R.id.wifiStatsPanel).setVisibility(View.GONE);
    }

    private void registerConnectivityReceiver() {
        // AlwaysActiveConnectivityReceiver, used for custom actions
        IntentFilter mConnectivityChangedFilter =
                new IntentFilter(Constants.CONNECTIVITY_CHANGED_ACTION);
        IntentFilter mWifiStateChangedFilter =
                new IntentFilter(Constants.WIFI_STATE_CHANGED_ACTION);
        IntentFilter mWifiScanResultsFilter =
                new IntentFilter(Constants.WIFI_SCAN_RESULTS_AVAILABLE_ACTION);
        IntentFilter mRssiFilter =
                new IntentFilter(Constants.RSSI_CHANGED_ACTION);
        IntentFilter mNetworkIdsFilter =
                new IntentFilter(Constants.NETWORK_IDS_CHANGED_ACTION);
        IntentFilter mNetworkStateFilter =
                new IntentFilter(Constants.NETWORK_STATE_CHANGED_ACTION);
        IntentFilter mSuppStateFilter =
                new IntentFilter(Constants.SUPPLICANT_STATE_CHANGED_ACTION);
        IntentFilter mSuppConnFilter =
                new IntentFilter(Constants.SUPPLICANT_CONNECTION_CHANGED_ACTION);

        // Register the receiver's and their IntentFilter's
        LocalBroadcastManager.getInstance(this).registerReceiver(mConnectivityReceiver, mConnectivityChangedFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(mConnectivityReceiver, mWifiStateChangedFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(mConnectivityReceiver, mWifiScanResultsFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(mConnectivityReceiver, mRssiFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(mConnectivityReceiver, mNetworkIdsFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(mConnectivityReceiver, mNetworkStateFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(mConnectivityReceiver, mSuppConnFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(mConnectivityReceiver, mSuppStateFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(mConnectivityReceiver, mNetworkStateFilter);
    }

    private void unregisterConnectivityReceiver() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mConnectivityReceiver);
    }

    /**
     * The onClick event for the wifi switch.
     *
     * @param view Switch object (id=wifiSwitch)
     */
    public void onToggleSwitchClicked(View view) {
        Switch switch1 = (Switch) view;
        if (switch1.isChecked()) {
            new ConnMan(this).turnOnWifi();
        } else {
            new ConnMan(this).turnOffWifi();
        }
    }

    /**
     * Receiver for handling Connectivity Changes and Wifi State changes.
     */
    class MyConnectivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(LOGTAG, "from activity");
            switch (intent.getAction()) {
                case Constants.CONNECTIVITY_CHANGED_ACTION:
                    ConnectionChange result =
                            intent.getParcelableExtra(Constants.RESULT_CONNECTION_CHANGED);
                    connectionChanged(result);
                    break;

                case Constants.WIFI_STATE_CHANGED_ACTION:
                    WifiStateChange wifiResult =
                            intent.getParcelableExtra(Constants.RESULT_WIFI_STATE);
                    wifiStateChanged(wifiResult);
                    break;

                case Constants.WIFI_SCAN_RESULTS_AVAILABLE_ACTION:
                    ScanResultsChange scanResultsCurrent =
                            intent.getParcelableExtra(Constants.RESULT_WIFI_SCAN_RESULTS);
                    scanResultsAvailable(scanResultsCurrent);
                    break;

                case Constants.NETWORK_IDS_CHANGED_ACTION:
                    break;

                case Constants.NETWORK_STATE_CHANGED_ACTION:
                    NetworkStateChange currentConnection =
                            intent.getParcelableExtra(Constants.RESULT_NETWORK_STATE_CHANGED);
                    setConnectionStuff(currentConnection);
                    break;

                case Constants.RSSI_CHANGED_ACTION:
                    RssiChange rssiChange =
                            intent.getParcelableExtra(Constants.RESULT_RSSI_CHANGED);
                    rssiValueChanged(rssiChange);
                    break;

                case Constants.SUPPLICANT_CONNECTION_CHANGED_ACTION:
                    SupplicantConnectionChange supplicantConnectionChange =
                            intent.getParcelableExtra(Constants.RESULT_SUPPLICANT_CONNECTION_CHANGED);
                    break;

                case Constants.SUPPLICANT_STATE_CHANGED_ACTION:
                    SupplicantStateChange supplicantStateChange =
                            intent.getParcelableExtra(Constants.RESULT_SUPPLICANT_STATE_CHANGED);
                    setSupplicantState(supplicantStateChange);
                    break;

                default:
                    break;
            }
        }
    }
}
