package cn.ommiao.musicmiao.core;

import android.app.Application;
import android.content.Context;

import cn.ommiao.logger.Logger;
import cn.ommiao.network.Client;

public class App extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        Client.initNetwork();
        Logger.initLogger();
    }

    public static Context getContext(){
        return mContext;
    }
}
