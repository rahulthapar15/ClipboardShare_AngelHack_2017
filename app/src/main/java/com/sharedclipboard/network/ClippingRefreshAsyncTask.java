package com.sharedclipboard.network;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.sharedclipboard.MainActivity;
import com.sharedclipboard.SharedClipperApp;
import com.sharedclipboard.storage.db.models.Clipping;
import com.sharedclipboard.storage.preferences.PreferenceUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class ClippingRefreshAsyncTask extends AsyncTask<String, Integer, Double> {

    private Context mContext;
    private MainActivity mainActivity;

    public ClippingRefreshAsyncTask (MainActivity m){
        mContext = m.getBaseContext();
        mainActivity = m;
    }

    @Override
    protected Double doInBackground(String... params) {
        String endpoint = NetworkUtils.SERVER_URL + "/refresh.do";
        URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }

        StringBuilder stringBuilder =  new StringBuilder();
        List<Clipping> clippings = Clipping.getAllClippings(SharedClipperApp.getDb(mContext).getClippingAll(true));
        for(Clipping clipping : clippings) {
            stringBuilder.append(Long.toString(clipping.getDate()));
            stringBuilder.append(":");
        }

        String passcode = PreferenceUtils.getString(mContext, "passcode","null");

        String clippingsString = stringBuilder.toString();
        String body = "clippings=" + clippingsString + "&passcode=" + passcode;
        Log.d("body", body);

        byte[] bytes = body.getBytes();
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
            // post the request
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();

            // handle the response
            int status = conn.getResponseCode();
            Log.d("TAGG", "" + status);
            if (status != 200) {
                throw new IOException("Post failed with error code " + status);
            }

            LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent("refresh"));

        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null)
                conn.disconnect();
        }
        return null;
    }

    protected void onPostExecute(Double result){
        mainActivity.refreshGridView();
    }
    protected void onProgressUpdate(Integer... progress){
    }
}
