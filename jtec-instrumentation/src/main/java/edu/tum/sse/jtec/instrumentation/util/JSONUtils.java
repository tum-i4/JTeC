package edu.tum.sse.jtec.instrumentation.util;

import com.google.gson.Gson;

public final class JSONUtils {
    public static String toJson(final Object o) {
        return new Gson().toJson(o);
    }
}
