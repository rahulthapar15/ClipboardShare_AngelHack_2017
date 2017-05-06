package com.sharedclipboard.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.sharedclipboard.MainActivity;
import com.sharedclipboard.R;
import com.sharedclipboard.SharedClipperApp;
import com.sharedclipboard.network.ClippingUploadAsyncTask;
import com.sharedclipboard.storage.db.models.Clipping;
import com.sharedclipboard.ui.widget.ClippingWidget;

public class ClipListenerService extends Service {

    public static final String EXTRA_ACTION_TYPE = "extra_action_type";
    public static final String EXTRA_CLIPPING_ID = "extra_clipping_id";
    public static final String EXTRA_CLIP = "extra_clip";
    public static final String EXTRA_CLIP_TIME = "extra_clip_time";
    public static final int ACTION_TYPE_CLICK = 1;
    public static final int ACTION_TYPE_ADD = 2;
    public static final int ACTION_TYPE_UPDATE_WIDGETS = 3;

    private ClipboardManager mClipManager = null;
    private Clipping previousClip = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("VVV","ClipListenerService :- onCreate");
        initClipper();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.hasExtra(EXTRA_ACTION_TYPE)){
            doClippingAction(intent);
        }
        return START_NOT_STICKY;
    }

    private void doClippingAction(Intent intent){
        Log.e("VVV","doClippingAction");
        int actionType = intent.getIntExtra(EXTRA_ACTION_TYPE,-1);
        if(actionType == ACTION_TYPE_CLICK){
            Log.e("VVV","ACTION_TYPE_CLICK");
            long id = intent.getLongExtra(EXTRA_CLIPPING_ID,-1);
            Log.e("VVV", "clicked clipping id = " + id);
            if(id > 0){
                copyToClipboard(id);
            }
        }else if(actionType == ACTION_TYPE_ADD){
            long time = intent.getLongExtra(EXTRA_CLIP_TIME,System.currentTimeMillis());
            String clip = intent.getStringExtra(EXTRA_CLIP);
            addAndUpdateNewClip(clip,time);
        }else if(actionType == ACTION_TYPE_UPDATE_WIDGETS){
            updateWidgets();
        }
    }

    private void addAndUpdateNewClip(String clip, long time) {
        if(mClipManager!=null){
            Clipping clipping = new Clipping(clip,time);
            long id = SharedClipperApp.getDb(getBaseContext()).insertClipping(clipping);
            if(id > 0) {
                mClipManager.removePrimaryClipChangedListener(mClipListener);
                mClipManager.setPrimaryClip(clipping.toClipData());
                mClipManager.addPrimaryClipChangedListener(mClipListener);
                sendNotification(clip);
                updateWidgets();
            }

        }
    }

    private void copyToClipboard(long id) {
        Log.e("VVV", "ClipListenerService copyToClipboard() id = " + id);
        try {
            Clipping clipping = new Clipping(SharedClipperApp.getDb(getBaseContext()).getClipping(id));
            if(mClipManager != null){
                mClipManager.removePrimaryClipChangedListener(mClipListener);
                mClipManager.setPrimaryClip(clipping.toClipData());
                mClipManager.addPrimaryClipChangedListener(mClipListener);
                Toast.makeText(getBaseContext(),getString(R.string.copied_to_clipboard),Toast.LENGTH_SHORT).show();
                Log.e("VVV","copied to clipboard");
            }
        }catch (Exception ex){
            Log.e("VVV" , "Exception in copying clipping");
            ex.printStackTrace();
        }

    }

    private void initClipper(){
        Log.e("VVV","ClipListenerService :- initClipper");
        mClipManager = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        mClipManager.addPrimaryClipChangedListener(mClipListener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy(){
        Log.e("VVV","onDestroy");
        mClipManager.removePrimaryClipChangedListener(mClipListener);
        //ClipListenerService.start(getBaseContext());
    }

    public static void start(Context context){
        Intent intent = new Intent(context,ClipListenerService.class);
        context.startService(intent);
    }

    private ClipboardManager.OnPrimaryClipChangedListener mClipListener = new ClipboardManager.OnPrimaryClipChangedListener() {
        @Override
        public void onPrimaryClipChanged() {
            Log.e("VVV","onPrimaryClipChanged");
            ClipData clip = mClipManager.getPrimaryClip();
            if(clip.getItemCount() > 0) {
                ClipData.Item item  = clip.getItemAt(0);
                if(item!=null && item.getText() != null) {
                    Long time = System.currentTimeMillis();
                    Clipping clipping = new Clipping(item.getText().toString(),1, time);
                    if(previousClip == null || (!previousClip.getClipping().equals(clipping.getClipping()))) {
                        previousClip = clipping;
                        long id = SharedClipperApp.getDb(getBaseContext()).insertClipping(clipping);
                        Log.e("VVV", "Clipping insert ID = " + id);
                        sendNotification(item.getText().toString());
                        updateWidgets();
                        ClippingUploadAsyncTask uploadAsyncTask = new ClippingUploadAsyncTask(getBaseContext());
                        String[] params = new String[2];
                        params[0] = item.getText().toString();
                        params[1] = Long.toString(time);
                        uploadAsyncTask.execute(params);
                        Log.d("prev time", params[1]);
                    }
                }
            }
        }
    };

    private void updateWidgets() {
        Log.e("VVV","ClipListenerService :- updateWidgets");
        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(new Intent("refresh"));
        Intent intent = new Intent(this,ClippingWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), ClippingWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        sendBroadcast(intent);
    }

    private void sendNotification(String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.psm_bar)
                .setContentTitle("New Clipping")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    public static Intent getSwapClippingIntent(Context context, Clipping clip){
        Intent intent = new Intent(context, ClipListenerService.class);
        intent.putExtra(ClipListenerService.EXTRA_ACTION_TYPE,ClipListenerService.ACTION_TYPE_CLICK);
        intent.putExtra(ClipListenerService.EXTRA_CLIPPING_ID,clip.getId());
        return intent;
    }
    public static Intent getUpdateWidgetsIntent(Context context){
        Intent intent = new Intent(context, ClipListenerService.class);
        intent.putExtra(ClipListenerService.EXTRA_ACTION_TYPE,ACTION_TYPE_UPDATE_WIDGETS);
        return intent;
    }
    public static Intent getAddNewClipIntent(Context context, String clip, long time){
        Intent intent = new Intent(context, ClipListenerService.class);
        intent.putExtra(ClipListenerService.EXTRA_ACTION_TYPE,ClipListenerService.ACTION_TYPE_ADD);
        intent.putExtra(ClipListenerService.EXTRA_CLIP,clip);
        intent.putExtra(ClipListenerService.EXTRA_CLIP_TIME,time);
        return intent;
    }
    public static void addNewClip(Context context, String clip, long time){
        context.startService(getAddNewClipIntent(context,clip,time));

    }
    public static void swapClipping(Context context, Clipping clipData) {
        context.startService(getSwapClippingIntent(context,clipData));
    }
    public static void updateWidgets(Context context) {

        context.startService(getUpdateWidgetsIntent(context));
    }
}
