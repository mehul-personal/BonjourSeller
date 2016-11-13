package com.analytics.bonjourseller;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by techiestown on 17/12/15.
 */
public class ApplicationData extends Application{
    static String serviceURL="http://192.95.6.213/";
    private RequestQueue mRequestQueue;
    private static ApplicationData mInstance;
    public static final String TAG = ApplicationData.class
            .getSimpleName();
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        mInstance = this;
    }
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }
    public static synchronized ApplicationData getInstance() {
        return mInstance;
    }
}
