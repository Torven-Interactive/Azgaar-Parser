package tvi.azgaar.parser.tasks;

import com.google.gson.*;
import tvi.azgaar.parser.Task;

import java.io.FileReader;
import java.util.ArrayList;


public class ZoneTask implements Task {

    @Override
    public JsonElement execute(String inputPath) {
        System.out.println("🚧 [ZoneTask] Commencing custom boundary and map zones extraction pass...");

        try {
            // Stream the JSON text structure cleanly
            try (FileReader reader = new FileReader(inputPath)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

                // Navigate path: root -> pack
                if (!root.has("pack")) {
                    System.err.println("🔴 [ZoneTask Error] Source file lacks the core 'pack' object node branch!");
                    return new JsonArray();
                }
                JsonObject pack = root.getAsJsonObject("pack");

                // Verify and navigate path: pack -> zones
                if (!pack.has("zones")) {
                    System.err.println("⚠️ [ZoneTask Warning] No 'zones' array data found inside the 'pack' node. Skipping execution.");
                    return new JsonArray();
                }
                JsonArray zonesArray = pack.getAsJsonArray("zones");

                // Wrap it securely into a clean, flat root structure
                JsonObject zoneOutput = new JsonObject();
                zoneOutput.add("zones", zonesArray);

                System.out.println("🟢 [ZoneTask Success] Extracted regional zones database array");

                return zoneOutput;
            }
        } catch (Exception e) {
            System.err.println("🔴 [ZoneTask Failure] Encountered an unhandled exception during extraction loop!");
            e.printStackTrace();
        }

        return new JsonArray();
    }
}
