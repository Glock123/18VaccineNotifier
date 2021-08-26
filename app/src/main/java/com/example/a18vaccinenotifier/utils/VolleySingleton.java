package com.example.a18vaccinenotifier.utils;


import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {
    private static VolleySingleton mySingleTon;
    private RequestQueue requestQueue;
    private static Context mctx;
    private VolleySingleton(Context context){
        mctx=context;
        this.requestQueue=getRequestQueue();

    }
    public RequestQueue getRequestQueue(){
        if (requestQueue==null) {
            requestQueue= Volley.newRequestQueue(mctx.getApplicationContext());
        }
        return requestQueue;
    }
    public static synchronized VolleySingleton getInstance(Context context){
        if (mySingleTon==null) {
            mySingleTon=new VolleySingleton(context);
        }
        return mySingleTon;
    }
    public<T> void addToRequestQue(Request<T> request) {
        requestQueue.add(request);

    }

}
