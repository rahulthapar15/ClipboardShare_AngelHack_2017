package com.sharedclipboard.storage.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


/**
 * Helper class to wrap the operations on shared preferences.
 */
public class PreferenceUtils {
    public static final String TAG = "PrefUtils";
    public static final String PREF_REGISTRATION_COMPLETE = "reg_complete";
    public static final String PREF_SENT_TOKEN_TO_SERVER = "reg_sent_token";
    public static final String PREF_PASSCODE = "passcode";
    public static final String PREF_EMAIL = "email";
    public static final String PREF_REGID = "reg_id";


    /**
     * put a string in preferences
     * @param context
     * @param key
     * @param value
     */
    public static void putString(Context context, String key, String value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(key, value);
        edit.apply();
    }

    /**
     * get a string from prefernce
     * @param context
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getString(Context context, String key, String defaultValue) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, defaultValue);
    }

    /**
     * put boolean into shared preferences
     * @param context
     * @param key
     * @param defaultValue
     * @return
     */
    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(key, defaultValue);
    }

    /**
     * get boolean from shared preferences
     * @param context
     * @param key
     * @param value
     */
    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(key, value);
        edit.commit();
    }
}
