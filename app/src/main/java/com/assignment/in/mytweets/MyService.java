package com.assignment.in.mytweets;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.IBinder;


import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.tweetui.SearchTimeline;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;

import io.fabric.sdk.android.Fabric;

public class MyService extends Service {
    private static final String TAG = "TWITTER_PRUEBA";
    private static final String TWITTER_KEY = "sdt8l46kBeILU78cPwLx9k2KA";
    private static final String TWITTER_SECRET = "tYI1DLjkJ1QBc3GtNpjyC6zcdSKKfebnmylCgZUFlVtmNNVVbt";
    public static SearchTimeline searchTimeline;
    private ServiceCallbacks serviceCallbacks;
    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        MyService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MyService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("TWITTER_PRUEBA", "null");
        return mBinder;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Service Created", Toast.LENGTH_LONG).show();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        searchTimeline = new SearchTimeline.Builder().query("#NarendraModi").build();
    }

    @Override
    public void onStart(Intent intent, int startid) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();
    }


    public void getInstance() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            if (searchTimeline == null) {
                searchTimeline = new SearchTimeline.Builder().query("#NarendraModi").build();

                Log.d("TWITTER_PRUEBA", "null");
            }
        }
        if (serviceCallbacks != null) {
            serviceCallbacks.doSomething(searchTimeline);
            Log.d("TWITTER_PRUEBA", "set");
        }
    }

    public void setCallbacks(ServiceCallbacks callbacks) {
        Log.d("TWITTER_PRUEBA", "before set");
        serviceCallbacks = callbacks;
        getInstance();
    }
}