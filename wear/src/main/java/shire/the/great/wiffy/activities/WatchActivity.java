package shire.the.great.wiffy.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.HashMap;

import shire.the.great.wearman.common.WearConstants;
import shire.the.great.wiffy.R;

public class WatchActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String LOGTAG = "WatchActivity";

    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;

    private static final String STATE_RESOLVING_ERROR = "resolving_error";

    private BroadcastReceiver mConnectivityReceiver;
    private BroadcastReceiver mWearableConnectedReceiver;

    private Switch mWifiSwitch;
    private TextView mSsidTV;
    private TextView mSuppStateTV;
    private TextView mDisconnectedTV;
    private TextView mConnectionNameTV;
    private TextView mSubConnectionTV;

    private GoogleApiClient mGoogleApiClient;

    private int mConnectedCount = 0;

    private HashMap<String, Integer> mUpdateIdMap;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mWifiSwitch = (Switch) findViewById(R.id.wifiSwitch);
                mSuppStateTV = (TextView) findViewById(R.id.suppStateTV);
                mSsidTV = (TextView) findViewById(R.id.ssidTV);
                mDisconnectedTV = (TextView) findViewById(R.id.disconnectedTV);
                mConnectionNameTV = (TextView) findViewById(R.id.connectionNameTV);
                mSubConnectionTV = (TextView) findViewById(R.id.subConnNameTV);
            }
        });

        mResolvingError = savedInstanceState != null
                && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mConnectivityReceiver = new MyConnectivityReceiver();
        mWearableConnectedReceiver = new WearConnectedBroadcastReceiver();
        registerConnectivityReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {  // more about this later
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        Log.d(LOGTAG, "onStop");
        sendMessageAsync(WearConstants.DISCONNECT);
        unregisterConnectivityReceiver();
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(LOGTAG, "onStop");
        super.onDestroy();
    }

    public void onWearWifiClicked(View view) {
        Switch wifiSwitch = (Switch) view;
        if (wifiSwitch.isChecked()) {
            sendMessageAsync(WearConstants.TOGGLE_WIFI_ON);
        } else {
            sendMessageAsync(WearConstants.TOGGLE_WIFI_OFF);
        }
    }

    private void setDisconnectedFromPhone(boolean disconnected) {
        if (disconnected) {
            mDisconnectedTV.setText("DISCONNECTED");
            mSuppStateTV.setText("");
            mSsidTV.setText("");
            mConnectionNameTV.setText(R.string.app_name);
            mWifiSwitch.setChecked(false);
            mSubConnectionTV.setText("");
        } else {
            mDisconnectedTV.setText("");
        }
    }

    private void sendMessageAsync(final String message) {
        if (mGoogleApiClient.isConnected()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
                    for (Node node : nodes.getNodes()) {
                        MessageApi.SendMessageResult result =
                                Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), message, message.getBytes()).await();
                        if (!result.getStatus().isSuccess()) {
                            Log.e(LOGTAG, "error");
                            Intent intent = new Intent();
                            intent.setAction(WearConstants.WEARABLE_DISCONNECTED);
                            LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
                        } else {
                            Log.i(LOGTAG, "success!! sent to: " + node.getDisplayName());
                        }
                    }
                }
            }).start();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        new CheckConnectionTask(this).execute(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (result.getErrorCode() == ConnectionResult.API_UNAVAILABLE) {
            // The Wearable API is unavailable
        }

        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GoogleApiAvailability.getErrorDialog()
            showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }
    }

    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }

    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GoogleApiAvailability.getInstance().getErrorDialog(
                    this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((WatchActivity) getActivity()).onDialogDismissed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mGoogleApiClient.isConnecting() &&
                        !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    /**
     * Receiver for handling Connectivity Changes and Wifi State changes.
     */
    class MyConnectivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(LOGTAG, intent.getIntExtra(WearConstants.UPDATE_ID, -100) + "");
            int updateId = intent.getIntExtra(WearConstants.UPDATE_ID, -100);
            switch (intent.getAction()) {
                case WearConstants.NETWORK_CONNECTION_DATA:
                    if (mUpdateIdMap.get(WearConstants.NETWORK_CONNECTION_DATA) < updateId) {
                        mUpdateIdMap.put(WearConstants.NETWORK_CONNECTION_DATA, updateId);
                        String netName = intent.getStringExtra(WearConstants.NETWORK_NAME);
                        String netSubName = intent.getStringExtra(WearConstants.NETWORK_SUB_NAME);
                        String netState = intent.getStringExtra(WearConstants.NETWORK_STATE);
                        String extraInfo = intent.getStringExtra(WearConstants.NETWORK_EXTRA_INFO);
                        if (netName.equals("WIFI") && netState.equals("CONNECTED")) {
                            mConnectionNameTV.setText(netName);
                            mSsidTV.setText(extraInfo);
                            mSubConnectionTV.setText("");
                        } else if (netName.equals("MOBILE") && netState.equals("CONNECTED")) {
                            mConnectionNameTV.setText(netName);
                            mSsidTV.setText("");
                            mSubConnectionTV.setText(netSubName);
                        }
                    }
                    break;

                case WearConstants.WIFI_STATE_DATA:
                    if (mUpdateIdMap.get(WearConstants.WIFI_STATE_DATA) < updateId) {
                        mUpdateIdMap.put(WearConstants.WIFI_STATE_DATA, updateId);
                        boolean wifiState = intent.getBooleanExtra(WearConstants.WIFI_STATE, false);
                        boolean checked = mWifiSwitch.isChecked();
                        if (!checked && wifiState) {
                            mWifiSwitch.setChecked(true);
                        } else if (checked && !wifiState) {
                            mWifiSwitch.setChecked(false);
                        }
                    }
                    break;

                case WearConstants.SUPPLICANT_STATE_DATA:
                    if (mUpdateIdMap.get(WearConstants.SUPPLICANT_STATE_DATA) < updateId) {
                        mUpdateIdMap.put(WearConstants.SUPPLICANT_STATE_DATA, updateId);
                        String suppState = intent.getStringExtra(WearConstants.SUPPLICANT_STATE);
                        Log.d(LOGTAG, suppState);
                        mSuppStateTV.setText(suppState);
                    }
                    break;

                default:
                    break;
            }
        }
    }

    class WearConnectedBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case WearConstants.WEARABLE_CONNECTED:
                    mUpdateIdMap = new HashMap<>();
                    mUpdateIdMap.put(WearConstants.NETWORK_CONNECTION_DATA, -1);
                    mUpdateIdMap.put(WearConstants.SUPPLICANT_STATE_DATA, -1);
                    mUpdateIdMap.put(WearConstants.WIFI_STATE_DATA, -1);
                    Log.d(LOGTAG, "receive connceted");
                    setDisconnectedFromPhone(false);
                    sendMessageAsync(WearConstants.CONNECT);
                    mConnectedCount++;
                    break;
                case WearConstants.WEARABLE_DISCONNECTED:
                    setDisconnectedFromPhone(true);
                    mConnectedCount--;
                    break;
                default:
                    break;
            }
            Log.d("WEARCONNECTED", mConnectedCount + "");
        }
    }

    private void registerConnectivityReceiver() {
        // MyConnectivityReceiver, used for custom actions
        IntentFilter mConnectivityChangedFilter =
                new IntentFilter(WearConstants.NETWORK_CONNECTION_DATA);
        IntentFilter mWifiStateChangedFilter =
                new IntentFilter(WearConstants.WIFI_STATE_DATA);
        IntentFilter mSuppStateChangedFilter =
                new IntentFilter(WearConstants.SUPPLICANT_STATE_DATA);
        // Register the receiver's and their IntentFilter's
        LocalBroadcastManager.getInstance(this).registerReceiver(mConnectivityReceiver, mConnectivityChangedFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(mConnectivityReceiver, mWifiStateChangedFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(mConnectivityReceiver, mSuppStateChangedFilter);


        IntentFilter mWearConnectedFitler =
                new IntentFilter(WearConstants.WEARABLE_CONNECTED);
        IntentFilter mWearDisconnFilter =
                new IntentFilter(WearConstants.WEARABLE_DISCONNECTED);

        LocalBroadcastManager.getInstance(this).registerReceiver(mWearableConnectedReceiver, mWearConnectedFitler);
        LocalBroadcastManager.getInstance(this).registerReceiver(mWearableConnectedReceiver, mWearDisconnFilter);
    }

    private void unregisterConnectivityReceiver() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mConnectivityReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mWearableConnectedReceiver);
    }
}
