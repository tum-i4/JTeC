package edu.tum.sse.jtec.util;

import com.google.gson.Gson;

import java.util.Map;

public final class JSONUtils {
    public static String toJson(Object o) {
        return new Gson().toJson(o);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return new Gson().fromJson(json, clazz);
    }

    public static Map fromJson(String json) {
        return new Gson().fromJson(json, Map.class);
    }
}
