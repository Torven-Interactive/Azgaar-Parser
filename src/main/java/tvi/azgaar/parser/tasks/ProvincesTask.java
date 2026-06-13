package tvi.azgaar.parser.tasks;

import com.google.gson.*;
import tvi.azgaar.parser.ExtendedTask;
import tvi.azgaar.parser.models.geopol.Province;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;


public class ProvincesTask extends ExtendedTask {

    @Override
    public JsonElement execute(String inputPath) {
        System.out.println("🗺️ [ProvincesTask] Commencing state subdivision and provincial boundary extraction pass...");
        try {

            try (FileReader reader = new FileReader(inputPath)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
                if (!root.has("pack")) return new JsonArray();
                JsonObject pack = root.getAsJsonObject("pack");

                if (!pack.has("provinces")) {
                    System.err.println("⚠️ [ProvincesTask Warning] No 'provinces' array found inside 'pack'.");
                    return new JsonArray();
                }
                JsonArray provincesArray = pack.getAsJsonArray("provinces");
                System.out.println("🟢 [ProvincesTask Success] Extracted sub-state provincial systems");

                return provincesArray;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new JsonArray();
    }

    @Override
    public Object load(String inputPath) {
        System.out.println("📥 [ProvincesTask Loader] Hydrating internal provincial sub-divisions...");
        List<Province> activeProvinces = new ArrayList<>();
        try (FileReader reader = new FileReader(inputPath)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            if (root.has("provinces")) {
                com.google.gson.Gson gson = new com.google.gson.Gson();
                for (JsonElement elem : root.getAsJsonArray("provinces")) {
                    activeProvinces.add(gson.fromJson(elem, Province.class));
                }
            }
        } catch (Exception e) {
            System.err.println("🔴 [ProvincesTask Loader Error] Failed to read province arrays.");
            e.printStackTrace();
        }
        return activeProvinces;
    }
}

