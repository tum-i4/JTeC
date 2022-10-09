package edu.tum.sse.jtec.util;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public final class JSONUtils {
    public static String toJson(Object o) {
        return new Gson().toJson(o);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return new Gson().fromJson(json, clazz);
    }

    public static <T> T fromJson(Path path, Class<T> clazz) throws IOException {
        Reader reader = Files.newBufferedReader(path);
        T object = new Gson().fromJson(reader, clazz);
        reader.close();
        return object;
    }

    public static <T> List<T> fromJson(Path path, Type listType) throws IOException {
        Reader reader = Files.newBufferedReader(path);
        List<T> list = new Gson().fromJson(reader, listType);
        reader.close();
        return list;
    }

    public static Map fromJson(String json) {
        return new Gson().fromJson(json, Map.class);
    }
}
