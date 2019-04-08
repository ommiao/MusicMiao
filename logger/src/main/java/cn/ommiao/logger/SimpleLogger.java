package cn.ommiao.logger;

import com.orhanobut.logger.AndroidLogAdapter;

public class SimpleLogger {
    public static void initLogger(){
        com.orhanobut.logger.Logger.addLogAdapter(new AndroidLogAdapter() {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return BuildConfig.DEBUG;
            }
        });
    }
}
