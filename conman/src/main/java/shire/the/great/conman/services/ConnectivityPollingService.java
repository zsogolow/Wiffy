package shire.the.great.conman.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import shire.the.great.wearman.common.Constants;
import shire.the.great.wearman.models.parcelables.NetworkStateChange;

/**
 * Created by ZachS on 4/1/2016.
 */
public class ConnectivityPollingService extends Service {
    private static final String LOGTAG = "ConnPollingService";

    private boolean isRunning  = false;
    private Looper looper;
    private MyServiceHandler myServiceHandler;
    private LocalBroadcastManager mBroadcastManager;

    @Override
    public void onCreate() {
        HandlerThread handlerthread = new HandlerThread("HandlerThread", Thread.MIN_PRIORITY);
        handlerthread.start();
        looper = handlerthread.getLooper();
        myServiceHandler = new MyServiceHandler(looper);
        isRunning = true;
        mBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Message msg = myServiceHandler.obtainMessage();
        msg.arg1 = startId;
        myServiceHandler.sendMessage(msg);
        Toast.makeText(this, "ConnectivityPollingService Started.", Toast.LENGTH_SHORT).show();
        //If service is killed while starting, it restarts.
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        Toast.makeText(this, "ConnectivityPollingService Completed or Stopped.", Toast.LENGTH_SHORT).show();
    }
    private final class MyServiceHandler extends Handler {
        public MyServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            synchronized (this) {
                while(isRunning) {
                    try {
                       // Log.i(LOGTAG, "ConnectivityPollingService running...");

                        WifiManager wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
                        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

                        ConnectivityManager cm =
                                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

                        Intent resultIntent = new Intent();
                        resultIntent.setAction(Constants.ACTION_RESULT_CONNECTIVITY_INFO);
                        resultIntent.putExtra(Constants.RESULT_CONNECTIVITY_INFO, new NetworkStateChange(activeNetwork, wifiInfo, ""));
                        mBroadcastManager.sendBroadcast(resultIntent);

                        Thread.sleep(500);
                    } catch (Exception e) {
                        Log.i(LOGTAG, e.getMessage());
                    }
                }
            }
            //stops the service for the start id.
            stopSelfResult(msg.arg1);
        }
    }
}
