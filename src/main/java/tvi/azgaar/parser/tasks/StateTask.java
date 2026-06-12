package tvi.azgaar.parser.tasks;

import com.google.gson.*;
import tvi.azgaar.parser.Task;

import java.io.FileReader;


public class StateTask implements Task {

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
                    System.err.println("⚠️  [StateTask Warning] No 'states' matrix array found inside 'pack'. Skipping execution.");
                    return new JsonArray();
                }

                // Extract the raw states array data segment
                JsonArray statesArray = pack.getAsJsonArray("states");

                // Wrap it securely into a clean, flat root states document layer for modding
                JsonObject statesOutput = new JsonObject();
                statesOutput.add("states", statesArray);

                System.out.println("🟢 [StateTask Success] Extracted political states registry");

                return statesOutput;
            }

        } catch (Exception e) {
            System.err.println("🔴 [StateTask Failure] Encountered an unhandled exception during extraction loop!");
            e.printStackTrace();
        }

        return new JsonArray();
    }
}
