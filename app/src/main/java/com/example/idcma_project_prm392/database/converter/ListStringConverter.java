// database/converter/ListStringConverter.java
package com.example.idcma_project_prm392.database.converter;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class ListStringConverter {
    private final Gson gson = new Gson();
    private final Type TYPE = new TypeToken<List<String>>(){}.getType();

    @TypeConverter
    public String fromList(List<String> list) { // Đã xóa 'static'
        return list == null ? "[]" : gson.toJson(list, TYPE);
    }

    @TypeConverter
    public List<String> toList(String json) { // Đã xóa 'static'
        if (json == null || json.trim().isEmpty()) return Collections.emptyList();
        return gson.fromJson(json, TYPE);
    }
}
