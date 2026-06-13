package tvi.azgaar.parser.tasks;

import com.google.gson.*;
import tvi.azgaar.parser.ExtendedTask;
import tvi.azgaar.parser.models.geopol.MilitaryUnit;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class MilitaryTask extends ExtendedTask {

    @Override
    public JsonElement execute(String inputPath) {
        System.out.println("🎖️ [MilitaryTask] Commencing global military choices and forces extraction pass...");

        try {
            // Stream the JSON text structure cleanly
            try (FileReader reader = new FileReader(inputPath)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

                // Navigate path: root -> settings -> options -> military
                if (!root.has("settings")) {
                    System.err.println("🔴 [MilitaryTask Error] Source file lacks the core 'settings' branch!");
                    return new JsonArray();
                }
                JsonObject settings = root.getAsJsonObject("settings");

                if (!settings.has("options")) {
                    System.err.println("🔴 [MilitaryTask Error] Core 'settings' node lacks the 'options' branch!");
                    return new JsonArray();
                }
                JsonObject options = settings.getAsJsonObject("options");

                if (!options.has("military")) {
                    System.err.println("⚠️ [MilitaryTask Warning] No 'military' array configuration found inside options. Skipping.");
                    return new JsonArray();
                }

                JsonArray militaryArray = options.getAsJsonArray("military");
                System.out.println("🟢 [MilitaryTask Success] Extracted army configurations registry");

                return militaryArray;
            }
        } catch (Exception e) {
            System.err.println("🔴 [MilitaryTask Failure] Encountered an unhandled exception during extraction loop!");
            e.printStackTrace();
        }

        return new JsonArray();
    }

    @Override
    public Object load(String inputPath) {
        System.out.println("📥 [MilitaryTask Loader] Hydrating standing armed regiments...");
        List<MilitaryUnit> activeMilitary = new ArrayList<>();
        try (FileReader reader = new FileReader(inputPath)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            if (root.has("military")) {
                com.google.gson.Gson gson = new com.google.gson.Gson();
                for (JsonElement elem : root.getAsJsonArray("military")) {
                    activeMilitary.add(gson.fromJson(elem, MilitaryUnit.class));
                }
            }
        } catch (Exception e) {
            System.err.println("🔴 [MilitaryTask Loader Error] Failed to read military arrays.");
            e.printStackTrace();
        }
        return activeMilitary;
    }
}
