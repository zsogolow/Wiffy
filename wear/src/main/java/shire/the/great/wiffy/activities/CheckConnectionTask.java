package shire.the.great.wiffy.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;

import shire.the.great.wearman.common.WearConstants;

/**
 * Created by ZachS on 4/6/2016.
 */
public class CheckConnectionTask extends AsyncTask<GoogleApiClient, Long, List<Node>> {
    private Context mContext;

    public CheckConnectionTask(Context context) {
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(List<Node> nodes) {
        Log.d("postExecuted", nodes.size() + "");
        boolean isConnected = false;
        for (Node node : nodes) {
            if (node.isNearby()) {
                isConnected = true;
            }
        }
        Intent intent = new Intent();
        if (isConnected) {
            intent.setAction(WearConstants.WEARABLE_CONNECTED);
            intent.putExtra(WearConstants.RESULT_NODE_CONNECTED, true);
        } else {
            intent.setAction(WearConstants.WEARABLE_DISCONNECTED);
            intent.putExtra(WearConstants.RESULT_NODE_CONNECTED, false);
        }
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled(List<Node> nodes) {
        super.onCancelled(nodes);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected List<Node> doInBackground(GoogleApiClient... params) {
        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(params[0]).await();
        return nodes.getNodes();
    }
}