package cn.ommiao.logger;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.orhanobut.logger.LogStrategy;

public class LogCatStrategy implements LogStrategy {

    @Override
    public void log(int priority, @Nullable String tag, @NonNull String message) {
        Log.println(priority, randomKey() + tag, message);
    }

    private String last;

    private String randomKey() {
        String key = "+";
        if (key.equals(last)) {
            key = "-";
        }
        last = key;
        return key;
    }
}
