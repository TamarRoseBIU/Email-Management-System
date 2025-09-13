package com.example.myemailapp.data.database.converter;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class StringListConverter {
    private static final Gson gson = new Gson();

    @TypeConverter
    public static String fromStringList(List<String> stringList) {
        if (stringList == null) {
            return null;
        }
        return gson.toJson(stringList);
    }

    @TypeConverter
    public static List<String> toStringList(String stringListString) {
        if (stringListString == null) {
            return null;
        }
        Type listType = new TypeToken<List<String>>() {}.getType();
        return gson.fromJson(stringListString, listType);
    }
}