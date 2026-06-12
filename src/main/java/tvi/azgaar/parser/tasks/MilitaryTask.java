package tvi.azgaar.parser.tasks;

import com.google.gson.*;
import tvi.azgaar.parser.Task;

import java.io.FileReader;

public class MilitaryTask implements Task {

    @Override
    public JsonElement execute(String inputPath) {
        System.out.println("🎖️ [MilitaryTask] Commencing global military choices and forces extraction pass...");

        try {
            // Stream the JSON text structure cleanly
            try (FileReader reader = new FileReader(inputPath)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

                // Navigate path: root -> settings -> options -> military
                if (!root.has("settings")) {
                    System.err.println("🔴 [MilitaryTask Error] Source file lacks the core 'settings' branch!");
                    return new JsonArray();
                }
                JsonObject settings = root.getAsJsonObject("settings");

                if (!settings.has("options")) {
                    System.err.println("🔴 [MilitaryTask Error] Core 'settings' node lacks the 'options' branch!");
                    return new JsonArray();
                }
                JsonObject options = settings.getAsJsonObject("options");

                if (!options.has("military")) {
                    System.err.println("⚠️ [MilitaryTask Warning] No 'military' array configuration found inside options. Skipping.");
                    return new JsonArray();
                }

                // Extract the raw military array data segment
                JsonArray militaryArray = options.getAsJsonArray("military");

                // Wrap it securely into a clean, flat root structure
                JsonObject militaryOutput = new JsonObject();
                militaryOutput.add("military", militaryArray);

                System.out.println("🟢 [MilitaryTask Success] Extracted army configurations registry");

                return militaryOutput;
            }
        } catch (Exception e) {
            System.err.println("🔴 [MilitaryTask Failure] Encountered an unhandled exception during extraction loop!");
            e.printStackTrace();
        }

        return new JsonArray();
    }
}
