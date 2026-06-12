package tvi.azgaar.parser.tasks;

import com.google.gson.*;
import tvi.azgaar.parser.ExtendedTask;
import tvi.azgaar.parser.models.geography.Biome;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class BiomeTask extends ExtendedTask {

    @Override
    public JsonElement execute(String inputPath) {
        System.out.println("🌿 [BiomeTask] Commencing precise environmental biome extraction pass from root...");

        try {
            // Stream the JSON text structure cleanly
            try (FileReader reader = new FileReader(inputPath)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

                // Initialize a clean container for our biome output records
                JsonObject biomesOutput = new JsonObject();

                // Safely extract biomesData as a JsonObject directly from root
                if (root.has("biomesData")) {
                    JsonObject biomesDataObject = root.getAsJsonObject("biomesData");
                    biomesOutput.add("biomesData", biomesDataObject);
                } else {
                    System.err.println("⚠️ [BiomeTask Warning] 'biomesData' object not found at the root level.");
                }

                // Safely extract biomesMatrix as a JsonArray directly from root
                if (root.has("biomesMatrix")) {
                    JsonArray biomesMatrixArray = root.getAsJsonArray("biomesMatrix");
                    biomesOutput.add("biomesMatrix", biomesMatrixArray);
                } else {
                    System.err.println("⚠️ [BiomeTask Warning] 'biomesMatrix' array not found at the root level.");
                }

                System.out.println("🟢 [BiomeTask Success] Extracted climate definitions object and matrix array");

                return biomesOutput;
            }
        } catch (Exception e) {
            System.err.println("🔴 [BiomeTask Failure] Encountered an unhandled exception during extraction loop!");
            e.printStackTrace();
        }

        return new JsonArray();
    }

    @Override
    public Object load(String inputPath) {
        System.out.println("📥 [BiomesTask Loader] Hydrating in-memory climate properties...");
        List<Biome> activeBiomes = new ArrayList<>();

        try (FileReader reader = new FileReader(inputPath)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

            // Your clean task targets the unbloated variable array directly
            if (root.has("biomes")) {
                for (com.google.gson.JsonElement elem : root.getAsJsonArray("biomes")) {
                    // Instantly hydrates your clean Java object list!
                    Gson gson = new Gson();
                    Biome biomeObj = gson.fromJson(elem, Biome.class);
                    activeBiomes.add(biomeObj);
                }
            }
        } catch (Exception e) {
            System.err.println("🔴 [BiomesTask Loader Error] Failed to deserialize climate records.");
            e.printStackTrace();
        }

        return activeBiomes; // Returns the usable list straight up to TaskSystem
    }
}
