package com.ontro.utils;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;

/**
 * Created by IDEOMIND02 on 27-06-2017.
 */

public class OntroApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig("N3ROo45jRtWWIwOmmg0mVEVhI", "WoCtqGSqriW7xjHhy23FrvyEi1aBNIUdd33sBosom0CjX6FFhg"))
                .debug(true)
                .build();
        Twitter.initialize(config);
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
