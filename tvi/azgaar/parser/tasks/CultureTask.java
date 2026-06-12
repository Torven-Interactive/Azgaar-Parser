package tvi.azgaar.parser.tasks;

import com.google.gson.*;
import tvi.azgaar.parser.ExtendedTask;
import tvi.azgaar.parser.models.linguistic.Culture;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class CultureTask extends ExtendedTask {

    @Override
    public JsonElement execute(String inputPath) {
        System.out.println("🎭 [CultureTask] Commencing culture extraction layer pass...");
        try {
            // Read the master file structure
            try (FileReader reader = new FileReader(inputPath)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

                if (!root.has("pack")) {
                    System.err.println("🔴 [CultureTask Error] File lacks the required 'pack' branch!");
                    return new JsonArray();
                }
                JsonObject pack = root.getAsJsonObject("pack");

                if (!pack.has("cultures")) {
                    System.err.println("⚠️ [CultureTask Warning] No 'cultures' array found inside 'pack'. skipping.");
                    return new JsonArray();
                }

                // Isolate the cultures array node
                JsonArray culturesArray = pack.getAsJsonArray("cultures");

                System.out.println("🟢 [CultureTask Success] Extracted cultures");

                // FIX: Return the raw array directly back to TaskSystem instead of wrapping it again
                return culturesArray;
            }
        } catch (Exception e) {
            System.err.println("🔴 [CultureTask Failure] Critical exception during execution!");
            e.printStackTrace();
        }
        return new JsonArray();
    }

    @Override
    public Object load(String inputPath) {
        System.out.println("📥 [CultureTask Loader] Hydrating ethnolinguistic world parameters...");
        List<Culture> activeCultures = new ArrayList<>();

        try (FileReader reader = new FileReader(inputPath)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

            // Symmetrical: Grabs the single root "cultures" array key directly from the flat file
            if (root.has("cultures") && root.get("cultures").isJsonArray()) {
                com.google.gson.Gson gson = new com.google.gson.Gson();
                JsonArray culturesArray = root.getAsJsonArray("cultures");

                for (JsonElement elem : culturesArray) {
                    if (elem == null || elem.isJsonNull()) {
                        continue;
                    }

                    Culture cultureObj = gson.fromJson(elem, Culture.class);
                    if (cultureObj != null) {
                        activeCultures.add(cultureObj);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("🔴 [CultureTask Loader Error] Failed to read flat culture arrays.");
            e.printStackTrace();
        }
        return activeCultures;
    }
}
