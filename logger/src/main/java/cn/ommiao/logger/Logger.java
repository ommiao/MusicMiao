package cn.ommiao.logger;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.PrettyFormatStrategy;

public class Logger {
    public static void initLogger(){
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(true)// (Optional) Whether to show thread info or not. Default true
                .logStrategy(new LogCatStrategy ())// (Optional) Changes the log strategy to print out. Default LogCat
                .methodCount(0)// (Optional) How many method line to show. Default 2
                .methodOffset(7) // (Optional) Hides internal method calls up to offset. Default 5
                .build();
        com.orhanobut.logger.Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return BuildConfig.DEBUG;
            }
        });
    }

}
