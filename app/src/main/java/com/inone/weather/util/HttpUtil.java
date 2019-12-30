package com.inone.weather.util;

import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;
/*
*和服务器交互
 */
public class HttpUtil {
    private static final String TAG = "HttpUtil";
    public static  void sendOkHttpRequest(String address,okhttp3.Callback callback){//传入地址，注册回调来处理服务器响应
        //创建OkHttpClient实例
        OkHttpClient client = new OkHttpClient();
        //创建Request，发起一条http请求
        Request request = new Request.Builder().url(address).build();
        //newCall()方法创建一个call对象，
        client.newCall(request).enqueue(callback);

    }

//    public static void sendOkHttpRequest(String address,okhttp3.Callback callback){  //发起Http请求调用sendOkHttpRequest方法（传入请求地址，注册一个回调来处理服务器响应）
//        OkHttpClient client = new OkHttpClient();  //OkHttpClient实例
//        Request request = new Request.Builder().url(address).build(); //Request请求创建
//        client.newCall(request).enqueue(callback); //同步请求
//    }
}
