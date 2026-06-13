package tvi.azgaar.parser.tasks;

import com.google.gson.*;
import tvi.azgaar.parser.ExtendedTask;
import tvi.azgaar.parser.models.linguistic.Religion;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ReligionTask extends ExtendedTask {

    @Override
    public JsonElement execute(String inputPath) {
        System.out.println("⛪ [ReligionTask] Commencing religion extraction layer pass...");
        try {
            // Read the master file structure
            try (FileReader reader = new FileReader(inputPath)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
                if (!root.has("pack")) {
                    System.err.println("🔴 [ReligionTask Error] File lacks the required 'pack' branch!");
                    return new JsonArray();
                }

                JsonObject pack = root.getAsJsonObject("pack");
                if (!pack.has("religions")) {
                    System.err.println("⚠️ [ReligionTask Warning] No 'religions' array found inside 'pack'. skipping.");
                    return new JsonArray();
                }

                // Isolate the religions array node
                JsonArray religionsArray = pack.getAsJsonArray("religions");
                System.out.println("🟢 [ReligionTask Success] Extracted religions");

                // FIX: Return the raw array directly back to TaskSystem instead of wrapping it again
                return religionsArray;
            }
        } catch (Exception e) {
            System.err.println("🔴 [ReligionTask Failure] Critical exception during execution!");
            e.printStackTrace();
        }
        return new JsonArray();
    }

    @Override
    public Object load(String inputPath) {
        System.out.println("📥 [ReligionTask Loader] Hydrating clerical belief frameworks...");
        List<Religion> activeReligions = new ArrayList<>();
        try (FileReader reader = new FileReader(inputPath)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            if (root.has("religions")) {
                com.google.gson.Gson gson = new com.google.gson.Gson();
                for (JsonElement elem : root.getAsJsonArray("religions")) {
                    activeReligions.add(gson.fromJson(elem, Religion.class));
                }
            }
        } catch (Exception e) {
            System.err.println("🔴 [ReligionTask Loader Error] Failed to read religion arrays.");
            e.printStackTrace();
        }
        return activeReligions;
    }
}
