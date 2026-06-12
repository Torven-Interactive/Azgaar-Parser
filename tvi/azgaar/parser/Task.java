package tvi.azgaar.parser;

import com.google.gson.JsonElement;

public interface Task {
    public JsonElement execute(String inputPath);
}
