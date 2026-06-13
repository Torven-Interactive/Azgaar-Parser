package tvi.azgaar.parser.tasks;

import com.google.gson.*;
import tvi.azgaar.parser.ExtendedTask;
import tvi.azgaar.parser.models.geopol.State;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;


public class StateTask extends ExtendedTask {

    @Override
    public JsonElement execute(String inputPath) {
        System.out.println("👑 [StateTask] Commencing sovereign state extraction layer pass...");
        try {
            // Stream the JSON text structure cleanly
            try (FileReader reader = new FileReader(inputPath)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

                if (!root.has("pack")) {
                    System.err.println("🔴 [StateTask Error] Source file lacks the core 'pack' object node branch!");
                    return new JsonArray();
                }
                JsonObject pack = root.getAsJsonObject("pack");

                if (!pack.has("states")) {
                    System.err.println("⚠️ [StateTask Warning] No 'states' matrix array found inside 'pack'. Skipping execution.");
                    return new JsonArray();
                }

                // Extract the raw states array data segment
                JsonArray statesArray = pack.getAsJsonArray("states");

                System.out.println("🟢 [StateTask Success] Extracted political states registry");

                // FIX: Return the raw array directly back to TaskSystem instead of wrapping it again
                return statesArray;
            }
        } catch (Exception e) {
            System.err.println("🔴 [StateTask Failure] Encountered an unhandled exception during extraction loop!");
            e.printStackTrace();
        }
        return new JsonArray();
    }

    @Override
    public Object load(String inputPath) {
        System.out.println("📥 [StateTask Loader] Hydrating sovereign political factions...");
        List<State> activeStates = new ArrayList<>();

        try (FileReader reader = new FileReader(inputPath)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

            // Symmetrical: Grabs the single root "states" array key directly from the flat file
            if (root.has("states") && root.get("states").isJsonArray()) {
                JsonArray statesArray = root.getAsJsonArray("states");

                for (JsonElement elem : statesArray) {
                    if (elem == null || elem.isJsonNull()) {
                        continue;
                    }

                    JsonObject stateJson = elem.getAsJsonObject();
                    State stateObj = new State();

                    // Directly pull values to cleanly map your object properties
                    if (stateJson.has("i")) {
                        stateObj.setId(stateJson.get("i").getAsInt());
                    }
                    if (stateJson.has("name")) {
                        stateObj.setName(stateJson.get("name").getAsString());
                    }
                    if (stateJson.has("color")) {
                        stateObj.setColor(stateJson.get("color").getAsString());
                    }

                    activeStates.add(stateObj);
                }
            }
        } catch (Exception e) {
            System.err.println("🔴 [StateTask Loader Error] Failed to read flat state arrays.");
            e.printStackTrace();
        }
        return activeStates;
    }
}
