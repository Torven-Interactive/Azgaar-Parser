package tvi.azgaar.parser.tasks;

import com.google.gson.*;
import tvi.azgaar.parser.Task;

import java.io.FileReader;


public class ProvincesTask implements Task {

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

                JsonObject provincesOutput = new JsonObject();
                provincesOutput.add("provinces", provincesArray);

                System.out.println("🟢 [ProvincesTask Success] Extracted sub-state provincial systems");

                return provincesOutput;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new JsonArray();
    }
}

