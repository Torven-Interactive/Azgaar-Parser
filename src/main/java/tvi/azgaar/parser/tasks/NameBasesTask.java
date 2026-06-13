package tvi.azgaar.parser.tasks;

import com.google.gson.*;
import tvi.azgaar.parser.ExtendedTask;
import tvi.azgaar.parser.models.linguistic.NameBase;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class NameBasesTask extends ExtendedTask {

    @Override
    public JsonElement execute(String inputPath) {
        System.out.println("🔤 [NameBasesTask] Commencing root-level linguistic name base extraction pass...");
        try {
            try (FileReader reader = new FileReader(inputPath)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

                if (!root.has("nameBases")) {
                    System.err.println("⚠️ [NameBasesTask Warning] No 'nameBases' array found at the root level.");
                    return new JsonArray();
                }
                JsonArray nameBasesArray = root.getAsJsonArray("nameBases");
                System.out.println("🟢 [NameBasesTask Success] Extracted root linguistic data profiles");

                return nameBasesArray;
            }
        } catch (Exception e) {
            System.err.println("🔴 [NameBasesTask Failure] Encountered an unhandled exception during extraction loop!");
            e.printStackTrace();
        }

        return new JsonArray();
    }

    @Override
    public Object load(String inputPath) {
        System.out.println("📥 [NameBasesTask Loader] Hydrating infinite text generator syllables...");
        List<NameBase> activeNames = new ArrayList<>();
        try (FileReader reader = new FileReader(inputPath)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            if (root.has("nameBases")) {
                com.google.gson.Gson gson = new com.google.gson.Gson();
                for (JsonElement elem : root.getAsJsonArray("nameBases")) {
                    activeNames.add(gson.fromJson(elem, NameBase.class));
                }
            }
        } catch (Exception e) {
            System.err.println("🔴 [NameBasesTask Loader Error] Failed to read name base arrays.");
            e.printStackTrace();
        }
        return activeNames;

    }
}

