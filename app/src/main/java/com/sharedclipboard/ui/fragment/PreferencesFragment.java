package com.sharedclipboard.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.content.LocalBroadcastManager;

import com.sharedclipboard.R;
import com.sharedclipboard.SharedClipperApp;
import com.sharedclipboard.service.ClipListenerService;
import com.sharedclipboard.storage.preferences.PreferenceUtils;
import com.sharedclipboard.ui.activity.LoginActivity;


public class PreferencesFragment extends PreferenceFragment {

    public PreferencesFragment(){

    }
    public static AlertDialog mSaveDiscardDialog = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from the preferences xml file
        addPreferencesFromResource(R.xml.preferences);
        findPreference("passcode").setTitle(PreferenceUtils.getString(getActivity(),"passcode","Unknown"));
        findPreference(PreferenceUtils.PREF_EMAIL).setTitle(PreferenceUtils.getString(getActivity(),PreferenceUtils.PREF_EMAIL,"sample@sample.com"));
       /* findPreference("help").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getActivity(), ScreenSlideActivity.class);
                intent.putExtra(ScreenSlideActivity.EXTRA_IS_SETTINGS_LAUNCH,true);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            }
        });*/
        findPreference("logout").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showSaveDiscardDialog();
                return true;
            }
        });
      /*  findPreference("about").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.parse("http://vm285051.wix.com/clipboard");
                intent.setData(uri);
                try {
                    startActivity(Intent.createChooser(intent,"Open project URL !"));
                }catch (Exception ex){
                }
                return true;
            }
        });*/
    }

    private void initSaveDiscardDialog() {
        Dialog.OnClickListener mOnDialogClickSaveDIscard = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case Dialog.BUTTON_POSITIVE: {
                        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent("logout"));
                        SharedClipperApp.getDb(getActivity()).resetDbValues();
                        PreferenceUtils.putString(getActivity(),PreferenceUtils.PREF_EMAIL,null);
                        PreferenceUtils.putString(getActivity(),PreferenceUtils.PREF_PASSCODE,null);
                        getActivity().stopService(new Intent(getActivity(), ClipListenerService.class));
                        getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
                        getActivity().finish();
                    }
                    break;

                    case Dialog.BUTTON_NEGATIVE: {
                    }
                    break;
                }
            }
        };
        if (mSaveDiscardDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Are you Sure ? ");
            builder.setMessage("This action will logout from your session, This will not delete your account");
            builder.setPositiveButton("Yes", mOnDialogClickSaveDIscard);
            builder.setNegativeButton("No", mOnDialogClickSaveDIscard);
            mSaveDiscardDialog = builder.create();
        }
    }
    private void showSaveDiscardDialog() {
        initSaveDiscardDialog();
        mSaveDiscardDialog.show();
    }
}
