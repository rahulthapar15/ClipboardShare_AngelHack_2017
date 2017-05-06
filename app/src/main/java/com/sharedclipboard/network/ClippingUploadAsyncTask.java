package com.sharedclipboard.network;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.sharedclipboard.storage.preferences.PreferenceUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ClippingUploadAsyncTask extends AsyncTask<String, Integer, Double> {

    String endpoint = NetworkUtils.SERVER_URL + "/add.do";
    private Context mContext;

    public ClippingUploadAsyncTask (Context context){
        mContext = context;
    }

    @Override
    protected Double doInBackground(String... params) {
        URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }

        String passcode = PreferenceUtils.getString(mContext, "passcode","null");
        String inputType = "phone";
        String clipping = params[0];
        String body = "passcode=" + passcode + "&input_type=" + inputType + "&clipping=" + clipping
                + "&time=" + params[1];

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

        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null)
                conn.disconnect();
        }

        return null;
    }

    protected void onPostExecute(Double result){
    }
    protected void onProgressUpdate(Integer... progress){
    }


}
