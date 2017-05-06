package com.sharedclipboard;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.sharedclipboard.service.ClipListenerService;
import com.sharedclipboard.storage.db.DBHelper;

public class SharedClipperApp extends Application {
    public static final String TAG = "SharedClipperApp";
    private DBHelper dbHelper = null ;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("VVV","SharedClipperApp :- onCreate");
        ClipListenerService.start(getBaseContext());
        dbHelper = new DBHelper(getBaseContext());
    }

    public static DBHelper getDb(Context appContext){
        return ((SharedClipperApp)appContext.getApplicationContext()).dbHelper;
    }
    public static final String getTag(String appendTag) {
        return TAG + " : " + appendTag;
    }

    public static final void printTrace(String tag, String msg) {
        Log.v(tag, msg);
    }
}
