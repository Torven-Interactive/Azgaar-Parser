package tvi.azgaar.parser.tasks;

import tvi.azgaar.parser.Task;

import com.google.gson.*;
import java.io.FileReader;
import java.util.ArrayList;


public class RoutesTask implements Task {

    @Override
    public JsonElement execute(String inputPath) {
        System.out.println("🛤️ [RoutesTask] Commencing commercial route extraction layer pass...");

        try {
            // Stream the JSON text structure cleanly
            try (FileReader reader = new FileReader(inputPath)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
                if (!root.has("pack")) {
                    System.err.println("🔴 [RoutesTask Error] Source file lacks the required 'pack' branch!");
                    return new JsonArray();
                }

                JsonObject pack = root.getAsJsonObject("pack");
                if (!pack.has("routes")) {
                    System.err.println("⚠️  [RoutesTask Warning] No 'routes' array found inside 'pack'. Skipping execution.");
                    return new JsonArray();
                }

                // Extract the raw routes array segment
                JsonArray routesArray = pack.getAsJsonArray("routes");

                // Wrap it securely into a clean, flat root routes document layer for modding
                JsonObject routesOutput = new JsonObject();
                routesOutput.add("routes", routesArray);

                System.out.println("🟢 [RoutesTask Success] Extracted commercial routes registry");

                return routesOutput;
            }

        } catch (Exception e) {
            System.err.println("🔴 [RoutesTask Failure] Encountered an unhandled exception during extraction loop!");
            e.printStackTrace();
        }

        return new JsonArray();
    }
}

