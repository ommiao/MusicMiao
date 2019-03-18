package cn.ommiao.musicmiao.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Modifier;
import java.util.HashMap;

public class GsonUtil {

    public static String toJson(Object object){
        Gson gson = newGsonExcludeTransient();
        return gson.toJson(object);
    }

    @Nullable
    public static <T> T fromJson(String json, Class<T> classOfT){
        Gson gson = newGsonExcludeTransient();
        T t;
        try {
            t = gson.fromJson(json,classOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            t = null;
        }
        return t;
    }

    public @NonNull
    HashMap<String,String> toHashMap(){
        Gson gson = newGsonExcludeTransient();
        TypeToken<HashMap<String,String>> token = new TypeToken<HashMap<String,String>>(){

        };
        HashMap<String,String> map = gson.fromJson(gson.toJson(this),token.getType());
        if (map == null){
            map = new HashMap<>();
        }
        return map;
    }

    public static Gson newGsonExcludeTransient(){
        return new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT,Modifier.STATIC).create();
    }

}
