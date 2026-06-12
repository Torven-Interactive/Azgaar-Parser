package tvi.azgaar.parser.tasks;

import com.google.gson.*;
import tvi.azgaar.parser.Task;

import java.io.FileReader;

public class MarkersTask implements Task {

    @Override
    public JsonElement execute(String inputPath) {
        System.out.println("📍 [MarkersTask] Commencing map point-of-interest markers extraction pass...");

        try {

            // Stream the JSON text structure cleanly
            try (FileReader reader = new FileReader(inputPath)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

                // Navigate path: root -> pack
                if (!root.has("pack")) {
                    System.err.println("🔴 [MarkersTask Error] Source file lacks the core 'pack' object node branch!");
                    return new JsonArray();
                }
                JsonObject pack = root.getAsJsonObject("pack");

                // Verify and navigate path: pack -> markers
                if (!pack.has("markers")) {
                    System.err.println("⚠️ [MarkersTask Warning] No 'markers' data array found inside the 'pack' node. Skipping.");
                    return new JsonArray();
                }
                JsonArray markersArray = pack.getAsJsonArray("markers");

                // Wrap it securely into a clean, flat root structure
                JsonObject markersOutput = new JsonObject();
                markersOutput.add("markers", markersArray);

                System.out.println("🟢 [MarkersTask Success] Extracted map markers registry array");

                return markersOutput;
            }
        } catch (Exception e) {
            System.err.println("🔴 [MarkersTask Failure] Encountered an unhandled exception during extraction loop!");
            e.printStackTrace();
        }

        return new JsonArray();
    }
}

