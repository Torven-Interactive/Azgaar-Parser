package tvi.azgaar.parser.tasks;

import com.google.gson.*;
import tvi.azgaar.parser.Task;

import java.io.FileReader;

public class MarkersTask implements Task {

    @Override
    public JsonElement execute(String inputPath) {
        System.out.println("📍 [MarkersTask] Extracting flat markers array...");
        try {
            try (FileReader reader = new FileReader(inputPath)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

                if (!root.has("pack")) {
                    System.err.println("🔴 [MarkersTask Error] File lacks the required 'pack' branch!");
                    return new JsonArray();
                }
                JsonObject pack = root.getAsJsonObject("pack");

                // Fixed typo: Pulls the correct raw "markers" array string out of Azgaar's pack node
                if (!pack.has("markers")) {
                    System.err.println("⚠️ [MarkersTask Warning] No 'markers' array found inside 'pack'. skipping.");
                    return new JsonArray();
                }

                JsonArray markersArray = pack.getAsJsonArray("markers");
                System.out.println("🟢 [MarkersTask Success] Extracted raw markers matrix array");

                // FIX: Return the raw array directly back up to TaskSystem without wrapping or misspelling it
                return markersArray;
            }
        } catch (Exception e) {
            System.err.println("🔴 [MarkersTask Failure] Critical exception during execution!");
            e.printStackTrace();
        }
        return new JsonArray();
    }
}

