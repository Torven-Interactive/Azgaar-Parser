package tvi.azgaar.parser.tasks;

import com.google.gson.*;
import tvi.azgaar.parser.Task;

import java.io.FileReader;

public class FeaturesTask implements Task {

    @Override
    public JsonElement execute(String inputPath) {
        System.out.println("🗻 [FeaturesTask] Commencing geographic landmass/continent macro extraction pass...");
        try {
            try (FileReader reader = new FileReader(inputPath)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
                if (!root.has("pack")) return new JsonArray();

                JsonObject pack = root.getAsJsonObject("pack");
                if (!pack.has("features")) {
                    System.err.println("⚠️ [FeaturesTask Warning] No 'features' array found inside 'pack'.");
                    return new JsonArray();
                }

                JsonArray featuresArray = pack.getAsJsonArray("features");
                System.out.println("🟢 [FeaturesTask Success] Extracted macro island/continent layers");

                // FIX: Return the raw array directly back to TaskSystem instead of wrapping it again
                return featuresArray;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JsonArray();
    }
}

