package tvi.azgaar.parser.tasks;

import com.google.gson.*;
import tvi.azgaar.parser.ExtendedTask;
import tvi.azgaar.parser.models.geography.River;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;


public class RiversTask extends ExtendedTask {

    @Override
    public JsonElement execute(String inputPath) {
        System.out.println("🌊 [RiversTask] Commencing hydrological river system extraction pass...");

        try {
            try (FileReader reader = new FileReader(inputPath)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

                if (!root.has("pack")) {
                    System.err.println("🔴 [RiversTask Error] Source file lacks the core 'pack' object node branch!");
                    return new JsonArray();
                }
                JsonObject pack = root.getAsJsonObject("pack");

                if (!pack.has("rivers")) {
                    System.err.println("⚠️ [RiversTask Warning] No 'rivers' data array found inside 'pack'. Skipping.");
                    return new JsonArray();
                }
                JsonArray riversArray = pack.getAsJsonArray("rivers");
                System.out.println("🟢 [RiversTask Success] Extracted river flow networks");

                return riversArray;
            }
        } catch (Exception e) {
            System.err.println("🔴 [RiversTask Failure] Encountered an unhandled exception during extraction loop!");
            e.printStackTrace();
        }

        return new JsonArray();
    }

    @Override
    public Object load(String inputPath) {
        System.out.println("📥 [RiversTask Loader] Hydrating fresh-water flow networks...");
        List<River> activeRivers = new ArrayList<>();

        try (FileReader reader = new FileReader(inputPath)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

            // Targets your clean, unbloated variable array key name directly
            if (root.has("rivers")) {
                for (JsonElement elem : root.getAsJsonArray("rivers")) {
                    // Instantly hydrates your clean Java object list!
                    Gson gson = new Gson();
                    River riverObj = gson.fromJson(elem, River.class);
                    activeRivers.add(riverObj);
                }
            }
        } catch (Exception e) {
            System.err.println("🔴 [RiversTask Loader Error] Failed to deserialize river system profiles.");
            e.printStackTrace();
        }

        return activeRivers; // Hands the usable list straight back up to TaskSystem
    }
}

