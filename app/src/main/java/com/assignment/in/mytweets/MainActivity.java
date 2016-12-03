package com.assignment.in.mytweets;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.google.gson.Gson;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.tweetui.SearchTimeline;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity implements ServiceCallbacks{
    private static final String TWITTER_KEY = "sdt8l46kBeILU78cPwLx9k2KA";
    private static final String TWITTER_SECRET = "tYI1DLjkJ1QBc3GtNpjyC6zcdSKKfebnmylCgZUFlVtmNNVVbt";
    private static final String TAG = "TWITTER_PRUEBA";
    private ListView listView;
    SharedPreferences prefs = null;
    public static MyService mService;
    SearchTimeline searchTimeline;
    public boolean mBound=false;
    private ServiceCallbacks serviceCallbacks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);
        bindUI();

        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        Gson gson = new Gson();
        String json = appSharedPrefs.getString("MyObject", "");
        SearchTimeline obj = gson.fromJson(json, SearchTimeline.class);
        if(obj!=null){
        TweetTimelineListAdapter ta = new TweetTimelineListAdapter.Builder(this).setTimeline(obj).build();
        listView.setAdapter(ta);}
        else if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            Log.d("TWITTER_PRUEBA","here");
            TweetTimelineListAdapter adapter = retrieveTimeLineByHashtag(this, "#NarendraModi");
            listView.setAdapter(adapter);
        }
        if (!isMyServiceRunning(MyService.class)) {
            startService(new Intent(this, MyService.class));
            Intent intent = new Intent(this, MyService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from service
        if (mBound) {
            mService.setCallbacks(null); // unregister
            unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService

        Log.d("TWITTER_PRUEBA","paste");
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
//
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            mService = binder.getService();
            mService.setCallbacks(MainActivity.this);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    private void bindUI() {
        listView = (ListView) findViewById(R.id.prueba);
    }

    public TweetTimelineListAdapter retrieveTimeLineByHashtag(Context context, String hashtag) {
        Log.d(TAG, "Loading tweets with hashtag " + hashtag);
        SearchTimeline searchTimeline = new SearchTimeline.Builder().query(hashtag).build();
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String s = gson.toJson(searchTimeline);
        prefsEditor.putString("MyObject", s);
        prefsEditor.commit();
        TweetTimelineListAdapter ta = new TweetTimelineListAdapter.Builder(context).setTimeline(searchTimeline).build();
        return ta;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void doSomething(SearchTimeline searchTimeline) {
        Log.d("TWITTER_PRUEBA","dosomething");
        TweetTimelineListAdapter ta = new TweetTimelineListAdapter.Builder(this).setTimeline(searchTimeline).build();
        listView.setAdapter(ta);
    }
}
