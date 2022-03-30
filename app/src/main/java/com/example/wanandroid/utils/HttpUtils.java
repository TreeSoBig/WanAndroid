package com.example.wanandroid.utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpUtils {
    public static void sendOKHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
