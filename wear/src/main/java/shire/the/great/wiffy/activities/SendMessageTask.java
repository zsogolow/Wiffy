package shire.the.great.wiffy.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import shire.the.great.wearman.common.WearConstants;
import shire.the.great.wearman.common.WearableApiHelper;

/**
 * Created by ZachS on 4/12/2016.
 */
public class SendMessageTask extends AsyncTask<String, Long, String> {
    private static final String LOGTAG = "SendMessageTask";
    private Context mContext;

    public SendMessageTask(Context context) {
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected String doInBackground(String... params) {
        GoogleApiClient client = WearableApiHelper.createAndConnectGoogleApiClient(mContext, 1000);
        Node opponent = WearableApiHelper.getOpponentNode(client, 1000);
        if (opponent != null) {
            MessageApi.SendMessageResult result =
                    Wearable.MessageApi.sendMessage(client, opponent.getId(),
                            params[0], params[0].getBytes()).await();
            if (!result.getStatus().isSuccess()) {
                Log.e(LOGTAG, "error");
                Intent intent = new Intent();
                intent.setAction(WearConstants.WEARABLE_DISCONNECTED);
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            } else {
                Log.i(LOGTAG, "success!! sent to: " + opponent.getDisplayName());
            }
        } else {
            return "No nodes!";
        }

        return "Complete";
    }
}
