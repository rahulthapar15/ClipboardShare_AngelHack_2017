package com.sharedclipboard.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sharedclipboard.service.ClipListenerService;

public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("VVV","BootReceiver :- onReceive");
        ClipListenerService.start(context);
    }
}
