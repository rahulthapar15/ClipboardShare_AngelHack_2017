package com.sharedclipboard;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.baoyz.actionsheet.ActionSheet;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.sharedclipboard.network.ClippingRefreshAsyncTask;
import com.sharedclipboard.service.ClipListenerService;
import com.sharedclipboard.service.RegistrationIntentService;
import com.sharedclipboard.storage.db.models.Clipping;
import com.sharedclipboard.storage.preferences.PreferenceUtils;
import com.sharedclipboard.ui.activity.BaseActivity;
import com.sharedclipboard.ui.activity.LoginActivity;
import com.sharedclipboard.ui.activity.SettingsActivity;
import com.sharedclipboard.ui.fragment.PreferencesFragment;

import static com.sharedclipboard.ui.fragment.PreferencesFragment.mSaveDiscardDialog;


public class MainActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener ,
        ActionSheet.ActionSheetListener{

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private FloatingActionButton floatingActionButton;
    PreferencesFragment preferencesFragment = new PreferencesFragment();

    public static GridView gridView;
    public static GridViewCustomAdapter gridViewCustomeAdapter;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private BroadcastReceiver mLogoutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };
    private boolean isReceiverRegistered;
    private boolean isLogoutReceiverRegistered;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private BroadcastReceiver mDataRefreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //gridViewCustomeAdapter.notifyDataSetChanged();
            gridViewCustomeAdapter = new GridViewCustomAdapter(MainActivity.this);
            // Set the Adapter to GridView
            gridView.setAdapter(gridViewCustomeAdapter);
            mSwipeRefreshLayout.setRefreshing(false);
        }
    };
    private boolean isDataRefreshRegistered;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        gridView=(GridView)findViewById(R.id.gridViewCustom);
        gridView.setEmptyView(findViewById(R.id.txtEmpty));
        // Create the Custom Adapter Object
        // Handling touch/click Event on GridView Item
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                if (gridViewCustomeAdapter != null) {
                    Clipping clip = gridViewCustomeAdapter.getClipping(position);
                    ClipListenerService.swapClipping(getBaseContext(), clip);
                }
            }
        });
        initGCM();

        floatingActionButton = (FloatingActionButton)findViewById(R.id.fab);
        floatingActionButton.setEnabled(true);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Toast.makeText(getApplicationContext(),"test",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);*/
               showActionSheet();
            }
        });

        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setIcon(R.drawable.sync_icon_burned);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setRefreshing(true);
        onRefresh();
        registerLogoutReceiver();
    }
    public void showActionSheet() {

        String webid = PreferenceUtils.getString(getApplicationContext(),"passcode","Unknown");
        String email_id = PreferenceUtils.getString(getApplicationContext(),PreferenceUtils.PREF_EMAIL,"sample@sample.com");

        ActionSheet.createBuilder(this, getSupportFragmentManager())
                .setCancelButtonTitle("LOGOUT")
                .setOtherButtonTitles("WEB ID : "+ webid,"EMAIL : " + email_id)
                .setCancelableOnTouchOutside(true).setListener(this).show();
    }

    @Override
    protected void onResume(){
        super.onResume();
        gridViewCustomeAdapter = new GridViewCustomAdapter(this);
        // Set the Adapter to GridView
        gridView.setAdapter(gridViewCustomeAdapter);
        registerReceiver();
        registerDataReceiver();
    }

    @Override
    protected void onPause(){
        super.onPause();
        unRegisterReceiver();
        unRegisterDataReceiver();
    }
    private void initGCM(){
        Log.e("VVV", "initGCM");
        boolean isRegistrationNeeded = PreferenceUtils.getBoolean(getBaseContext(),PreferenceUtils.PREF_SENT_TOKEN_TO_SERVER,false);
        if(!isRegistrationNeeded) {
            mRegistrationBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    boolean sentToken = PreferenceUtils.getBoolean(getBaseContext(), PreferenceUtils.PREF_SENT_TOKEN_TO_SERVER, false);
                    if (sentToken) {
                        showToast("GCM registration done");
                    } else {
                        showToast("GCM registration ERROR");
                    }
                }
            };
            // Registering BroadcastReceiver
            registerReceiver();

            if (checkPlayServices()) {
                // Start IntentService to register this application with GCM.
                Intent intent = new Intent(this, RegistrationIntentService.class);
                startService(intent);
            }
        }
    }

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {
        //onResume();
        ClippingRefreshAsyncTask refreshAsyncTask = new ClippingRefreshAsyncTask(this);
        refreshAsyncTask.execute("");
        final Activity a = this;
        Log.d("Refresh", "refresh");
    }

    public void refreshGridView() {
        gridViewCustomeAdapter = new GridViewCustomAdapter(this);
        // Set the Adapter to GridView
        gridView.setAdapter(gridViewCustomeAdapter);
    }


    private void registerLogoutReceiver(){
        if(!isLogoutReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mLogoutReceiver, new IntentFilter("logout"));
            isLogoutReceiverRegistered = true;
        }
    }
    private void unRegisterLogoutReceiver(){
        if(isLogoutReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mLogoutReceiver);
            isLogoutReceiverRegistered = false;
        }
    }

    private void registerReceiver(){
        if(!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(PreferenceUtils.PREF_REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }
    private void unRegisterReceiver(){
        if(isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
            isReceiverRegistered = false;
        }
    }
    private void registerDataReceiver(){
        if(!isDataRefreshRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mDataRefreshReceiver, new IntentFilter("refresh"));
            isDataRefreshRegistered = true;
        }
    }
    private void unRegisterDataReceiver(){
        if(isDataRefreshRegistered) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mDataRefreshReceiver);
            isDataRefreshRegistered = false;
        }
    }
    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i("VVV", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        unRegisterLogoutReceiver();
    }

    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
       if (isCancel) {
           //Toast.makeText(getApplicationContext(), "Logout is pressed ", Toast.LENGTH_SHORT).show();
           showSaveDiscardDialog();
       }
    }

    private void showSaveDiscardDialog() {
        initSaveDiscardDialog();
        mSaveDiscardDialog.show();
    }

    private void initSaveDiscardDialog() {
        Dialog.OnClickListener mOnDialogClickSaveDIscard = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case Dialog.BUTTON_POSITIVE: {
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent("logout"));
                        SharedClipperApp.getDb(getApplicationContext()).resetDbValues();
                        PreferenceUtils.putString(getApplicationContext(),PreferenceUtils.PREF_EMAIL,null);
                        PreferenceUtils.putString(getApplicationContext(),PreferenceUtils.PREF_PASSCODE,null);
                        MainActivity.this.stopService(new Intent(MainActivity.this, ClipListenerService.class));
                        MainActivity.this.startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }
                    break;

                    case Dialog.BUTTON_NEGATIVE: {
                    }
                    break;
                }
            }
        };
        if (mSaveDiscardDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Are you Sure ? ");
            builder.setMessage("This action will logout from your session, This will not delete your account");
            builder.setPositiveButton("Yes", mOnDialogClickSaveDIscard);
            builder.setNegativeButton("No", mOnDialogClickSaveDIscard);
            mSaveDiscardDialog = builder.create();
        }
    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        //Toast.makeText(getApplicationContext(), "click item index "+ index,Toast.LENGTH_SHORT).show();
        switch (index){
            case 0 :
                    break;
            case 1 :
                    break;
            default:
                break;
        }
    }
}