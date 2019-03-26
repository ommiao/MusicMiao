package cn.ommiao.network;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;

public class Client {

    private static final long CONNECT_TIMEOUT = 120L;
    private static final long READ_TIMEOUT = 120L;
    private static final long WRITE_TIMEOUT = 120L;

    protected static OkHttpClient CLIENT;

    public static void initNetwork(){
        CLIENT = new OkHttpClient
                .Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .build();
    }


}
