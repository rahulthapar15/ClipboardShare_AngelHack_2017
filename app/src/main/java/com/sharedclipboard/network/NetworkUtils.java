package com.sharedclipboard.network;

import android.net.Uri;

public class NetworkUtils {

    public static final String SERVER_URL = "https://teak-passage-132116.appspot.com/";
    public static final String API_REGISTRATION = "register.do";
    public static final String API_POST = "post.do";
    public static final Uri URL_POST = Uri.parse(SERVER_URL + API_POST);
    public static final Uri URL_REGISTRATION = Uri.parse(SERVER_URL + API_REGISTRATION);
}
