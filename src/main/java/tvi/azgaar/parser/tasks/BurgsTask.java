package tvi.azgaar.parser.tasks;

import com.google.gson.*;
import tvi.azgaar.parser.ExtendedTask;
import tvi.azgaar.parser.models.geopol.Burg;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class BurgsTask extends ExtendedTask {

    @Override
    public JsonElement execute(String inputPath) {
        System.out.println("🏙️ [BurgsTask] Commencing settlement and urban center extraction pass...");

        try {
            try (FileReader reader = new FileReader(inputPath)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

                if (!root.has("pack")) {
                    System.err.println("🔴 [BurgsTask Error] Source file lacks the core 'pack' object node branch!");
                    return new JsonArray();
                }
                JsonObject pack = root.getAsJsonObject("pack");

                if (!pack.has("burgs")) {
                    System.err.println("⚠️ [BurgsTask Warning] No 'burgs' data array found inside 'pack'. Skipping.");
                    return new JsonArray();
                }
                JsonArray burgsArray = pack.getAsJsonArray("burgs");

                JsonObject burgsOutput = new JsonObject();
                burgsOutput.add("burgs", burgsArray);

                System.out.println("🟢 [BurgsTask Success] Extracted settlement registry array");

                return burgsOutput;
            }
        } catch (Exception e) {
            System.err.println("🔴 [BurgsTask Failure] Encountered an unhandled exception during extraction loop!");
            e.printStackTrace();
        }

        return new JsonArray();
    }

    @Override
    public Object load(String inputPath) {
        System.out.println("📥 [BurgsTask Loader] Hydrating city settlements and populations...");
        List<Burg> activeBurgs = new ArrayList<>();
        try (FileReader reader = new FileReader(inputPath)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            if (root.has("burgs")) {
                com.google.gson.Gson gson = new com.google.gson.Gson();
                for (JsonElement elem : root.getAsJsonArray("burgs")) {
                    activeBurgs.add(gson.fromJson(elem, Burg.class));
                }
            }
        } catch (Exception e) {
            System.err.println("🔴 [BurgsTask Loader Error] Failed to read burg arrays.");
            e.printStackTrace();
        }
        return activeBurgs;
    }
}

