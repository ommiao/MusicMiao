package cn.ommiao.musicmiao.core;

import android.app.Application;
import android.content.Context;

import cn.ommiao.logger.SimpleLogger;
import cn.ommiao.network.Client;

public class App extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        initNetwork();
        initLogger();
    }

    private void initNetwork(){
        Client.initNetwork();
    }

    private void initLogger(){
        SimpleLogger.initLogger();
    }

    public static Context getContext(){
        return mContext;
    }
}
